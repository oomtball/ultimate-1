package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state;

import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.IState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.Valuation;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.ValuationState;

public abstract class ValuationState<S extends ValuationState<S>> implements IState<S> {
	protected Valuation mValuation;
	
	public Valuation getValuation() {
		return mValuation;
	}
	
	/**
	 * Deep copy all variables and their values.
	 * @return the copy.
	 */
	public Valuation getValuationFullCopy() {
		return mValuation.clone();
	}
	
	/**
	 * Deep copy local variables and their values.
	 * Shallow copy global variables and their values.
	 * @return the copy.
	 */
	public Valuation getValuationLocalCopy() {
		return mValuation.cloneLocals();
	}
}
