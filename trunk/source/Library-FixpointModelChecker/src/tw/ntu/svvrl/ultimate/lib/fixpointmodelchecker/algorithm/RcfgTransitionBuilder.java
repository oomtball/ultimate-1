package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.javabdd.*;

import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BooleanLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.CLocation;
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
	
	static BDDFactory bdd;
	private final RcfgStatementExtractor mStatementExtractor;
	private final SimpleEvaluator mSimpleEvaluator;
//	static BDDFactory bdd;
	public List<BDD> rcfgTrans = new ArrayList<BDD>();
	public List<Pair<String, Pair<Integer, Integer>>> rcfgTransPc = new ArrayList<Pair<String, Pair<Integer, Integer>>>();
	public List<BDD> initialTrans = new ArrayList<BDD>();
	
	BDDDomain[] v; // represents different bdd variables
	BDDDomain[] vprime; // represents different bdd variables
	BDDDomain[] rcfgPc;
	BDDDomain[] rcfgPcPrime;
	Set<String> varOrder;
	Set<String> threadOrder;
	
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String be = "BinaryExpression";
	
	public RcfgTransitionBuilder(final BoogieIcfgContainer rcfg, final ILogger logger, final IUltimateServiceProvider services, 
			BDDFactory bf, BDDDomain[] _v, BDDDomain[] _vprime, Set<String> vo) {
		mServices = services;
		mLogger = logger;
		
		mLocNodes = rcfg.getProgramPoints();
		mStatementExtractor = new RcfgStatementExtractor();
		mSimpleEvaluator = new SimpleEvaluator(mLogger);
		
		bdd = bf;
		v = _v; // represents different bdd variables
		vprime = _vprime; // represents different bdd variables
		varOrder = vo;
		
		// set up the program counters for each thread -- pc.
		Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc = computeProgramCounter(mLocNodes);

		// get all assignments we need to build BDD transition.
		Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments = getAllAssignments(allStatesWithPc);
		
		// set up BDDs of the program counter
		threadOrder = allStatesWithPc.keySet();
		
		int[] pam2 = findPcNeededBits(allStatesWithPc).stream().mapToInt(Integer::intValue).toArray();
		rcfgPc = bdd.extDomain(pam2); // represents pc of different threads
		rcfgPcPrime = bdd.extDomain(pam2); // represents pcprime of different threads
		
		// test case
		BDD input = setInput2();
		
		BDDPairing bp = bdd.makePair();
		bp.set(v, vprime);
		BDD input2 = input.replace(bp);
		
		buildRcfgTrans(allAssignments, input, threadOrder);
		
//		BDD test = bdd.zero();
//		for (BDD b : rcfgTrans) {
//			mLogger.info(b);
//			test.orWith(b);
//			mLogger.info(test);
//		}
		
//		Set<String> cpondsPc = allStatesWithPc.keySet();	
		
		// set up all pre-states for BDD transitions.
//		Set<SimpleState> preState = getPreStates(varAndValue);
		
		// calculate post states.
//		Set<SimpleStateTransition> transitions = calculatePost(preState, allAssignments);
	}
	
	public List<BDD> getRcfgTrans() {
		return rcfgTrans;
	}
	
	public List<BDD> getInitialTrans() {
		return initialTrans;
	}
	
	public List<Pair<String, Pair<Integer, Integer>>> getRcfgTransPc() {
		return rcfgTransPc;
	}
	
	public BDDDomain[] getRcfgPc() {
		return rcfgPc;
	}
	
	public BDDDomain[] getRcfgPcPrime() {
		return rcfgPcPrime;
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

	private List<Integer> findPcNeededBits(Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc) {
		List<Integer> bitArray3 = new ArrayList<Integer>();
		for (String s : allStatesWithPc.keySet()) {
			 bitArray3.add(Integer.toBinaryString(Collections.max(allStatesWithPc.get(s).values())).length());
		}
		
		List<Integer> v2 = new ArrayList<Integer>(); 
		for (int i = 0; i < bitArray3.size(); i++) {
			v2.add((int) Math.pow(2, bitArray3.get(i)));
		}
		return v2;
	}

	private BDD setInput() {
		BDD input = bdd.one();
		
		BDDBitVector turnPre = bdd.buildVector(v[0]);
		BDDBitVector xPre = bdd.buildVector(v[1]);
		BDDBitVector t1Pre = bdd.buildVector(v[2]);
		BDDBitVector flag1Pre = bdd.buildVector(v[3]);
		BDDBitVector tpost1Pre = bdd.buildVector(v[4]);
		BDDBitVector t2Pre = bdd.buildVector(v[5]);
		BDDBitVector flag2Pre = bdd.buildVector(v[6]);
		BDDBitVector f12Pre = bdd.buildVector(v[7]);
		BDDBitVector y2Pre = bdd.buildVector(v[8]);
		BDDBitVector f21Pre = bdd.buildVector(v[9]);
		BDDBitVector tpost0Pre = bdd.buildVector(v[10]);
		BDDBitVector y1Pre = bdd.buildVector(v[11]);
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
		
		return input;
	}
	
	private BDD setInput2() {
		BDD input = bdd.one();
		
		BDDBitVector x = bdd.buildVector(v[0]);
		BDDBitVector xv = bdd.constantVector(2, 0);
		
		for (int n = 0; n < x.size(); n++) {
			input = input.and(x.getBit(n).biimp(xv.getBit(n)));
		}
		
		return input;
	}
	
	private void buildRcfgTrans(Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments, BDD input, 
			Set<String> threadOrder) {
		for (String s : allAssignments.keySet()) {
			if (!varOrder.contains(s)) {
				continue;
			}
			int needVar = calculateIndex(s);
			int tempMin = 10000;
			BDD initialTran = bdd.one();
			for (Pair<Expression, Pair<String, Pair<Integer, Integer>>> s2 : allAssignments.get(s)) {
				String var = s;
				Expression expr = s2.getFirst();
				String pcThread = s2.getSecond().getFirst();
				Pair<Integer, Integer> pc = s2.getSecond().getSecond();
				
				BDD transition = bdd.one();
				
//				mLogger.info(var + " = " + expr + " at " + s2.getSecond());
				// deal with transitions
				if (expr.getClass().getSimpleName().equals(il)) {
					transition = caseIL(expr, needVar, bdd);
				}
				else if (expr.getClass().getSimpleName().equals(ie)) {
					transition = caseIE(expr, needVar, varOrder, bdd);
				}
				else if (expr.getClass().getSimpleName().equals(be)) {
					transition = caseBE(expr, needVar, varOrder, bdd);
				} 
				// deal with PCs
				int count = 0;
				for (String thread : threadOrder) {
					if (thread.equals(pcThread)) {
						int prePcValue = pc.getFirst();
						int postPcValue = pc.getSecond();
						BDDBitVector prePc = bdd.buildVector(rcfgPc[count]);
						BDDBitVector postPc = bdd.buildVector(rcfgPcPrime[count]);
						BDDBitVector preBl = bdd.constantVector(prePc.size(), prePcValue);
						BDDBitVector postBl = bdd.constantVector(postPc.size(), postPcValue);
						
						
						for (int i = 0; i < prePc.size(); i++) {
							transition = transition.andWith(prePc.getBit(i).biimp(preBl.getBit(i)));
						}
						for (int i = 0; i < postPc.size(); i++) {
							transition = transition.andWith(postPc.getBit(i).biimp(postBl.getBit(i)));
						}
						prePc.free();
						postPc.free();
						preBl.free();
						postBl.free();
					}
					count++;
				}
//				mLogger.info("transition : " + transition);
				rcfgTrans.add(transition);
				rcfgTransPc.add(s2.getSecond());
				
				for (String sss : expr.getPayload().getAnnotations().keySet()) {
					CLocation cl = (CLocation) expr.getPayload().getAnnotations().get(sss);
					if (cl.getStartLine() < tempMin) {
						tempMin = cl.getStartLine();
						initialTran = transition;
					}
				}
			}
			initialTrans.add(initialTran);
		}
	}
	
	private BDD caseIL(Expression expr, int needVar, BDDFactory bdd) {
		IntegerLiteral newExpr = (IntegerLiteral) expr;
		int expValue = Integer.parseInt(newExpr.getValue());
		BDD transition = bdd.one();
		BDDBitVector leftVar = bdd.buildVector(vprime[needVar]);
		BDDBitVector rightValue = bdd.constantVector(leftVar.size(), expValue);
		for (int j = 0; j < leftVar.size(); j++) {
			transition = transition.andWith(leftVar.getBit(j).biimp(rightValue.getBit(j)));
		}
//		for (int i = 0; i < v.length; i++) {
//			if (i == needVar) {
//				BDDBitVector leftVar = bdd.buildVector(vprime[i]);
//				BDDBitVector rightValue = bdd.constantVector(leftVar.size(), expValue);
//				for (int j = 0; j < leftVar.size(); j++) {
//					transition = transition.andWith(leftVar.getBit(j).biimp(rightValue.getBit(j)));
//				}
//				leftVar.free();
//				rightValue.free();
//			}
//			else {
//				BDDBitVector leftVar = bdd.buildVector(vprime[i]);
//				BDDBitVector rightVar = bdd.buildVector(v[i]);
//				for (int j = 0; j < leftVar.size(); j++) {
//					transition = transition.andWith(leftVar.getBit(j).biimp(rightVar.getBit(j)));
//				}
//				leftVar.free();
//				rightVar.free();
//			}
//		}
		leftVar.free();
		rightValue.free();
		return transition;
	}
	
	private BDD caseIE(Expression expr, int needVar, Set<String> varOrder, BDDFactory bdd) {
		IdentifierExpression newExpr = (IdentifierExpression) expr;
		String rightv = newExpr.getIdentifier();
		BDD transition = bdd.one();
		
		int needVar2 = calculateIndex(rightv);
		
		BDDBitVector leftVar = bdd.buildVector(vprime[needVar]);
		BDDBitVector rightVar = bdd.buildVector(v[needVar2]);
		
		for (int i = 0; i < leftVar.size(); i++) {
			transition = transition.andWith(leftVar.getBit(i).biimp(rightVar.getBit(i)));
		}
		leftVar.free();
		rightVar.free();
		return transition;
	}
	
	private BDD caseBE(Expression expr, int needVar, Set<String> varOrder, BDDFactory bdd) {
		BinaryExpression newExpr = (BinaryExpression) expr;
		BDD transition = bdd.one();
		Expression binaryLeft = newExpr.getLeft();
		Expression binaryRight = newExpr.getRight();
		String leftClass = binaryLeft.getClass().getSimpleName();
		String rightClass = binaryRight.getClass().getSimpleName();
		
		BDDBitVector leftVar = bdd.buildVector(vprime[needVar]);
		String ope = newExpr.getOperator().toString();
//		mLogger.info(ope);
		
		BDDBitVector binaryLeftThing = null; 
		BDDBitVector binaryRightThing = null; 
		BDDBitVector rightVar = null;
		if (leftClass.equals(il)) {
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryLeftThing = bdd.constantVector(leftVar.size(), Integer.parseInt(newBinaryLeft.getValue()));
				binaryRightThing = bdd.constantVector(leftVar.size(), Integer.parseInt(newBinaryRight.getValue()));
			}
			else if (rightClass.equals(ie)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;		
				int count = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[count]);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
			}
			else if (rightClass.equals(be)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				binaryRightThing = caseBEforBvec(binaryRight);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
			}
			rightVar = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(ie)) {
			IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
			int count = calculateIndex(newBinaryLeft.getIdentifier());
			binaryLeftThing = bdd.buildVector(v[count]);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int count2 = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[count2]);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
			}
			rightVar = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(be)) {
			binaryLeftThing = caseBEforBvec(binaryLeft);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int count = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[count]);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
			}
			rightVar = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
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

	public BDD rcfgGetPost(BDD input, BDD transition) {
		BDD post = bdd.one();
		
		BDDPairing bp = bdd.makePair();
		bp.set(v, vprime);
		BDD input2 = input.replace(bp);
		List<Integer> needDomains = new ArrayList<Integer>();
		for (int i : transition.restrict(input).scanSetDomains()) {
			needDomains.add(i-12);
		}
		for (int i = 0; i < vprime.length; i++) {
			if (needDomains.contains(i)) {
				BDDBitVector test1 = bdd.buildVector(vprime[i]);
				BDDBitVector test2 = bdd.constantVector(test1.size(), transition.restrict(input).scanVar(vprime[i]));
				
				for (int n = 0; n < test1.size(); n++) {
					post = post.and(test1.getBit(n).biimp(test2.getBit(n)));
				}
			}
			else {
				BDDBitVector test1 = bdd.buildVector(vprime[i]);
				BDDBitVector test2 = bdd.constantVector(test1.size(), input2.scanVar(vprime[i]));
				
				for (int n = 0; n < test1.size(); n++) {
					post = post.and(test1.getBit(n).biimp(test2.getBit(n)));
				}
			}
		}
		return post;
	}

	private BDDBitVector dealWithAssignmentOperator(BDDBitVector binaryLeftThing, BDDBitVector binaryRightThing, String ope) {
		BDDBitVector opeResult = null;
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
		else if (ope.equals(ARITHMINUS)) {
			opeResult = binaryLeftThing.sub(binaryRightThing);
		}
		
		return opeResult;
	}
	
	private BDDBitVector caseBEforBvec(Expression expr) {
		BDDBitVector result = null;
		BinaryExpression newBe = (BinaryExpression) expr;
		Expression binaryLeft = newBe.getLeft();
		Expression binaryRight = newBe.getRight();
		String ope = newBe.getOperator().toString();
		String leftClass = binaryLeft.getClass().getSimpleName();
		String rightClass = binaryRight.getClass().getSimpleName();
		BDDBitVector binaryLeftThing = null; 
		BDDBitVector binaryRightThing = null; 
		if (leftClass.equals(il)) {
			if (rightClass.equals(ie)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int index  = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[index]);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(be)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				binaryRightThing = caseBEforBvec(binaryRight);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
		}
		else if (leftClass.equals(ie)) {
			IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
			int index  = calculateIndex(newBinaryLeft.getIdentifier());
			binaryLeftThing = bdd.buildVector(v[index]);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int index2  = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[index2]);
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
		}
		else if (leftClass.equals(be)) {
			binaryLeftThing = caseBEforBvec(binaryLeft);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int index2  = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[index2]);
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
				result = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			}
		}
		return result;
	}
	
	private int calculateIndex(String var) {
		int count = 0;
		for (String s : varOrder) {
			if (s.equals(var)) {
				break;
			}
			count++;
		}
		return count;
	}

//	private Set<SimpleState> getPreStates(Map<String, Set<Integer>> varAndValue){
//	Set<SimpleState> preStates = new HashSet<>();
//	List<List<Integer>> beforeProduct = new ArrayList<>();
//	for (String s : varAndValue.keySet()) {
//		List<Integer> temp = new ArrayList<>();
//		for (Integer i : varAndValue.get(s)) {
//			temp.add(i);
//		}
//		beforeProduct.add(temp);
//	}
//
//	for (List<Integer> l : computeCombinations(beforeProduct)) {
//		SimpleState tempState = new SimpleState(varAndValue.keySet(), l);
//		preStates.add(tempState);
//	}
//	return preStates;
//}
	
//	private static <T> List<List<T>> computeCombinations(List<List<T>> lists) {
//    List<List<T>> combinations = Arrays.asList(Arrays.asList());
//    for (List<T> list : lists) {
//        List<List<T>> extraColumnCombinations = new ArrayList<>();
//        for (List<T> combination : combinations) {
//            for (T element : list) {
//                List<T> newCombination = new ArrayList<>(combination);
//                newCombination.add(element);
//                extraColumnCombinations.add(newCombination);
//            }
//        }
//        combinations = extraColumnCombinations;
//    }
//    return combinations;
//}
	
//	private Set<SimpleStateTransition> calculatePost(Set<SimpleState> preStates, Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments) {
//	Set<SimpleStateTransition> tempTransitions = new HashSet<>();
//	for (SimpleState s : preStates) {
//		mLogger.info(s.getVars());
//		for (String s2 : allAssignments.keySet()) {
//			for(Pair<Expression, Pair<String, Pair<Integer, Integer>>> p : allAssignments.get(s2)) {
//				String test = "IntegerLiteral";
//				if (p.getFirst().getClass().getSimpleName().equals(test)) {
//					List<Integer> postValues = mSimpleEvaluator.calculatePost(s.getVars(), s.getValues(), s2, p.getFirst(), p.getSecond());
//					SimpleStateTransition tempTransition = new SimpleStateTransition(s.getVars(), s.getValues(), postValues);
//					tempTransitions.add(tempTransition);
//				}
//			}
//			break;
//		}
//		break;
//	}
//	return tempTransitions;
//}
}