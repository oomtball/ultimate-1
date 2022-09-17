package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.RcfgStatementExtractor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

public class TransitionBuilder{
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	private final Map<String, Map<DebugIdentifier, BoogieIcfgLocation>> mLocNodes;
	
	private final RcfgStatementExtractor mStatementExtractor;
	private final SimpleEvaluator mSimpleEvaluator;
	
	public TransitionBuilder(final BoogieIcfgContainer rcfg, final ILogger logger, final IUltimateServiceProvider services) {
		mServices = services;
		mLogger = logger;
		
		mLocNodes = rcfg.getProgramPoints();
		mStatementExtractor = new RcfgStatementExtractor();
		mSimpleEvaluator = new SimpleEvaluator(mLogger);
		
		// set up the program counters for each thread -- pc.
		Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc = computeProgramCounter(mLocNodes);

		// get all assignments we need to build BDD transition.
		Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments = getAllAssignments(allStatesWithPc);
		
		// set up all possible values for each variable. (filter)
		Map<String, Set<Integer>> varAndValue = getVarAndValue(allAssignments);
		
		// set up all pre-states for BDD transitions.
		Set<SimpleState> preStates = getPreStates(varAndValue);
		
		for (SimpleState s : preStates) {
			for (String s2 : allAssignments.keySet()) {
				for(Pair<Expression, Pair<String, Pair<Integer, Integer>>> p : allAssignments.get(s2)) {
					SimpleState postState = mSimpleEvaluator.calculatePost(s, s2, p.getFirst(), p.getSecond());
					break;
				}
				break;
			}
			break;
		}
		// calculate post states.
		
		// translate transitions to BDD.
		
	}
	
	private Map<String, Map<BoogieIcfgLocation, Integer>> computeProgramCounter(Map<String, Map<DebugIdentifier, BoogieIcfgLocation>> mLocNodes){
		Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc = new HashMap<>();
		int maxThreadStates = -1; // is used to decide the bit number of program counters 
		for (String s : mLocNodes.keySet()) { // each thread
			Map<BoogieIcfgLocation, Integer> temp2 = new HashMap<>();
			int tempPc = 0;
			Map<DebugIdentifier, BoogieIcfgLocation> temp = mLocNodes.get(s);
			for (DebugIdentifier d : temp.keySet()) {
				temp2.put(temp.get(d), tempPc);
				allStatesWithPc.put(temp.get(d).getProcedure(), temp2);
				tempPc += 1;
//				allRcfgStates.add(temp.get(d));
			}
			if (tempPc > maxThreadStates) {
				maxThreadStates = tempPc;
			}
		}
		return allStatesWithPc;
	}
	
	private Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> getAllAssignments(Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc){
		Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments = new HashMap<>();
		for (String thread : allStatesWithPc.keySet()) {
			Map<BoogieIcfgLocation, Integer> tempStateMap = allStatesWithPc.get(thread);
			for (BoogieIcfgLocation tempState : tempStateMap.keySet()) {
				for (IcfgEdge edge : tempState.getOutgoingEdges()) {
//					mLogger.info(edge);
					String ss = "class de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence";
					if (edge.getClass().toString().equals(ss)) {
						// all assignment
						final List<Statement> statements = mStatementExtractor.process(edge);
						for (Statement stmt : statements) {
							if (stmt instanceof AssignmentStatement) {
								AssignmentStatement temp = (AssignmentStatement) stmt;
								for (LeftHandSide lhs : temp.getLhs()) {
									VariableLHS lhs2 = (VariableLHS) lhs;
									for (Expression rhs : temp.getRhs()) {
										Pair<Integer, Integer> tempPair = new Pair<>(allStatesWithPc.get(thread).get(tempState), allStatesWithPc.get(thread).get(edge.getTarget()));
										Pair<String, Pair<Integer, Integer>> tempPair2 = new Pair<>(thread, tempPair);
										Pair<Expression, Pair<String, Pair<Integer, Integer>>> tempPair3 = new Pair<>(rhs, tempPair2);
										if (!allAssignments.keySet().contains(lhs2.getIdentifier())) {
											Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>> newSet = new HashSet<>();
											newSet.add(tempPair3);
											allAssignments.put(lhs2.getIdentifier(), newSet);
										}
										else {
											Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>> oldSet = allAssignments.get(lhs2.getIdentifier());
											oldSet.add(tempPair3);
											allAssignments.replace(lhs2.getIdentifier(), oldSet);
										}
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return allAssignments;
	}

	private Map<String, Set<Integer>> getVarAndValue(Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments){
		Map<String, Set<Integer>> varAndValue = new HashMap<>();
		String s1 = "~turn~0";
		Set<Integer> temp1 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s1, temp1);
//		String s2 = "main_#t~pre2";
//		Set<Integer> temp2 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s2, temp2);
//		String s3 = "main_#res";
//		Set<Integer> temp3 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s3, temp3);
//		String s4 = "#res.base";
//		Set<Integer> temp4 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s4, temp4);
		String s5 = "~t1~0";
		Set<Integer> temp5 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s5, temp5);
//		String s6 = "main_#t~pre4";
//		Set<Integer> temp6 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s6, temp6);
		String s7 = "~t2~0";
		Set<Integer> temp7 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s7, temp7);
		String s8 = "~f12~0";
		Set<Integer> temp8 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s8, temp8);
//		String s9 = "#NULL.offset";
//		Set<Integer> temp9 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s9, temp9);
		String s10 = "~y2~0";
		Set<Integer> temp10 = new HashSet<>(Arrays.asList(0, 1, 2));
		varAndValue.put(s10, temp10);
		String s11 = "~f21~0";
		Set<Integer> temp11 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s11, temp11);
		String s12 = "~y1~0";
		Set<Integer> temp12 = new HashSet<>(Arrays.asList(0, 1, 2));
		varAndValue.put(s12, temp12);
//		String s13 = "#res.offset";
//		Set<Integer> temp13 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s13, temp13);
//		String s14 = "~_.offset";
//		Set<Integer> temp14 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s14, temp14);
//		String s15 = "#t~ret8";
//		Set<Integer> temp15 = new HashSet<>(Arrays.asList(0));
//		varAndValue.put(s15, temp15);
		String s16 = "~x~0";
		Set<Integer> temp16 = new HashSet<>(Arrays.asList(0, 1, 2));
		varAndValue.put(s16, temp16);
		String s17 = "~flag1~0";
		Set<Integer> temp17 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s17, temp17);
		String s18 = "#t~post1";
		Set<Integer> temp18 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s18, temp18);
		String s19 = "~flag2~0";
		Set<Integer> temp19 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s19, temp1);
//		String s20 = "#pthreadsForks";
//		Set<Integer> temp20 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s20, temp20);
//		String s21 = "~_.base";
//		Set<Integer> temp21 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s21, temp21);
//		String s22 = "#NULL.base";
//		Set<Integer> temp22 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s22, temp22);
		String s23 = "#t~post0";
		Set<Integer> temp23 = new HashSet<>(Arrays.asList(0, 1));
		varAndValue.put(s23, temp23);
//		String s24 = "#valid";
//		Set<Integer> temp24 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s24, temp24);
		return varAndValue;
	}
		
	private Set<SimpleState> getPreStates(Map<String, Set<Integer>> varAndValue){
		Set<SimpleState> preStates = new HashSet<>();
		List<List<Integer>> beforeProduct = new ArrayList<>();
		for (String s : varAndValue.keySet()) {
			List<Integer> temp = new ArrayList<>();
			for (Integer i : varAndValue.get(s)) {
				temp.add(i);
			}
			beforeProduct.add(temp);
		}

		for (List<Integer> l : computeCombinations(beforeProduct)) {
			SimpleState tempState = new SimpleState(varAndValue.keySet(), l);
			preStates.add(tempState);
		}
		return preStates;
	}
	
	private static <T> List<List<T>> computeCombinations(List<List<T>> lists) {
	    List<List<T>> combinations = Arrays.asList(Arrays.asList());
	    for (List<T> list : lists) {
	        List<List<T>> extraColumnCombinations = new ArrayList<>();
	        for (List<T> combination : combinations) {
	            for (T element : list) {
	                List<T> newCombination = new ArrayList<>(combination);
	                newCombination.add(element);
	                extraColumnCombinations.add(newCombination);
	            }
	        }
	        combinations = extraColumnCombinations;
	    }
	    return combinations;
	}

}