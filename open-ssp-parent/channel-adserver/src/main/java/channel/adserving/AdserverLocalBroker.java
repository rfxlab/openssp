package channel.adserving;

import java.util.Optional;

import com.atg.openssp.common.exception.BidProcessingException;
import com.atg.openssp.common.provider.AdProviderReader;

/**
 * This class acts as Broker to the local in-memory connector. It uses an in-memory database for bidding process
 * 
 * @author André Schmer
 *
 */
public class AdserverLocalBroker extends AbstractAdServerBroker {

	static AdProviderReader testableAdProvider = null;
	
	public static void setTestableAdProvider(AdProviderReader testableAdProvider) {
		AdserverLocalBroker.testableAdProvider = testableAdProvider;
	}
	

	// define database connector

	public AdserverLocalBroker() {
		// TODO
	}
	

	/**
	 * Connects to the database of local ad server.
	 * 
	 * @return Optional of {@link AdProviderReader}
	 * @throws BidProcessingException
	 */
	@Override
	public Optional<AdProviderReader> call() throws BidProcessingException {
		
		try {
			AdProviderReader adProvider = null;
			// TODO
			if(testableAdProvider != null) {
				adProvider = testableAdProvider;
			}
			
			return Optional.ofNullable(adProvider);
		} finally {
			
		}
	}
	
	

}
