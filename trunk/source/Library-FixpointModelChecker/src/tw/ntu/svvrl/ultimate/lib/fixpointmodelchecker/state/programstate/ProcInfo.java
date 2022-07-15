package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate;

import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.threadstate.ThreadState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.Valuation;

/**
 * Keep the needed information on the procedure stack.
 * Including procedure names and their valuation copy when 
 * executing {@link CallStatement} and {@link Return}.
 */
public class ProcInfo {
	private final String mProcName;
	private Valuation mValuationRecord;
	
	/**
	 * <code> mValuationRecord </code> is initialized with null.
	 * Once the {@link CallStatement} is invoked, the valuation is recorded.
	 */
	public ProcInfo(final String procName) {
		mProcName = procName;
		mValuationRecord = null;
	}
	
	public String getProcName() {
		return mProcName;
	}
	
	public Valuation getValuationRecord() {
		return mValuationRecord;
	}
	
	public void setValuationRecord(Valuation v) {
		mValuationRecord = v;
	}
	
}
