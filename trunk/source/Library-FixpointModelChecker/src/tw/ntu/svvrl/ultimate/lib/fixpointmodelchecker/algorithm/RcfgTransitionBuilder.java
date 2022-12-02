package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javabdd.*;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.CallStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.HavocStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression.Operator;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Summary;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

public class RcfgTransitionBuilder{
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	static BDDFactory bdd;
	
	private List<IcfgLocation> foundLoc;
	private List<IcfgEdge> foundEdge;
	
	private Map<String, Integer> arrayWithLength;
	
	private List<Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>>> assignmentStatementWithPc;
	private List<Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>>> assumeStatementWithPc;
	private List<Pair<CallStatement, Pair<String, Pair<Integer, Integer>>>> callStatementWithPc;
	private List<Pair<HavocStatement, Pair<String, Pair<Integer, Integer>>>> havocStatementWithPc;
	
	private final AssignmentStatementEvalator mAssignmentStatementEvalator;
	private final AssumeStatementEvalator mAssumeStatementEvalator;
	
//	public List<Integer> finishPcForEachThread;
	public Map<String, Integer> needInitialBefore;
	public Map<String, Set<Integer>> finishPcForEachThread;
	
//	static BDDFactory bdd;
	public List<BDD> rcfgTrans = new ArrayList<BDD>();
	public List<Pair<String, Pair<Integer, Integer>>> rcfgTransPc = new ArrayList<Pair<String, Pair<Integer, Integer>>>();
	public List<BDD> initialTrans = new ArrayList<BDD>();
	
	List<Integer> needMaxPc;
	Map<String, Integer> needMaxPc2;
	Map<IcfgLocation, Integer> locationWithPc;
	
	BDDDomain[] v; // represents different bdd variables
	BDDDomain[] vprime; // represents different bdd variables
	BDDDomain[] rcfgPc;
	BDDDomain[] rcfgPcPrime;
	Set<String> varOrder;
	Set<String> threadOrder;
	List<Pair<String, Expression[]>> threadAndInput;
	
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String be = "BinaryExpression";
	String tshort = "#t~short";
	
	public RcfgTransitionBuilder(final BoogieIcfgContainer rcfg, final ILogger logger, final IUltimateServiceProvider services, 
			BDDFactory _bdd, BDDDomain[] _v, BDDDomain[] _vprime, Set<String> vo, List<Pair<String, Expression[]>> _threadAndInput, 
			Map<String, Integer> _arrayWithLength) {
		mServices = services;
		mLogger = logger;
		varOrder = vo;
		threadOrder = rcfg.getProcedureEntryNodes().keySet();
		threadAndInput = _threadAndInput;
		arrayWithLength = _arrayWithLength;
		// get edge for each thread in order
		Map<String, List<IcfgEdge>> threadToAllEdges = getThreadToEdges(rcfg.getProcedureEntryNodes());
		assignmentStatementWithPc = new ArrayList<Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>>>();
		assumeStatementWithPc = new ArrayList<Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>>>();
		callStatementWithPc = new ArrayList<Pair<CallStatement, Pair<String, Pair<Integer, Integer>>>>();
		havocStatementWithPc = new ArrayList<Pair<HavocStatement, Pair<String, Pair<Integer, Integer>>>>();

//		finishPcForEachThread = new ArrayList<Integer>();
		needInitialBefore = new HashMap<String, Integer>(); 
		finishPcForEachThread = new HashMap<String, Set<Integer>>();
		needMaxPc2 = new HashMap<String, Integer>();
		locationWithPc = new HashMap<IcfgLocation, Integer>();
		getAsWithPc(threadToAllEdges);
		getNeedInitialBefore();
		mLogger.info(needInitialBefore);
		needMaxPc = test();
//		mLogger.info(Arrays.toString(needMaxPc.toArray()));
		
		bdd = _bdd;
		v = _v; // represents different bdd variables
		vprime = _vprime; // represents different bdd variables
		int[] pam2 = needMaxPc.stream().mapToInt(Integer::intValue).toArray();
		rcfgPc = bdd.extDomain(pam2); // represents pc of different threads
		rcfgPcPrime = bdd.extDomain(pam2); // represents pcprime of different threads
		
		mAssignmentStatementEvalator = new AssignmentStatementEvalator(logger, services, 
				bdd, _v, _vprime, varOrder);
		mAssumeStatementEvalator = new AssumeStatementEvalator(logger, services, 
				bdd, _v, _vprime, varOrder);
		
		mLogger.info(Arrays.toString(varOrder.toArray()));
		buildRcfgTrans();
//		buildSelfLoop();
//		mLogger.info(finishPcForEachThread);
	}
	
	private void dfsRecursive(IcfgLocation initialLoc) {
		if (foundLoc.contains(initialLoc)) {
			return;
		}
		foundLoc.add(initialLoc);
		
		if (initialLoc.getOutgoingNodes().isEmpty()) {
			return;
		}
		else {
			for (IcfgLocation il : initialLoc.getOutgoingNodes()) {
				for (IcfgEdge ie : initialLoc.getOutgoingEdges()) {
					if (ie.getTarget().equals(il) && !foundEdge.contains(ie)) {
						foundEdge.add(ie);
					}
				}
				dfsRecursive(il);
			}
		}
	}
	
	
	private Map<String, List<IcfgEdge>> getThreadToEdges(Map<String, BoogieIcfgLocation> initialStates) {
		Map<String, List<IcfgEdge>> threadToEdges = new HashMap<String, List<IcfgEdge>>();
		for (String thread : threadOrder) {
			foundLoc = new ArrayList<IcfgLocation>();
			foundEdge = new ArrayList<IcfgEdge>();
			
			dfsRecursive(initialStates.get(thread));
			
			threadToEdges.put(thread, foundEdge);
		}
//		mLogger.info(threadToEdges);
		return threadToEdges;
	}
	
	
	private void getAsWithPc(Map<String, List<IcfgEdge>> threadToAllEdges) {
		for (String thread : threadOrder){
			int pcCount =  0;
			Set<Integer> finishPc = new HashSet<Integer>();
			for (IcfgEdge edge : threadToAllEdges.get(thread)) {
				IcfgLocation source = edge.getSource();
				IcfgLocation target = edge.getTarget();
				if (edge instanceof StatementSequence) {
					StatementSequence ss = (StatementSequence) edge;
					List<Statement> statements = ss.getStatements();
					for (Statement stmt : statements) {
						if (stmt instanceof AssignmentStatement) {
							AssignmentStatement as = (AssignmentStatement) stmt;
							Boolean isContained = true;
							for (LeftHandSide lhs : as.getLhs()) {
								VariableLHS lhs2 = (VariableLHS) lhs;
								if (!varOrder.contains(lhs2.getIdentifier())) {
									isContained = false;
								}
							}
							if (isContained) {
								if (!locationWithPc.containsKey(source)) {
									locationWithPc.put(source, pcCount);
								}
								if (!locationWithPc.containsKey(target)) {
									locationWithPc.put(target, pcCount+1);
									pcCount++;
								}
								Pair<Integer, Integer> pcPair = new Pair<>(locationWithPc.get(source), locationWithPc.get(target));
								Pair<String, Pair<Integer, Integer>> pcPairWithThread = new Pair<>(thread, pcPair);
								Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> asWithPc = new Pair<>(as, pcPairWithThread);
								assignmentStatementWithPc.add(asWithPc);
							}
						}
						else if (stmt instanceof AssumeStatement) {
							AssumeStatement as = (AssumeStatement) stmt;
							if (!locationWithPc.containsKey(source)) {
								locationWithPc.put(source, pcCount);
							}
							if (!locationWithPc.containsKey(target)) {
								locationWithPc.put(target, pcCount+1);
								pcCount++;
							}
							Pair<Integer, Integer> tempPair = new Pair<>(locationWithPc.get(source), locationWithPc.get(target));
							Pair<String, Pair<Integer, Integer>> pcPairWithThread = new Pair<>(thread, tempPair);
							Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>> tempPair3 = new Pair<>(as, pcPairWithThread);
							assumeStatementWithPc.add(tempPair3);
						}
						else if (stmt instanceof HavocStatement) {
							HavocStatement hs = (HavocStatement) stmt;
							Boolean isContained = true;
							for (VariableLHS lhs : hs.getIdentifiers()) {
								if (!varOrder.contains(lhs.getIdentifier())) {
									isContained = false;
								}
							}
							if (isContained) {
								if (!locationWithPc.containsKey(source)) {
									locationWithPc.put(source, pcCount);
								}
								if (!locationWithPc.containsKey(target)) {
									locationWithPc.put(target, pcCount+1);
									pcCount++;
								}
								Pair<Integer, Integer> tempPair = new Pair<>(locationWithPc.get(source), locationWithPc.get(target));
								Pair<String, Pair<Integer, Integer>> pcPairWithThread = new Pair<>(thread, tempPair);
								Pair<HavocStatement, Pair<String, Pair<Integer, Integer>>> tempPair3 = new Pair<>(hs, pcPairWithThread);
								havocStatementWithPc.add(tempPair3);
							}
						}
					}
				}
				else if (edge instanceof Summary) {
					Summary newEdge = (Summary) edge;
					CallStatement cs = newEdge.getCallStatement();
					String ud = "ULTIMATE.dealloc";
					if (!cs.getMethodName().equals(ud)) {
						Boolean isContained = true;
						for (VariableLHS lhs : cs.getLhs()) {
							if (!varOrder.contains(lhs.getIdentifier())) {
								isContained = false;
							}
						}
						for (Expression expr : cs.getArguments()) {
							if (expr instanceof IdentifierExpression) {
								IdentifierExpression ie = (IdentifierExpression) expr;
								if (!varOrder.contains(ie.getIdentifier())) {
									isContained = false;
								}
							}
						}
						if (isContained) {
							if (!locationWithPc.containsKey(source)) {
								locationWithPc.put(source, pcCount);
							}
							if (!locationWithPc.containsKey(target)) {
								locationWithPc.put(target, pcCount+1);
								pcCount++;
							}
							Pair<Integer, Integer> pcPair = new Pair<>(locationWithPc.get(source), locationWithPc.get(target));
							Pair<String, Pair<Integer, Integer>> pcPairWithThread = new Pair<>(thread, pcPair);
							Pair<CallStatement, Pair<String, Pair<Integer, Integer>>> csWithPc = new Pair<>(cs, pcPairWithThread);
//							mLogger.info(cs);
//							mLogger.info(pcPairWithThread);
							callStatementWithPc.add(csWithPc);
						}
					}
				}
				if (target.getOutgoingEdges().isEmpty()) {
//					finishPcForEachThread.add(locationWithPc.get(target));
					finishPc.add(locationWithPc.get(target));
					
				}
			}
			needMaxPc2.put(thread, (int) Math.pow(2, Integer.toBinaryString(pcCount).length()));
//			needInitialBefore.put(thread, finishInitialPc);
			finishPcForEachThread.put(thread, finishPc);
		}
	}

	
	private List<Integer> test() {
		List<Integer> temp = new ArrayList<Integer>();
		String us = "ULTIMATE.start";
		temp.add(needMaxPc2.get(us));
		for (Pair<String, Expression[]> p : threadAndInput) {
			temp.add(needMaxPc2.get(p.getFirst()));
		}
		return temp;
	}
	
	
	private void getNeedInitialBefore() {
		String us = "ULTIMATE.start";
		List<String> usedVar = new ArrayList<String>();
		int finishInitialPc = 0;
		Boolean lock = false;
		for (Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> p1 : assignmentStatementWithPc) {
			if (p1.getSecond().getFirst().equals(us)) {
				if (!lock) {
					for (Expression ex : p1.getFirst().getRhs()) {
						if (!ex.getClass().getSimpleName().equals(il)){
							lock = true;
						}
						else {
							for (LeftHandSide lhs : p1.getFirst().getLhs()) {
								VariableLHS lhs2 = (VariableLHS) lhs;
								if (!usedVar.contains(lhs2.getIdentifier())) {
									finishInitialPc++;
									usedVar.add(lhs2.getIdentifier());
								}
							}
						}
					}
				}
				else {
					break;
				}
			}
		}
		lock = false;
		String ua = "#Ultimate.allocOnStack";
		String wii = "write~init~int";
		for (Pair<CallStatement, Pair<String, Pair<Integer, Integer>>> p1 : callStatementWithPc) {
			if (p1.getSecond().getFirst().equals(us)) {
				if (!lock) {
					if (p1.getFirst().getMethodName().equals(ua) || p1.getFirst().getMethodName().equals(wii)){
						finishInitialPc++;
					}
					else {
						lock = true;
					}
				}
				else {
					break;
				}
			}
		}
		needInitialBefore.put(us, finishInitialPc);
		for (String thread : threadOrder){
			if (!thread.equals(us)) {
				int finishInitialPc2 = 0;
				Boolean lock2 = false;
				for (Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> p1 : assignmentStatementWithPc) {
					if (p1.getSecond().getFirst().equals(thread)) {
						if (!lock2) {
							for (Expression ex : p1.getFirst().getRhs()) {
								if (!ex.getClass().getSimpleName().equals(il)){
									finishInitialPc2 = p1.getSecond().getSecond().getFirst();
									lock2 = true;
								}
								else {
									for (LeftHandSide lhs : p1.getFirst().getLhs()) {
										VariableLHS lhs2 = (VariableLHS) lhs;
										if (!usedVar.contains(lhs2.getIdentifier())) {
											usedVar.add(lhs2.getIdentifier());
										}
										else {
											finishInitialPc2 = p1.getSecond().getSecond().getFirst();
											lock2 = true;
										}
									}
								}
							}
						}
					}
				}
				needInitialBefore.put(thread, finishInitialPc2);
			}
		}
	}
	
	
	private void buildRcfgTrans() {
		// First do thread ULTIMATE.start
		String us = "ULTIMATE.start";
		for (Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> p : assignmentStatementWithPc) {
			if (us.equals(p.getSecond().getFirst())) {
				assignmentSection(p, 0, null);
			}
		}
		for (Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>> p : assumeStatementWithPc) {
			if (us.equals(p.getSecond().getFirst())) {
				assumeSection(p, 0);
			}
		}
		for (Pair<CallStatement, Pair<String, Pair<Integer, Integer>>> p : callStatementWithPc) {
			if (us.equals(p.getSecond().getFirst())) {
				callSection(p, 0, null);
			}
		}
		for (Pair<HavocStatement, Pair<String, Pair<Integer, Integer>>> p : havocStatementWithPc) {
			if (us.equals(p.getSecond().getFirst())) {
				havocSection(p, 0);
			}
		}
		// Next do other threads
		int count = 1;
		for (Pair<String, Expression[]> p : threadAndInput) {
			for (Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> p2 : assignmentStatementWithPc) {
				if (p2.getSecond().getFirst().equals(p.getFirst())) {
					assignmentSection(p2, count, p.getSecond());
				}
			}
			for (Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>> p2 : assumeStatementWithPc) {
				if (p2.getSecond().getFirst().equals(p.getFirst())) {
					assumeSection(p2, count);
				}
			}
			for (Pair<CallStatement, Pair<String, Pair<Integer, Integer>>> p2 : callStatementWithPc) {
				if (p2.getSecond().getFirst().equals(p.getFirst())) {
					callSection(p2, count, p.getSecond());
				}
			}
			for (Pair<HavocStatement, Pair<String, Pair<Integer, Integer>>> p2 : havocStatementWithPc) {
				if (p2.getSecond().getFirst().equals(p.getFirst())) {
					havocSection(p2, count);
				}
			}
			count++;
		}
	}

	

	private void assignmentSection(Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> p, int count, Expression[] inputEx) {
		AssignmentStatement as = p.getFirst();
		int needVar = -1;
		int needVar2 = -1;
		BDD transition = bdd.one();
		if (as.getLhs().length == 1) {
			VariableLHS lhs = (VariableLHS) as.getLhs()[0];
			String leftVar = lhs.getIdentifier();
			needVar = calculateIndex(leftVar);
			if (!leftVar.contains(tshort)) {
				Expression expr = as.getRhs()[0];
				
				// deal with transitions
				transition = mAssignmentStatementEvalator.buildTran(expr, needVar);
			}
			else {
				if (as.getRhs()[0] instanceof IdentifierExpression) {
					Expression expr = as.getRhs()[0];
					transition = mAssignmentStatementEvalator.buildTran(expr, needVar);
				}
				else if (as.getRhs()[0] instanceof BinaryExpression) {
					transition = bdd.zero();
					BDD temp1 = mAssumeStatementEvalator.buildTran(p.getFirst().getRhs()[0]);
					IntegerLiteral il1 = new IntegerLiteral(as.getLoc(), Integer.toString(1));
					BDD temp2 = mAssignmentStatementEvalator.buildTran(il1, needVar);
					transition = transition.or(temp1.and(temp2));
					BDD temp3 = temp1.not();
					IntegerLiteral il2 = new IntegerLiteral(as.getLoc(), Integer.toString(0));
					BDD temp4 = mAssignmentStatementEvalator.buildTran(il2, needVar);
					transition = transition.or(temp3.and(temp4));
				}
			}
		}
		else if (as.getLhs().length == 2) {
			VariableLHS lhs = (VariableLHS) as.getLhs()[0];
			needVar = calculateIndex(lhs.getIdentifier());
			Expression expr = as.getRhs()[0];
			if (expr instanceof IdentifierExpression) {
				IdentifierExpression ie = (IdentifierExpression) expr;
				if (!varOrder.contains(ie.getIdentifier())) {
					expr = inputEx[0];
				}
			}
			
			BDD transition1 = mAssignmentStatementEvalator.buildTran(expr, needVar);
			
			VariableLHS lhs2 = (VariableLHS) as.getLhs()[1];
			needVar2 = calculateIndex(lhs2.getIdentifier());
			Expression expr2 = as.getRhs()[1];
			if (expr2 instanceof IdentifierExpression) {
				IdentifierExpression ie = (IdentifierExpression) expr2;
				if (!varOrder.contains(ie.getIdentifier())) {
					expr2 = inputEx[1];
				}
			}
			
			BDD transition2 = mAssignmentStatementEvalator.buildTran(expr2, needVar2);
			
			transition = transition1.and(transition2);
		}
		// deal with PCs
		BDD transitionWithPc = addPc2(transition, count, p.getSecond().getSecond());
		if (!transitionWithPc.isZero()) {
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(p.getSecond());
		}
		int needBefore = needInitialBefore.get(p.getSecond().getFirst());
		if (p.getSecond().getSecond().getFirst() < needBefore) {
			initialTrans.add(transitionWithPc);
		}
	}
	
	private void assumeSection(Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>> p, int count) {
		Expression expr = p.getFirst().getFormula();
		boolean isShort = false;
		int checkType = 0;
		if (expr instanceof IdentifierExpression) {
			isShort = true;
			checkType = 1;
		}
		else if (expr instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression) expr;
			if (ue.getExpr() instanceof IdentifierExpression) {
				isShort = true;
				checkType = 2;
			}
			else if (ue.getExpr() instanceof UnaryExpression) {
				UnaryExpression ue2 = (UnaryExpression) ue.getExpr();
				if (ue2.getExpr() instanceof IdentifierExpression) {
					isShort = true;
					checkType = 3;
				}
			}
		}
		BDD transition = bdd.one();
		if (!isShort) {
			// deal with transitions
			transition = mAssumeStatementEvalator.buildTran(expr);
		}
		else {
			if (checkType == 1) {
				IdentifierExpression ie = (IdentifierExpression) expr;
				String var = ie.getIdentifier();
				Expression intLit = new IntegerLiteral(p.getFirst().getLoc(), Integer.toString(1));
				Expression wantToCompare = new IdentifierExpression(p.getFirst().getLoc(), var);
				transition = mAssumeStatementEvalator.buildTran2(intLit, wantToCompare, Operator.COMPEQ);
			}
			else if (checkType == 2) {
				UnaryExpression ue = (UnaryExpression) expr;
				IdentifierExpression ie = (IdentifierExpression) ue.getExpr();
				String var = ie.getIdentifier();
				Expression intLit = new IntegerLiteral(p.getFirst().getLoc(), Integer.toString(1));
				Expression wantToCompare = new IdentifierExpression(p.getFirst().getLoc(), var);
				transition = mAssumeStatementEvalator.buildTran2(intLit, wantToCompare, Operator.COMPEQ).not();
			}
			else if (checkType == 3) {
				UnaryExpression ue = (UnaryExpression) expr;
				UnaryExpression ue2 = (UnaryExpression) ue.getExpr();
				IdentifierExpression ie = (IdentifierExpression) ue2.getExpr();
				String var = ie.getIdentifier();
				Expression intLit = new IntegerLiteral(p.getFirst().getLoc(), Integer.toString(1));
				Expression wantToCompare = new IdentifierExpression(p.getFirst().getLoc(), var);
				transition = mAssumeStatementEvalator.buildTran2(intLit, wantToCompare, Operator.COMPEQ);
			}
		}
		// deal with PCs
		BDD transitionWithPc = addPc2(transition, count, p.getSecond().getSecond());	
								
//		mLogger.info(transitionWithPc);
		if (!transitionWithPc.isZero()) {
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(p.getSecond());
		}	
	}
	
	
	private void callSection(Pair<CallStatement, Pair<String, Pair<Integer, Integer>>> p, int count, Expression[] newEx) {
		String ua = "#Ultimate.allocOnStack";
		String ri = "read~int";
		String wi = "write~int";
		String wii = "write~init~int";
		
		CallStatement cs = (CallStatement) p.getFirst();
		Pair<String, Pair<Integer, Integer>> threadWithPcPair = p.getSecond();
		Expression[] expr = cs.getArguments();
		if (cs.getMethodName().equals(ua)) {
			BDD transition = bdd.one();
			BDD transitionWithPc = addPc2(transition, count, threadWithPcPair.getSecond());
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(threadWithPcPair);
			
			int needBefore = needInitialBefore.get(p.getSecond().getFirst());
			if (p.getSecond().getSecond().getFirst() < needBefore) {
				initialTrans.add(transitionWithPc);
			}
		}
		else if (cs.getMethodName().equals(wii)) {
			IntegerLiteral il = (IntegerLiteral) expr[0];
			int value = Integer.parseInt(il.getValue());
			IdentifierExpression ie = (IdentifierExpression) expr[1];
			String leftVar = ie.getIdentifier().replace(".base", "").replace(".offset", "");
			if (expr[1] instanceof IdentifierExpression && expr[2] instanceof IdentifierExpression) {
				leftVar = leftVar + "~0";
			}
			else if (expr[1] instanceof IdentifierExpression && expr[2] instanceof BinaryExpression) {
				BinaryExpression be = (BinaryExpression) expr[2];
				IntegerLiteral il2 = (IntegerLiteral) be.getLeft();
				int index = Integer.parseInt(il2.getValue()) / 4;
				leftVar = leftVar + "~" + index;
			}
			int needVar = calculateIndex(leftVar);

			BDD transition = mAssignmentStatementEvalator.buildTran(il, needVar);
			BDD transitionWithPc = addPc2(transition, count, threadWithPcPair.getSecond());
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(threadWithPcPair);
			
			int needBefore = needInitialBefore.get(p.getSecond().getFirst());
			if (p.getSecond().getSecond().getFirst() < needBefore) {
				initialTrans.add(transitionWithPc);
			}
		}
		else if (cs.getMethodName().equals(wi)) {
			Expression wantToWrite = expr[0];
			IdentifierExpression ie = (IdentifierExpression) expr[1];
			String leftVar = ie.getIdentifier().replace(".base", "").replace(".offset", "");
			Expression index = null;
			if (expr[2] instanceof BinaryExpression) {
				BinaryExpression be = (BinaryExpression) expr[2];
				if (be.getRight() instanceof BinaryExpression) {
					BinaryExpression be2 = (BinaryExpression) be.getRight();
					index = be2.getRight();
				}
				else if (be.getRight() instanceof IdentifierExpression) {
					IntegerLiteral il = (IntegerLiteral) be.getLeft();
					int i = Integer.parseInt(il.getValue());
					index = new IntegerLiteral(cs.getLoc(), Integer.toString(i/4));
				}
			}
			else if (expr[2] instanceof IdentifierExpression) {
				index = new IntegerLiteral(cs.getLoc(), Integer.toString(0));
			}
			int leftNeedVar = -1;
			BDD transition = bdd.zero();
			for (int i = 0; i < arrayWithLength.get(leftVar); i++) {
				String leftVar2 = leftVar + "~" + i;
				leftNeedVar = calculateIndex(leftVar2);
				if (index instanceof IdentifierExpression) {
					Expression intLit = new IntegerLiteral(cs.getLoc(), Integer.toString(i));
					BDD temp1 = mAssumeStatementEvalator.buildTran2(intLit, index, Operator.COMPEQ);
					BDD temp2 = mAssignmentStatementEvalator.buildTran(wantToWrite, leftNeedVar);
					BDD temp3 = temp1.and(temp2);
					transition = transition.or(temp3);
				}
				else if (index instanceof IntegerLiteral) {
					IntegerLiteral il = (IntegerLiteral) index;
					int value = Integer.parseInt(il.getValue());
					if (value == i) {
						BDD temp = mAssignmentStatementEvalator.buildTran(wantToWrite, leftNeedVar);
						transition = transition.or(temp);
					}
				}
			}
			BDD transitionWithPc = addPc2(transition, count, threadWithPcPair.getSecond());
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(threadWithPcPair);
		}
		else if (cs.getMethodName().equals(ri)) {
			VariableLHS vl = cs.getLhs()[0];
			String leftVar = vl.getIdentifier();
			int leftNeedVar = calculateIndex(leftVar);
			if (expr[1] instanceof IdentifierExpression) {
				IdentifierExpression ie = (IdentifierExpression) expr[1];
				if (!arrayWithLength.containsKey(ie.getIdentifier())) {
					expr = newEx;
				}
			}
			IdentifierExpression ie = (IdentifierExpression) expr[0];
			String rightVar = ie.getIdentifier().replace(".base", "").replace(".offset", "");
			Expression index = null;
			if (expr[1] instanceof BinaryExpression) {
				BinaryExpression be = (BinaryExpression) expr[1];
				if (be.getRight() instanceof BinaryExpression) {
					BinaryExpression be2 = (BinaryExpression) be.getRight();
					index = be2.getRight();
				}
				else if (be.getRight() instanceof IdentifierExpression) {
					IntegerLiteral il = (IntegerLiteral) be.getLeft();
					int i = Integer.parseInt(il.getValue());
					index = new IntegerLiteral(cs.getLoc(), Integer.toString(i/4));
				}
			}
			
			else if (expr[1] instanceof IdentifierExpression) {
				index = new IntegerLiteral(cs.getLoc(), Integer.toString(0));
			}
			BDD transition = bdd.zero();
			for (int i = 0; i < arrayWithLength.get(rightVar); i++) {
				String rightVar2 = rightVar + "~" + i;
				if (index instanceof IdentifierExpression) {
					Expression intLit = new IntegerLiteral(cs.getLoc(), Integer.toString(i));
					Expression wantToWrite = new IdentifierExpression(cs.getLoc(), rightVar2);
					BDD temp1 = mAssumeStatementEvalator.buildTran2(intLit, index, Operator.COMPEQ);
					BDD temp2 = mAssignmentStatementEvalator.buildTran(wantToWrite, leftNeedVar);
					BDD temp3 = temp1.and(temp2);
					transition = transition.or(temp3);
				}
				else if (index instanceof IntegerLiteral) {
					IntegerLiteral il = (IntegerLiteral) index;
					int value = Integer.parseInt(il.getValue());
					if (value == i) {
						Expression wantToWrite = new IdentifierExpression(cs.getLoc(), rightVar2);
						BDD temp = mAssignmentStatementEvalator.buildTran(wantToWrite, leftNeedVar);
						transition = transition.or(temp);
					}
				}
			}
			BDD transitionWithPc = addPc2(transition, count, threadWithPcPair.getSecond());
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(threadWithPcPair);
		}
	}
	
	private void havocSection(Pair<HavocStatement, Pair<String, Pair<Integer, Integer>>> p, int count) {
		BDD transition = bdd.one();
		BDD transitionWithPc = addPc2(transition, count, p.getSecond().getSecond());
		if (!transitionWithPc.isZero()) {
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(p.getSecond());
		}
	}
	
	private BDD addPc(BDD t, Pair<String, Pair<Integer, Integer>> pcPair) {
		BDD transition = t;
		String pcThread = pcPair.getFirst();
		Pair<Integer, Integer> pc = pcPair.getSecond();
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
		return transition;
	}
		
	
	private BDD addPc2(BDD t, int threadIndex, Pair<Integer, Integer> pcPair) {
		BDD transition = t;
		Pair<Integer, Integer> pc = pcPair;
		int prePcValue = pc.getFirst();
		int postPcValue = pc.getSecond();
		BDDBitVector prePc = bdd.buildVector(rcfgPc[threadIndex]);
		BDDBitVector postPc = bdd.buildVector(rcfgPcPrime[threadIndex]);
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
		return transition;
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
	

	public Set<String> getThreadOrder() {
		return threadOrder;
	}

	private void buildSelfLoop() {
		int count = 0;
		for (String s : threadOrder) {
			for (int pc : finishPcForEachThread.get(s)) {
				BDD transition = bdd.one();
				BDDBitVector prePc = bdd.buildVector(rcfgPc[count]);
				BDDBitVector postPc = bdd.buildVector(rcfgPcPrime[count]);
				BDDBitVector preBl = bdd.constantVector(prePc.size(), pc);
				BDDBitVector postBl = bdd.constantVector(postPc.size(), pc);
				
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
				rcfgTrans.add(transition);
			}
			count++;
		}
	}
	
	
	public Map<String, Set<Integer>> getFinishPcForEachThread() {
		return finishPcForEachThread;
	}
	
}