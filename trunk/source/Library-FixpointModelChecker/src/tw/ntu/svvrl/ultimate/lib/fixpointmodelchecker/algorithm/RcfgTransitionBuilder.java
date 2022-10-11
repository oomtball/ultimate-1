package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.javabdd.*;

import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
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


public class RcfgTransitionBuilder{
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	private final Map<String, Map<DebugIdentifier, BoogieIcfgLocation>> mLocNodes;
	
	private final RcfgStatementExtractor mStatementExtractor;
	private final SimpleEvaluator mSimpleEvaluator;
	static BDDFactory bdd;
	public List<BDD> rcfgTrans = new ArrayList<BDD>();
	
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String be = "BinaryExpression";
	
	public RcfgTransitionBuilder(final BoogieIcfgContainer rcfg, final ILogger logger, final IUltimateServiceProvider services) {
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
		
		bdd = BDDFactory.init("j", 100000, 100000, false);
//		BDD result = bdd.one();
//		BDDDomain[] pre = bdd.extDomain(new int[]{16, 16});
//		BDDDomain[] post = bdd.extDomain(new int[]{16, 16});
//		
//		BDDBitVector x = bdd.buildVector(pre[0]);
//		BDDBitVector y = bdd.buildVector(pre[1]);
//		BDDBitVector v = bdd.constantVector(4, 11);
//		BDDBitVector z = y.add(v);
//		
//		BDDBitVector xp = bdd.buildVector(post[0]);
//		BDDBitVector yp = bdd.buildVector(post[1]);
//		for (int n = 0; n < x.size(); n++) {
////			result = result.and(xp.getBit(n).biimp(z.getBit(n)));
//			result = result.and(yp.getBit(n).biimp(v.getBit(n)));
//			result = result.and(xp.getBit(n).biimp(x.getBit(n)));
//		}
//		
//		BDDBitVector xv = bdd.constantVector(4, 3);
//		BDDBitVector yv = bdd.constantVector(4, 5);
//		
//		for (int n = 0; n < x.size(); n++) {
//			input = input.and(xpre.getBit(n).biimp(xv.getBit(n)));
//		}
//		for (int n = 0; n < x.size(); n++) {
//			input = input.and(ypre.getBit(n).biimp(yv.getBit(n)));
//		}
		
//		mLogger.info(result);
//		mLogger.info(input);
//		mLogger.info(result.restrict(input));
		
		// set up all pre-states for BDD transitions.
//		Set<SimpleState> preState = getPreStates(varAndValue);
		
		// calculate post states.
//		Set<SimpleStateTransition> transitions = calculatePost(preState, allAssignments);
		
		// translate transitions to BDD.
		int[] pam = findRcfgNeededBits(varAndValue).stream().mapToInt(Integer::intValue).toArray();
////		mLogger.info(Arrays.toString(pam)); 
//		
//		int[] pam2 = findPcNeededBits(allStatesWithPc).stream().mapToInt(Integer::intValue).toArray();
////		mLogger.info(Arrays.toString(pam2)); // [32, 32, 32, 32, 32, 32]
//		
//		bdd = BDDFactory.init("jdd", 10000, 10000, false);
		BDD result = bdd.zero();
		BDDDomain[] preVar = bdd.extDomain(pam); // represents different bdd variables
		BDDDomain[] postVar = bdd.extDomain(pam); // represents different bdd variables
		
		// test case
		BDD input = bdd.one();
		
		BDDBitVector turnPre = bdd.buildVector(preVar[0]);
		BDDBitVector xPre = bdd.buildVector(preVar[1]);
		BDDBitVector t1Pre = bdd.buildVector(preVar[2]);
		BDDBitVector flag1Pre = bdd.buildVector(preVar[3]);
		BDDBitVector tpost1Pre = bdd.buildVector(preVar[4]);
		BDDBitVector t2Pre = bdd.buildVector(preVar[5]);
		BDDBitVector flag2Pre = bdd.buildVector(preVar[6]);
		BDDBitVector f12Pre = bdd.buildVector(preVar[7]);
		BDDBitVector y2Pre = bdd.buildVector(preVar[8]);
		BDDBitVector f21Pre = bdd.buildVector(preVar[9]);
		BDDBitVector tpost0Pre = bdd.buildVector(preVar[10]);
		BDDBitVector y1Pre = bdd.buildVector(preVar[11]);
		BDDBitVector turnv = bdd.constantVector(2, 1);
		BDDBitVector xv = bdd.constantVector(2, 2);
		BDDBitVector t1v = bdd.constantVector(2, 0);
		BDDBitVector flag1v = bdd.constantVector(2, 1);
		BDDBitVector tpost1v = bdd.constantVector(2, 1);
		BDDBitVector t2v = bdd.constantVector(2, 0);
		BDDBitVector flag2v = bdd.constantVector(2, 0);
		BDDBitVector f12v = bdd.constantVector(2, 0);
		BDDBitVector y2v = bdd.constantVector(2, 0);
		BDDBitVector f21v = bdd.constantVector(2, 0);
		BDDBitVector tpost0v = bdd.constantVector(2, 0);
		BDDBitVector y1v = bdd.constantVector(2, 0);
		
		for (int n = 0; n < turnPre.size(); n++) {
			input = input.and(turnPre.getBit(n).biimp(turnv.getBit(n)));
		}
		for (int n = 0; n < xPre.size(); n++) {
			input = input.and(xPre.getBit(n).biimp(xv.getBit(n)));
		}
		for (int n = 0; n < t1Pre.size(); n++) {
			input = input.and(t1Pre.getBit(n).biimp(t1v.getBit(n)));
		}
		for (int n = 0; n < flag1Pre.size(); n++) {
			input = input.and(flag1Pre.getBit(n).biimp(flag1v.getBit(n)));
		}
		for (int n = 0; n < tpost1Pre.size(); n++) {
			input = input.and(tpost1Pre.getBit(n).biimp(tpost1v.getBit(n)));
		}
		for (int n = 0; n < t2Pre.size(); n++) {
			input = input.and(t2Pre.getBit(n).biimp(t2v.getBit(n)));
		}
		for (int n = 0; n < flag2Pre.size(); n++) {
			input = input.and(flag2Pre.getBit(n).biimp(flag2v.getBit(n)));
		}
		for (int n = 0; n < f12Pre.size(); n++) {
			input = input.and(f12Pre.getBit(n).biimp(f12v.getBit(n)));
		}
		for (int n = 0; n < y2Pre.size(); n++) {
			input = input.and(y2Pre.getBit(n).biimp(y2v.getBit(n)));
		}
		for (int n = 0; n < f21Pre.size(); n++) {
			input = input.and(f21Pre.getBit(n).biimp(f21v.getBit(n)));
		}
		for (int n = 0; n < tpost0Pre.size(); n++) {
			input = input.and(tpost0Pre.getBit(n).biimp(tpost0v.getBit(n)));
		}
		for (int n = 0; n < y1Pre.size(); n++) {
			input = input.and(y1Pre.getBit(n).biimp(y1v.getBit(n)));
		}
		
//		mLogger.info(input);
		
//		BDDDomain[] bddPc = bdd.extDomain(pam2); // represents pc of different threads
//		
//		Set<String> cpondsPc = allStatesWithPc.keySet();
//		
		for (String s : allAssignments.keySet()) {
			if (!varAndValue.containsKey(s)) {
				continue;
			}
			int needVar = 0;
			for (String s3 : varAndValue.keySet()) {
				if (s3.equals(s)) {
					break;
				}
				needVar++;
			}
			for (Pair<Expression, Pair<String, Pair<Integer, Integer>>> s2 : allAssignments.get(s)) {
				String var = s;
				Expression expr = s2.getFirst();
				String pcThread = s2.getSecond().getFirst();
				Pair<Integer, Integer> pc = s2.getSecond().getSecond();
				
				BDD transition = bdd.one();
				
				mLogger.info(var + " = " + expr + " at " + s2.getSecond());
				// deal with transitions
				if (expr.getClass().getSimpleName().equals(il)) {
					transition = caseIL(expr, preVar, postVar, needVar);
				}
				else if (expr.getClass().getSimpleName().equals(ie)) {
					transition = caseIE(expr, preVar, postVar, needVar, varAndValue);
				}
				else if (expr.getClass().getSimpleName().equals(be)) {
					transition = caseBE(expr, preVar, postVar, needVar, varAndValue);
				} 
				// deal with PCs
//				int count = 0;
//				for (String thread : cpondsPc) {
//					if (thread.equals(pcThread)) {
//						int prePcValue = pc.getFirst();
//						int postPcValue = pc.getSecond();
//						BDDBitVector prePc = bdd.buildVector(bddPc[count*2]);
//						BDDBitVector postPc = bdd.buildVector(bddPc[count*2+1]);
//						BDDBitVector preBl = bdd.constantVector(prePc.size(), prePcValue);
//						BDDBitVector postBl = bdd.constantVector(postPc.size(), postPcValue);
//						
//						
//						for (int i = 0; i < prePc.size(); i++) {
//							transition = transition.andWith(prePc.getBit(i).biimp(preBl.getBit(i)));
//						}
//						for (int i = 0; i < postPc.size(); i++) {
//							transition = transition.andWith(postPc.getBit(i).biimp(postBl.getBit(i)));
//						}
//						prePc.free();
//						postPc.free();
//						preBl.free();
//						postBl.free();
//					}
//					count++;
//				}
				mLogger.info(transition);
				mLogger.info(transition.restrict(input));
				rcfgTrans.add(transition);
//				result = result.orWith(transition);
//				break;
			}
//			break;
		}
//		mLogger.info(result);	
	}
	
	public List<BDD> getRcfgTrans() {
		return rcfgTrans;
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

	private Set<SimpleStateTransition> calculatePost(Set<SimpleState> preStates, Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments) {
		Set<SimpleStateTransition> tempTransitions = new HashSet<>();
		for (SimpleState s : preStates) {
			mLogger.info(s.getVars());
			for (String s2 : allAssignments.keySet()) {
				for(Pair<Expression, Pair<String, Pair<Integer, Integer>>> p : allAssignments.get(s2)) {
					String test = "IntegerLiteral";
					if (p.getFirst().getClass().getSimpleName().equals(test)) {
						List<Integer> postValues = mSimpleEvaluator.calculatePost(s.getVars(), s.getValues(), s2, p.getFirst(), p.getSecond());
						SimpleStateTransition tempTransition = new SimpleStateTransition(s.getVars(), s.getValues(), postValues);
						tempTransitions.add(tempTransition);
					}
				}
				break;
			}
			break;
		}
		return tempTransitions;
	}

	private List<Integer> findRcfgNeededBits(Map<String, Set<Integer>> varAndValue) {
		List<Integer> bitArray = new ArrayList<Integer>();
		for (String s : varAndValue.keySet()) {
//			mLogger.info(s);
			bitArray.add(Integer.toBinaryString(Collections.max(varAndValue.get(s))).length());
		}
		int maxv = Collections.max(bitArray);
		
		List<Integer> v = new ArrayList<Integer>(); 
		for (int i = 0; i < bitArray.size(); i++) {
			v.add((int) Math.pow(2, maxv));
		}
		return v;
	}
	
	private List<Integer> findPcNeededBits(Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc) {
		List<Integer> bitArray3 = new ArrayList<Integer>();
		for (String s : allStatesWithPc.keySet()) {
			 bitArray3.add(Integer.toBinaryString(Collections.max(allStatesWithPc.get(s).values())).length());
		}
		
		List<Integer> v2 = new ArrayList<Integer>(); 
		for (int i = 0; i < bitArray3.size(); i++) {
			v2.add((int) Math.pow(2, bitArray3.get(i)));
			v2.add((int) Math.pow(2, bitArray3.get(i)));
		}
		return v2;
	}

	private BDD caseIL(Expression expr, BDDDomain[] preVar, BDDDomain[] postVar, int needVar) {
		IntegerLiteral newExpr = (IntegerLiteral) expr;
		int expValue = Integer.parseInt(newExpr.getValue());
		BDD transition = bdd.one();
		BDDBitVector leftVar = bdd.buildVector(postVar[needVar]);
		BDDBitVector rightValue = bdd.constantVector(leftVar.size(), expValue);
		for (int j = 0; j < leftVar.size(); j++) {
			transition = transition.andWith(leftVar.getBit(j).biimp(rightValue.getBit(j)));
		}
//		for (int i = 0; i < preVar.length; i++) {
//			if (i == needVar) {
//				BDDBitVector leftVar = bdd.buildVector(postVar[i]);
//				BDDBitVector rightValue = bdd.constantVector(leftVar.size(), expValue);
//				for (int j = 0; j < leftVar.size(); j++) {
//					transition = transition.andWith(leftVar.getBit(j).biimp(rightValue.getBit(j)));
//				}
//			}
//			else {
//				BDDBitVector leftVar = bdd.buildVector(postVar[i]);
//				BDDBitVector rightVar = bdd.buildVector(preVar[i]);
//				for (int j = 0; j < leftVar.size(); j++) {
//					transition = transition.andWith(leftVar.getBit(j).biimp(rightVar.getBit(j)));
//				}
//			}
//		}
		leftVar.free();
		rightValue.free();
		return transition;
	}
	
	private BDD caseIE(Expression expr, BDDDomain[] preVar, BDDDomain[] postVar, int needVar, Map<String, Set<Integer>> varAndValue) {
		IdentifierExpression newExpr = (IdentifierExpression) expr;
		String rightv = newExpr.getIdentifier();
		BDD transition = bdd.one();
		
		int needVar2 = 0;
		for (String s3 : varAndValue.keySet()) {
			if (s3.equals(rightv)) {
				break;
			}
			needVar2++;
		}
		
		BDDBitVector leftVar = bdd.buildVector(postVar[needVar]);
		BDDBitVector rightVar = bdd.buildVector(preVar[needVar2]);
		
		for (int i = 0; i < leftVar.size(); i++) {
			transition = transition.andWith(leftVar.getBit(i).biimp(rightVar.getBit(i)));
		}
		leftVar.free();
		rightVar.free();
		return transition;
	}
	
	private BDD caseBE(Expression expr, BDDDomain[] preVar, BDDDomain[] postVar, int needVar, Map<String, Set<Integer>> varAndValue) {
		BinaryExpression newExpr = (BinaryExpression) expr;
		BDD transition = bdd.one();
		Expression binaryLeft = newExpr.getLeft();
		Expression binaryRight = newExpr.getRight();
		String leftClass = binaryLeft.getClass().getSimpleName();
		String rightClass = binaryRight.getClass().getSimpleName();
		
		BDDBitVector leftVar = bdd.buildVector(postVar[needVar]);
		String ope = newExpr.getOperator().toString();
//		mLogger.info(ope);
		
		BDDBitVector binaryLeftThing = null; 
		BDDBitVector binaryRightThing = null; 
		BDDBitVector rightVar = null;
		if (leftClass.equals(be)) {
//			leftBDD =  caseBE(left, bddVar, needVar, varAndValue);
		}
		if (rightClass.equals(be)) {
			
		}
		if (leftClass.equals(il) && rightClass.equals(il)) {
			IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
			IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
			binaryLeftThing = bdd.constantVector(leftVar.size(), Integer.parseInt(newBinaryLeft.getValue()));
			binaryRightThing = bdd.constantVector(leftVar.size(), Integer.parseInt(newBinaryRight.getValue()));
			rightVar = dealWithOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(ie) && rightClass.equals(il)) {
			IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
			IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
			
			int count = 0;
			for (String s : varAndValue.keySet()) {
				if (s.equals(newBinaryLeft.getIdentifier())) {
					break;
				}
				count++;
			}
			binaryLeftThing = bdd.buildVector(preVar[count]);
			binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
			rightVar = dealWithOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(il) && rightClass.equals(ie)) {
			IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
			IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
			
			int count = 0;
			for (String s : varAndValue.keySet()) {
				if (s.equals(newBinaryRight.getIdentifier())) {
					break;
				}
				count++;
			}
			binaryRightThing = bdd.buildVector(preVar[count]);
			binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
			rightVar = dealWithOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(ie) && rightClass.equals(ie)) {
			IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
			IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
			
			int count1 = 0;
			int count2 = 0;
			for (String s : varAndValue.keySet()) {
				if (s.equals(newBinaryLeft.getIdentifier())) {
					break;
				}
				count1++;
			}
			for (String s : varAndValue.keySet()) {
				if (s.equals(newBinaryRight.getIdentifier())) {
					break;
				}
				count2++;
			}
			binaryLeftThing = bdd.buildVector(preVar[count1]);
			binaryRightThing = bdd.buildVector(preVar[count2]);
			rightVar = dealWithOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		for (int i = 0; i < leftVar.size(); i++) {
			transition = transition.andWith(leftVar.getBit(i).biimp(rightVar.getBit(i)));
		}
		leftVar.free();
		rightVar.free();
		
		return transition;
	}
	
	private BDDBitVector dealWithOperator(BDDBitVector binaryLeftThing, BDDBitVector binaryRightThing, String ope) {
		BDDBitVector opeResult = null;
		String LOGICIFF = "LOGICIFF"; //  <->
		String LOGICIMPLIES = "LOGICIMPLIES"; // -> 
		String LOGICAND = "LOGICAND"; // &&
		String LOGICOR = "LOGICOR"; // ||
		String COMPLT = "COMPLT"; // <
		String COMPGT = "COMPGT";  // >
		String COMPLEQ = "COMPLEQ";  // <=
		String COMPGEQ = "COMPGEQ";  // >=
		String COMPEQ = "COMPEQ"; // ==
		String COMPNEQ = "COMPNEQ";  // !==
		String COMPPO = "COMPPO"; 
		String BITVECCONCAT = "BITVECCONCAT"; 
		String ARITHPLUS = "ARITHPLUS"; // +
		String ARITHMINUS = "ARITHMINUS"; // -
		String ARITHMUL = "ARITHMUL"; // *
		String ARITHDIV = "ARITHDIV"; // /
		String ARITHMOD = "ARITHMOD"; // %
//		mLogger.info(binaryLeftThing);
//		mLogger.info(binaryRightThing);
		if (ope.equals(ARITHPLUS)) {
			opeResult = binaryLeftThing.add(binaryRightThing);
		}
//		else if (ope.equals(ARITHMINUS)) {
//			opeResult = binaryLeftThing.sub(binaryRightThing);
//		}
		
		return opeResult;
	}
}