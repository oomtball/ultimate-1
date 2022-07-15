package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.threadstate.ThreadState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.IState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.TransitionToolkit;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.nevertransitiontoolkit.NeverTransitionToolkit;

public class NeverState implements IState<NeverState>{
	private final String mStateName;
	private final List<OutgoingInternalTransition<CodeBlock, NeverState>> mTranss = new ArrayList<>();
	
	private final boolean mIsInitial;
	private final boolean mIsFinal;
	
	public NeverState(String stateName, boolean isInitial, boolean isFinal) {
		mStateName = stateName;
		mIsInitial = isInitial;
		mIsFinal = isFinal;
	}
	
	public boolean isFinal() {
		return mIsFinal;
	}
	
	public List<OutgoingInternalTransition<CodeBlock, NeverState>> getTranss() {
		return mTranss;
	} 
	
	public void addTrans(OutgoingInternalTransition<CodeBlock, NeverState> trans) {
		mTranss.add(trans);
	}
	
	public String getName() {
		return mStateName;
	}
	
	
	@Override
	public boolean equals(NeverState anotherState) {
		return mStateName.equals(anotherState.getName()) ? true : false;
	}
	
	@Override
	public String toString() {
		return "NeverState@" + mStateName;
	}
}
