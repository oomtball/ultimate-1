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
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer.ProgramStateExplorer;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramStateTransition;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer.NeverClaimAutExplorer;

public class FixpointModelChecker {
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private final BoogieIcfgContainer mRcfgRoot;
	private final INestedWordAutomaton<CodeBlock, String> mNWA;
	
	private final ProgramStateExplorer mProgramStateExplorer;
	private final NeverClaimAutExplorer mNeverClaimAutExplorer;
	
	
	public FixpointModelChecker(final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNWA = null;
		
//		mLogger.info(rcfg);
		
		mProgramStateExplorer = new ProgramStateExplorer(rcfg, mLogger, mServices);
		mNeverClaimAutExplorer = null;
	}
	
	public FixpointModelChecker(final INestedWordAutomaton<CodeBlock, String> nwa, final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNWA = nwa;
//		mLogger.info(rcfg);
		
		mProgramStateExplorer = new ProgramStateExplorer(rcfg, mLogger, mServices);
		mNeverClaimAutExplorer = new NeverClaimAutExplorer(nwa);
	}
	
	public Set<ProgramState> getProgramInitialStates() {
		return mProgramStateExplorer.getInitialStates();
	}
	
	public Set<NeverState> getNeverInitialStates() {
		return mNeverClaimAutExplorer.getInitialStates();
	}
	public Collection<String> getNeverFinalStates() {
		return mNeverClaimAutExplorer.getFinalStates();
	}
	public List<OutgoingInternalTransition<CodeBlock, NeverState>> getNeverEnabledTrans(final NeverState n, 
			final ProgramState correspondingProgramState) {
		return mNeverClaimAutExplorer.getEnabledTrans(n, correspondingProgramState);
	}
	public List<ProgramStateTransition> getProgramEnabledTransByThreadID(final ProgramState p, final long tid) {
		return mProgramStateExplorer.getEnabledTransByThreadID(p, tid);
	}
	public List<Long> getProgramSafestOrder(final ProgramState p) {
		return mProgramStateExplorer.getSafestOrder(p);
	}
	public ProgramState doProgramTransition(final ProgramState p, final ProgramStateTransition trans) {
		return mProgramStateExplorer.doTransition(p, trans);
	}
	
	public NeverState doNeverTransition(final NeverState n, final OutgoingInternalTransition<CodeBlock, NeverState> edge) {
		return mNeverClaimAutExplorer.doTransition(n, edge);
	}
}
