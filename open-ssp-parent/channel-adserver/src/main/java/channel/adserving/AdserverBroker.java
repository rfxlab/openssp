package channel.adserving;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.http.client.utils.URIBuilder;

import com.atg.openssp.common.core.broker.AbstractBroker;
import com.atg.openssp.common.core.connector.JsonGetConnector;
import com.atg.openssp.common.exception.BidProcessingException;
import com.atg.openssp.common.provider.AdProviderReader;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;

/**
 * This class acts as Broker to the adserver connector. It uses a get-connector to connect direct to the adserver and retrieves the result from it.
 * 
 * @author André Schmer
 *
 */
public class AdserverBroker extends AbstractAdServerBroker {

	private final Gson gson;

	private final JsonGetConnector jsonGetConnector;

	// define endpoint
	private static final String scheme = "http";
	private static final String host = "doamin.com";
	private static final String path = "/path/to/target";

	final URIBuilder uriBuilder;

	public AdserverBroker() {
		uriBuilder = new URIBuilder().setCharset(StandardCharsets.UTF_8).setScheme(scheme).setHost(host).setPath(path);
		jsonGetConnector = new JsonGetConnector();
		gson = new Gson();
	}

	/**
	 * Connects to the Adserver.
	 * 
	 * @return Optional of {@link AdProviderReader}
	 * @throws BidProcessingException
	 */
	public Optional<AdProviderReader> call() throws BidProcessingException {
		final Stopwatch stopwatch = Stopwatch.createStarted();
		try {

			final String result = jsonGetConnector.connect(uriBuilder);
			final AdProviderReader adProvider = gson.fromJson(result, AdservingCampaignProvider.class);
			stopwatch.stop();
			return Optional.ofNullable(adProvider);
		} finally {
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
		}
	}
	
	

}
