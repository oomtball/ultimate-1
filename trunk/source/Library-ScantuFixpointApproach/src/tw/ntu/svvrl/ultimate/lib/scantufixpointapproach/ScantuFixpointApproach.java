package tw.ntu.svvrl.ultimate.lib.scantufixpointapproach;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressAwareTimer;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.IAbstractState;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.variables.IProgramVarOrConst;
import tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.algorithm.FixpointEngine;

public class ScantuFixpointApproach {
	
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	public <STATE extends IAbstractState<STATE>> ScantuFixpointApproach(final IIcfg<? extends IcfgLocation> rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger
		mServices = services;
		mLogger = logger;
		final IProgressAwareTimer timer;
		
		final FixpointEngine<STATE, IcfgEdge, IProgramVarOrConst, IcfgLocation> fxpe = new FixpointEngine<>();
		final boolean result = fxpe.run(rcfg, mLogger);
		mLogger.info(result);
	}

}
