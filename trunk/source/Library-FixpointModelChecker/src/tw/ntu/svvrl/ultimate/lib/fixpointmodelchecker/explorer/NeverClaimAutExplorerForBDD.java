package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverStateFactory;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.nevertransitiontoolkit.NeverTransitionToolkit;
import jdd.bdd.BDD;

public class NeverClaimAutExplorerForBDD {
	final INestedWordAutomaton<CodeBlock, String> mNwa;
	final Map<String, NeverState> mName2State;
	
	// BDD part 
	BDD propertyBDD;
	int allBDD = 0;
	
	public NeverClaimAutExplorerForBDD(INestedWordAutomaton<CodeBlock, String> nwa) {
		mNwa = nwa;
		NeverStateFactory neverStatefactory = new NeverStateFactory(nwa);
		mName2State = neverStatefactory.createName2State();
		
		// BDD part
		propertyBDD = new BDD(1000,100);
		final int p1 = propertyBDD.createVar();
		final int p2 = propertyBDD.createVar();
		final int p3 = propertyBDD.createVar();
		
		int initialState;
		initialState = propertyBDD.and(p1, p2);
		propertyBDD.ref(initialState);
		
		int tempState = initialState;
		propertyBDD.ref(tempState);
		propertyBDD.deref(initialState);
		
		
	}
	
	public int createTransBDD(int tempThisState) {
		int tempTrans;
		int tempPostState;
		for(final NeverState n : getInitialStates()) { // start from all last state
			for (final OutgoingInternalTransition<CodeBlock, NeverState> s : getAllTrans(n)) { // for each transition
				
				// calculate post state for each transition
				
				// post state
				final int tempP1 = propertyBDD.createVar();
				final int tempP2 = propertyBDD.createVar();
				tempPostState = propertyBDD.and(tempP1, tempP2);
				
				// new transition for this state
				tempTrans = propertyBDD.and(tempThisState, tempPostState);
				
			}
		}
		return 0;
	}
	
	public Set<NeverState> getInitialStates() {
		final Set<NeverState> result = new HashSet<>();
		final Set<String> initialNames = mNwa.getInitialStates();
		for(final String initialName : initialNames) {
			result.add(mName2State.get(initialName));
		}
		return result;
	}
	public Collection<String> getFinalStates() {
		return mNwa.getFinalStates();
	}
	
	public List<OutgoingInternalTransition<CodeBlock, NeverState>> getAllTrans(final NeverState n){
		List<OutgoingInternalTransition<CodeBlock, NeverState>> allTrans = new ArrayList<>();
		allTrans = n.getTranss();
		return allTrans;
	}
	public List<OutgoingInternalTransition<CodeBlock, NeverState>> getEnabledTrans(final NeverState n, final ProgramState correspondingProgramState) {
		List<OutgoingInternalTransition<CodeBlock, NeverState>> enabledTrans = new ArrayList<>();
		
		for(final OutgoingInternalTransition<CodeBlock, NeverState> edge : n.getTranss()) {
			final NeverTransitionToolkit transitionToolkit
			= new NeverTransitionToolkit(edge, n, correspondingProgramState);
			if (transitionToolkit.checkTransEnabled()) {
				enabledTrans.add(edge);
			}
		}
		return enabledTrans;
	}
	

	public NeverState doTransition(final NeverState n, final OutgoingInternalTransition<CodeBlock, NeverState> edge) {
		final NeverTransitionToolkit transitionToolkit 
			= new NeverTransitionToolkit(edge, n, null);
		return (NeverState) transitionToolkit.doTransition();
	}
}
