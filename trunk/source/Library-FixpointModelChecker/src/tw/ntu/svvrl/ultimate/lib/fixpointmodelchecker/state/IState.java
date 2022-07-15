package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state;

import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.IState;

public interface IState<S extends IState<S>> {
	
	public boolean equals(S anotherS);
}
