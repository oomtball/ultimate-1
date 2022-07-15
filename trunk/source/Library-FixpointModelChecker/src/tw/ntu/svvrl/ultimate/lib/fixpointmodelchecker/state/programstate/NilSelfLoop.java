package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate;

/**
 * If the {@link ProgramState} has no successor, attach a nil self-loop.
 * @see the definition of synchronous product.
 */
public class NilSelfLoop extends ProgramStateTransition {
	public NilSelfLoop() {
		
	}

	@Override
	public String getCStatement() {
		return "Stay put.";
	}
}
