package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.javabdd.*;

import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
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
	Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignmentStatements;
	Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>> allAssumeStatements;
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
		
		allAssignmentStatements = new HashMap<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>>();
		allAssumeStatements = new HashSet<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>();
		
		bdd = bf;
		v = _v; // represents different bdd variables
		vprime = _vprime; // represents different bdd variables
		varOrder = vo;
		
		// set up the program counters for each thread -- pc.
		Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc = computeProgramCounter(mLocNodes);

		// get all statements we need to build BDD transition.
		getAllExpression(allStatesWithPc);
		
		// set up BDDs of the program counter
		threadOrder = allStatesWithPc.keySet();
		
		int[] pam2 = findPcNeededBits(allStatesWithPc).stream().mapToInt(Integer::intValue).toArray();
		rcfgPc = bdd.extDomain(pam2); // represents pc of different threads
		rcfgPcPrime = bdd.extDomain(pam2); // represents pcprime of different threads
		
		buildRcfgTrans(allAssignmentStatements, threadOrder);
		
//		BDD test = bdd.zero();
//		for (BDD b : rcfgTrans) {
//			mLogger.info(b);
//			test = test.or(b);
//		}
//		mLogger.info(test);
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
	
	private void getAllExpression(Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc){
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
										if (!allAssignmentStatements.keySet().contains(lhs2.getIdentifier())) {
											Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>> newSet = new HashSet<>();
											newSet.add(tempPair3);
											allAssignmentStatements.put(lhs2.getIdentifier(), newSet);
										}
										else {
											Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>> oldSet = allAssignmentStatements.get(lhs2.getIdentifier());
											oldSet.add(tempPair3);
											allAssignmentStatements.replace(lhs2.getIdentifier(), oldSet);
										}
										break;
									}
								}
							}
							else if (stmt instanceof AssumeStatement) {
								AssumeStatement temp = (AssumeStatement) stmt;
								Pair<Integer, Integer> tempPair = new Pair<>(allStatesWithPc.get(thread).get(tempState), allStatesWithPc.get(thread).get(edge.getTarget()));
								Pair<String, Pair<Integer, Integer>> tempPair2 = new Pair<>(thread, tempPair);
								Pair<Expression, Pair<String, Pair<Integer, Integer>>> tempPair3 = new Pair<>(temp.getFormula(), tempPair2);
								allAssumeStatements.add(tempPair3);
							}
						}
					}
				}
			}
		}
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
	
	private void buildRcfgTrans(Map<String, Set<Pair<Expression, Pair<String, Pair<Integer, Integer>>>>> allAssignments, Set<String> threadOrder) {
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

}