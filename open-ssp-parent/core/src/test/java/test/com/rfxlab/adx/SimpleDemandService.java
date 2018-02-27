package test.com.rfxlab.adx;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.demand.BidExchange;
import com.atg.openssp.common.demand.ResponseContainer;
import com.atg.openssp.common.demand.Supplier;
import com.atg.openssp.common.exception.InvalidBidException;
import com.atg.openssp.common.provider.AdProviderReader;
import com.atg.openssp.core.exchange.Auction;
import com.atg.openssp.core.exchange.channel.rtb.DemandBroker;
import com.atg.openssp.core.exchange.channel.rtb.DemandExecutorServiceFacade;
import com.atg.openssp.core.exchange.channel.rtb.DemandService;
import com.google.gson.Gson;

import auction.RequestResponseHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidresponse.model.BidResponse;

public class SimpleDemandService implements Callable<AdProviderReader> {

	private static final Logger log = LoggerFactory.getLogger(DemandService.class);

	private final SessionAgent agent;

	/**
	 * 
	 * @param {@link
	 * 			SessionAgent}
	 */
	public SimpleDemandService(final SessionAgent agent) {
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
	@Override
	public AdProviderReader call() throws Exception {
		AdProviderReader adProvider = null;
		final float impFloor = 0.88f;
		final float dealFloor1 = 3.f;
		final float dealFloor2 = 2.8f;
		final String currency = "USD";

		final String deal_id_1 = "998877";
		final String deal_id_2 = "998866";

		// bidrequest1
		// bidresponse, price in USD
		final float bidPrice1 = 3.5f;
		final BidRequest bidRequest1 = RequestResponseHelper.createRequest(impFloor, dealFloor1, currency, deal_id_1, 1)
				.build();
		// final BidResponse bidResponse =
		// RequestResponseHelper.createResponse(bidPrice1, currency, deal_id_1);
		try {
			Supplier supplier1 = new Supplier();
			supplier1.setShortName("dsp1");
			supplier1.setSupplierId(1l);

			String json = executeGet("http://devdsp.hadarone.com/dsp-sim/DemandService");
			BidResponse bidResponse = new Gson().fromJson(json, BidResponse.class);
			final ResponseContainer responseContainer = new ResponseContainer(supplier1, bidResponse);
			BidExchange bidExchange = agent.getBidExchange();
			bidExchange.setBidRequest(supplier1, bidRequest1);
			bidExchange.setBidResponse(responseContainer.getSupplier(), responseContainer.getBidResponse());
			adProvider = Auction.auctioneer(bidExchange);
		} catch (final InvalidBidException e) {
			log.error("{} {}", agent.getRequestid(), e.getMessage());
		}
		return adProvider;
	}

	private String executeGet(String url) throws IOException {
		OkHttpClient client = new OkHttpClient();
		// code request code here

		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();

	}

}