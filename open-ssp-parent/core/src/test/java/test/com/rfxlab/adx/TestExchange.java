package test.com.rfxlab.adx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.atg.openssp.common.core.exchange.ExchangeExecutorServiceFacade;
import com.atg.openssp.common.exception.RequestException;
import com.atg.openssp.common.provider.AdProviderReader;
import com.atg.openssp.core.cache.type.ConnectorCache;
import com.atg.openssp.core.exchange.ChannelFactory;
import com.atg.openssp.core.exchange.ExchangeServer;
import com.atg.openssp.core.exchange.RequestSessionAgent;
import com.atg.openssp.core.exchange.channel.rtb.DemandService;
import com.atg.openssp.core.exchange.channel.rtb.OpenRtbConnector;
import com.atg.openssp.core.system.LocalContext;
import com.google.gson.Gson;
import com.rfxlab.ssp.core.system.vertx.SspDataLoader;

import channel.adserving.AdservingService;
import junit.framework.Assert;
import util.math.FloatComparator;

public class TestExchange {

	public static void main(String[] args) throws Exception {
		try {
			// initing cache data
			SspDataLoader.init();

			final MockHttpServletRequest request = new MockHttpServletRequest();
			final MockHttpServletResponse response = new MockHttpServletResponse();

			request.addParameter("site", "1");
			request.addParameter("w", "1024");
			request.addParameter("h", "768");
			request.addParameter("prot", "3");
			request.addParameter("domain", "atg.com");
			request.addParameter("sd", "0");
			request.addParameter("prot", "2.2");
			request.addParameter("page", "atg.com");
			request.addParameter("mimes", "video/mp4");

			RequestSessionAgent agent = null;
			try {
				agent = new RequestSessionAgent(request, response);
			} catch (final RequestException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}

			SimpleDemandService demandService = new SimpleDemandService(agent);
			System.out.println(demandService);

			List<Callable<AdProviderReader>> callables = new ArrayList<>();
			callables.add(demandService);

			try {
				AdProviderReader winner = demandService.call();
				System.out.println(new Gson().toJson(winner));
			} catch (final Exception e) {
				e.printStackTrace();
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static Future<AdProviderReader> validate(final Future<AdProviderReader> a,
			final Future<AdProviderReader> b) {
		try {
			if (b.get() == null) {
				return a;
			}
			if (a.get() == null) {
				return b;
			}

			if (FloatComparator.greaterThanWithPrecision(a.get().getPriceEur(), b.get().getPriceEur())) {
				return a;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return b;
	}
}
