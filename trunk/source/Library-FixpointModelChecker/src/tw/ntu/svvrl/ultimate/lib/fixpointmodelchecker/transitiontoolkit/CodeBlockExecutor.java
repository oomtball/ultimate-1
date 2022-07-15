package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit;

import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.IState;

public abstract class CodeBlockExecutor<S extends IState<S>> {
	protected S mCurrentState;
	protected CodeBlock mCodeBlock;

	protected abstract boolean checkEnabled();

}
