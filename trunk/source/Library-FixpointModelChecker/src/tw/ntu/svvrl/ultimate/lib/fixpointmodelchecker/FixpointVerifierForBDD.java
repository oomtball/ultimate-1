package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import jdd.bdd.BDD;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;

public class FixpointVerifierForBDD {
	private final FixpointModelCheckerForBDD mFMCBDD;
	private final ILogger mLogger;
	
	public FixpointVerifierForBDD(final ILogger logger, final FixpointModelCheckerForBDD fmcbdd) {
		mFMCBDD = fmcbdd;
		mLogger = logger;
	}
	
	public void run() {
//		for(final NeverState n : mFMCBDD.getNeverInitialStates()) {
//			for (final OutgoingInternalTransition<CodeBlock, NeverState> s : mFMCBDD.getNeverTrans(n)) {
//				mLogger.info(s.getLetter());
//			}
//		}
	}
}
