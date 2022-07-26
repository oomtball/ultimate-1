package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.FixpointModelChecker;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.NilSelfLoop;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramStateTransition;

import jdd.bdd.BDD;

public class FixpointVerifier {
	private final FixpointModelChecker mFMC;
	private final ILogger mLogger;
	Set<Pair<ProgramState, NeverState>> fixpointState = new HashSet<>();
	Set<Pair<ProgramState, NeverState>> tempState = new HashSet<>();
	final BDD bdd;
	
	boolean isFixpoint;
	boolean neverPropertyHolds;
	
	public FixpointVerifier(final ILogger logger, final FixpointModelChecker fmc) {
		mFMC = fmc;
		mLogger = logger;
		isFixpoint = false;
		neverPropertyHolds = false;
		bdd = new BDD(1000,100);
	}
	
	public void run() {
		final Set<ProgramState> pInitials = mFMC.getProgramInitialStates();
		final Set<NeverState> nInitials = mFMC.getNeverInitialStates();
		
		// debug part
		int shutdown = 0;
		
		for(final ProgramState p : pInitials) {
			for(final NeverState n : nInitials)
			{
				final Pair<ProgramState, NeverState> s0 = new Pair<>(p, n);
				tempState.add(s0);
				while (!isFixpoint) {
					calculateNext(tempState);
					// debug part 
					shutdown = shutdown + 1;
//					mLogger.info(tempState);
				}
			}
		}
//		mLogger.info(fixpointState);
		// take the last item in postStateSpace and check if there any accepting state in this fixpoint
		for (final Pair<ProgramState, NeverState> p2 : fixpointState) {
			if (p2.getSecond().isFinal()) {
				neverPropertyHolds = true;
			}
		}
		
		if (neverPropertyHolds) {
			mLogger.info("Specifications do not hold.");
		}
		else {
			mLogger.info("All specifications holds.");
		}
	}
	
	private void calculateNext(Set<Pair<ProgramState, NeverState>> s) {
		final Set<Pair<ProgramState, NeverState>> tempPostState = new HashSet<>(); //store the next post state computed in this loop
		final Set<Pair<ProgramState, NeverState>> lastPostState = s; //store the next post state computed in this loop
		
		for (final Pair<ProgramState, NeverState> p : s) {
			final List<OutgoingInternalTransition<CodeBlock, NeverState>> nxt 
			= mFMC.getNeverEnabledTrans(getNeverState(p), getProgramState(p));
			
//			mLogger.info(nxt);
		
			for(final OutgoingInternalTransition<CodeBlock, NeverState> t : nxt) { // for each edge
				final NeverState nxtNs = mFMC.doNeverTransition(getNeverState(p), t); // next step for property edge
				final Pair<ProgramState, NeverState> succ = new Pair<>(getProgramState(p), nxtNs);
//				tempPostState.add(succ);
				
				List<Long> order = mFMC.getProgramSafestOrder(getProgramState(succ)); 
				for(Long i : order) { // for each thread 
					final List<ProgramStateTransition> nxt2 
					= mFMC.getProgramEnabledTransByThreadID(getProgramState(succ), i);
					
					NilSelfLoop nilSelfLoop = mFMC.checkNeedOfSelfLoop(getProgramState(succ));
					if(nilSelfLoop != null) {
						nxt2.add(nilSelfLoop);
					}
					
					for(final ProgramStateTransition t2 : nxt2) { // next step for every thread
						final ProgramState nxtPs = mFMC.doProgramTransition(getProgramState(succ), t2); 
						final Pair<ProgramState, NeverState> succ2 = new Pair<>(nxtPs, getNeverState(succ));
						tempPostState.add(succ2);
					}
				}
			}
		}
		
		// debug part
		mLogger.info(tempPostState);
		mLogger.info("");
		
//		 check if there exist a fixpoint and record it
		if (tempPostState.equals(lastPostState)) {
			isFixpoint = true;
			fixpointState = tempPostState;
			mLogger.info("Fixpoint of (System X (complement of property))" + fixpointState + " is found.");
			return;
		}
		else {
			tempState = tempPostState;
			return;
		}
	}
	private ProgramState getProgramState(final Pair<ProgramState, NeverState> sPair) {
//		mLogger.info(setOfPair.iterator().next().getFirst());
		return sPair.getFirst();
	}
	
	private NeverState getNeverState(final Pair<ProgramState, NeverState> sPair) {
		return sPair.getSecond();
	}
}
