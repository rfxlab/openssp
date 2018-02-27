package channel.adserving;

import java.util.Optional;

import com.atg.openssp.common.core.broker.AbstractBroker;
import com.atg.openssp.common.exception.BidProcessingException;
import com.atg.openssp.common.provider.AdProviderReader;

public abstract class AbstractAdServerBroker extends AbstractBroker{

	public abstract Optional<AdProviderReader> call() throws BidProcessingException;
}
