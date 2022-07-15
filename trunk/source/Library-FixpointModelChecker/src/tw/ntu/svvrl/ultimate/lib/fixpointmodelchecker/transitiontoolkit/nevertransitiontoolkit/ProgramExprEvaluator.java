package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.nevertransitiontoolkit;

import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.ExprEvaluator;

public class ProgramExprEvaluator extends ExprEvaluator<ProgramState> {
	
	public ProgramExprEvaluator(final ProgramState state) {
		super(state);
		mFuncInitValuationInfo = null;
		mFuncValuation = null;
	}
}
