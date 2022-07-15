package tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.algorithm;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;

public class FixpointEngine{
	
	private boolean acceptProperty;
	public FixpointEngine(final ILogger logger, final IUltimateServiceProvider services) {
		
		acceptProperty = false;
	}
	
	public boolean run(final BoogieIcfgContainer rcfg, final ILogger logger) {
		logger.info("Starting buchi fixpoint engine");
		logger.info(rcfg);

		return acceptProperty;
	}
}