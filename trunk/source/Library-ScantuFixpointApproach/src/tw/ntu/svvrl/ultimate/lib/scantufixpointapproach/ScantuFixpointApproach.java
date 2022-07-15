package tw.ntu.svvrl.ultimate.lib.scantufixpointapproach;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressAwareTimer;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.IAbstractState;
import tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.algorithm.FixpointEngine;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;

public class ScantuFixpointApproach {
	
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	public <STATE extends IAbstractState<STATE>> ScantuFixpointApproach(final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger
		mServices = services;
		mLogger = logger;
		final IProgressAwareTimer timer;
		
		final FixpointEngine fxpe = new FixpointEngine(mLogger, mServices);
		final boolean result = fxpe.run(rcfg, mLogger);
		mLogger.info(result);
	}
	
}
