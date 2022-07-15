package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit;

import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.IState;

/**
 * This class handle all issues about a transition(edge) and the statements on it.
 *
 */
public abstract class TransitionToolkit<S extends IState<S>> {
	protected CodeBlockExecutor<S> mCodeBlockExecutor;
	
	public boolean checkTransEnabled() {
		return mCodeBlockExecutor.checkEnabled();
	}
	
	protected abstract S doTransition();
}
