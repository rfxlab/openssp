package channel.adserving;

import java.util.Optional;
import java.util.concurrent.Callable;

import com.atg.openssp.common.core.broker.AbstractBroker;
import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.provider.AdProviderReader;

import util.math.FloatComparator;

/**
 * @author André Schmer
 *
 */
public class AdservingService implements Callable<AdProviderReader> {

	private final AbstractAdServerBroker broker;

	private final SessionAgent agent;

	/**
	 * 
	 * @param agent
	 *            {@link SessionAgent}
	 */
	public AdservingService(final SessionAgent agent) {
		this.agent = agent;
		broker = new AdserverBroker();
		broker.setSessionAgent(agent);
	}
	
	/**
	 * 
	 * @param agent
	 *            {@link SessionAgent}
	 */
	public AdservingService(final SessionAgent agent, boolean localBroker) {
		this.agent = agent;
		broker = new AdserverLocalBroker();
		broker.setSessionAgent(agent);
	}

	/**
	 * Calls the Broker for Adserver.
	 * 
	 * @return {@link AdProviderReader}
	 */
	@Override
	public AdProviderReader call() throws Exception {
		try {
			final Optional<AdProviderReader> adProvider = broker.call();

			if (adProvider.isPresent()) {
				final AdProviderReader provider = adProvider.get();

				// check if the ad response price is greator or equal the floorprice
				float bidfloorPrice = agent.getParamValues().getVideoad().getBidfloorPrice();
				if (FloatComparator.greaterOrEqual(provider.getPriceEur(), bidfloorPrice)) {
					return provider;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
