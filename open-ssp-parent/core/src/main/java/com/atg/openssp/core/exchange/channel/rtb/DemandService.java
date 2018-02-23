package com.atg.openssp.core.exchange.channel.rtb;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atg.openssp.common.cache.CurrencyCache;
import com.atg.openssp.common.core.connector.JsonPostConnector;
import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.demand.BidExchange;
import com.atg.openssp.common.demand.ResponseContainer;
import com.atg.openssp.common.demand.Supplier;
import com.atg.openssp.common.exception.InvalidBidException;
import com.atg.openssp.common.provider.AdProviderReader;
import com.atg.openssp.core.cache.type.ConnectorCache;
import com.atg.openssp.core.exchange.Auction;
import com.atg.openssp.core.exchange.BidRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import auction.RequestResponseHelper;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.Impression;
import openrtb.bidresponse.model.BidResponse;


/**
 * @author André Schmer
 *
 */
public class DemandService implements Callable<AdProviderReader> {

	private static final Logger log = LoggerFactory.getLogger(DemandService.class);

	private final SessionAgent agent;

	/**
	 * 
	 * @param {@link
	 * 			SessionAgent}
	 */
	public DemandService(final SessionAgent agent) {
		this.agent = agent;
	}

	/**
	 * Calls the DSP. Collects the results of bidrequest, storing the results after
	 * validating into a {@link BidExchange} object.
	 * 
	 * <p>
	 * Principle of work is the following:
	 * <ul>
	 * <li>Loads the connectors as callables from the cache
	 * {@link DemandBroker}</li>
	 * <li>Invoke the callables due to the {@link DemandExecutorServiceFacade}</li>
	 * <li>For every result in the list of futures, the response will be validated
	 * {@link OpenRtbVideoValidator} and stored in a {@link BidExchange} object</li>
	 * <li>From the set of reponses in the {@link BidExchange} a bidding winner will
	 * be calculated in the Auction service {@link Auction}</li>
	 * </ul>
	 * <p>
	 * 
	 * @return {@link AdProviderReader}
	 * @throws Exception
	 */
	public AdProviderReader call() throws Exception {
		return process1();
	}
	

	public AdProviderReader process0() throws Exception {
		AdProviderReader adProvider = null;
		try {
			final List<DemandBroker> connectors = loadSupplierConnectors();

			final List<Future<ResponseContainer>> futures = DemandExecutorServiceFacade.instance.invokeAll(connectors);

			futures.parallelStream().filter(Objects::nonNull).forEach(future -> {
				try {
					final ResponseContainer responseContainer = future.get();
					// final boolean valid =
					// OpenRtbVideoValidator.instance.validate(agent.getBidExchange().getBidRequest(responseContainer.getSupplier()),
					// responseContainer
					// .getBidResponse());
					//
					// if (false == valid) {
					// LogFacade.logException(this.getClass(), ExceptionCode.E003,
					// agent.getRequestid(), responseContainer.getBidResponse().toString());
					// return;// important!
					// }
					agent.getBidExchange().setBidResponse(responseContainer.getSupplier(),
							responseContainer.getBidResponse());
				} catch (final ExecutionException e) {
					log.error("ExecutionException {} {}", agent.getRequestid(), e.getMessage());
				} catch (final InterruptedException e) {
					log.error("InterruptedException {} {}", agent.getRequestid(), e.getMessage());
				} catch (final CancellationException e) {
					log.error("CancellationException {} {}", agent.getRequestid(), e.getMessage());
				}
			});

			try {
				adProvider = Auction.auctioneer(agent.getBidExchange());
			} catch (final InvalidBidException e) {
				log.error("{} {}", agent.getRequestid(), e.getMessage());
			}
		} catch (final Exception e) {
			e.printStackTrace();
			log.error(" InterruptedException (outer) {} {}", agent.getRequestid(), e.getMessage());
		}

		return adProvider;
	}

	/**
	 * Loads the connectors for supplier from the cache.
	 * <p>
	 * Therefore it prepares the {@link BidRequest} for every connector, which is a
	 * representant to a demand connection.
	 * 
	 * @return a {@code List} with {@link DemandBroker}
	 * 
	 * @link SessionAgent
	 */
	private List<DemandBroker> loadSupplierConnectors() {
		final List<OpenRtbConnector> connectorList = ConnectorCache.instance.getAll();
		final List<DemandBroker> connectors = new ArrayList<>();

		final BidRequest bidRequest = BidRequestBuilder.build(agent);
		connectorList.stream().filter(b -> b.getSupplier().getActive() == 1).forEach(connector -> {

			final DemandBroker demandBroker = new DemandBroker(connector.getSupplier(), connector, agent);
			if (bidRequest.getImp().get(0).getBidfloor() > 0) {
				final Impression imp = bidRequest.getImp().get(0);
				// floorprice in EUR -> multiply with rate to get target
				// currency therfore floorprice currency is always the same
				// as supplier currency
				imp.setBidfloor(bidRequest.getImp().get(0).getBidfloor()
						* CurrencyCache.instance.get(connector.getSupplier().getCurrency()));
				imp.setBidfloorcur(connector.getSupplier().getCurrency());
			}

			bidRequest.setTest(connector.getSupplier().getUnderTest());
			demandBroker.setBidRequest(bidRequest);
			agent.getBidExchange().setBidRequest(connector.getSupplier(), bidRequest);
			connectors.add(demandBroker);
		});

		return connectors;
	}

	
	public AdProviderReader process1() throws Exception {
		AdProviderReader adProvider = null;
		final float impFloor = 0.81f;
		final float dealFloor1 = 3.f;
		final float dealFloor2 = 2.8f;
		final String currency = "USD";

		final String deal_id_1 = "998877";
		final String deal_id_2 = "998866";

		Supplier supplier1 = new Supplier();
		supplier1.setShortName("dsp1");
		supplier1.setSupplierId(1l);
		supplier1.setContentType("application/json");
		supplier1.setOpenRtbVersion("2.4");

		
		// bidrequest1
		// bidresponse, price in USD
		final float bidPrice1 = 3.5f;
		final BidRequest bidRequest1 = RequestResponseHelper.createRequest(impFloor, dealFloor1, currency, deal_id_1, 1)
				.build();
		Gson gson = new GsonBuilder().setVersion(Double.valueOf(supplier1.getOpenRtbVersion())).create();
		final String jsonBidrequest = gson.toJson(bidRequest1, BidRequest.class);
		// final BidResponse bidResponse =
		// RequestResponseHelper.createResponse(bidPrice1, currency, deal_id_1);
		try {
		
			Header[] headers = new Header[2];
			headers[0] = new BasicHeader("x-openrtb-version", supplier1.getOpenRtbVersion());
			headers[1] = new BasicHeader("ContentType", supplier1.getContentType());
			URI endpoint = new URI("http://devdsp.hadarone.com/dsp-sim/DemandService");
			final HttpPost httpPost = new HttpPost(endpoint);
			httpPost.setHeaders(headers);

			JsonPostConnector jsonPostConnector = new JsonPostConnector();
			
			String json = jsonPostConnector.connect(new StringEntity(jsonBidrequest, ContentType.APPLICATION_JSON),httpPost);

			BidResponse bidResponse = new Gson().fromJson(json, BidResponse.class);
			final ResponseContainer responseContainer = new ResponseContainer(supplier1, bidResponse);
			
			BidExchange bidExchange = agent.getBidExchange();
			bidExchange.setBidRequest(supplier1, bidRequest1);
			bidExchange.setBidResponse(responseContainer.getSupplier(), responseContainer.getBidResponse());
			
			adProvider = Auction.auctioneer(bidExchange);
		} catch (final InvalidBidException e) {
			log.error("{} {}", agent.getRequestid(), e.getMessage());
		}
		System.out.println(new Gson().toJson(adProvider));
		return adProvider;
	}

}
