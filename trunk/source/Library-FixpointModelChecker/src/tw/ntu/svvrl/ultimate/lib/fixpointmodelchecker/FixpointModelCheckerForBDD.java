package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker;

import java.util.List;
import java.util.Collection;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer.ProgramStateExplorerForBDD;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer.NeverClaimAutExplorerForBDD;

public class FixpointModelCheckerForBDD {
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private final BoogieIcfgContainer mRcfgRoot;
	private final INestedWordAutomaton<CodeBlock, String> mNWA;
	
	private final ProgramStateExplorerForBDD mProgramStateExplorerForBDD;
	private final NeverClaimAutExplorerForBDD mNeverClaimAutExplorerForBDD;
	
	
	public FixpointModelCheckerForBDD(final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNWA = null;
		
//		mLogger.info(rcfg);
		
		mProgramStateExplorerForBDD = new ProgramStateExplorerForBDD(rcfg, mLogger, mServices);
		mNeverClaimAutExplorerForBDD = null;
	}
	
	public FixpointModelCheckerForBDD(final INestedWordAutomaton<CodeBlock, String> nwa, final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNWA = nwa;
//		mLogger.info(rcfg);
		
		mProgramStateExplorerForBDD = new ProgramStateExplorerForBDD(rcfg, mLogger, mServices);
		mNeverClaimAutExplorerForBDD = new NeverClaimAutExplorerForBDD(nwa);
	}
	public Set<NeverState> getNeverInitialStates() {
		return mNeverClaimAutExplorerForBDD.getInitialStates();
	}
	public List<OutgoingInternalTransition<CodeBlock, NeverState>> getNeverTrans(final NeverState n) {
		return mNeverClaimAutExplorerForBDD.getAllTrans(n);
	}
}
