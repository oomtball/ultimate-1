package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sf.javabdd.*;

import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.cdt.translation.implementation.CLocation;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.RcfgStatementExtractor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

public class RcfgTransitionBuilder{
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	static BDDFactory bdd;
	
	List<IcfgLocation> foundLoc;
	List<IcfgEdge> foundEdge;
	
	List<Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>>> assignmentStatementWithPc;
	List<Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>>> assumeStatementWithPc;
	
	private final AssignmentStatementEvalator mAssignmentStatementEvalator;
	private final AssumeStatementEvalator mAssumeStatementEvalator;
	
	public List<Integer> finishPcForEachThread;
	
//	static BDDFactory bdd;
	public List<BDD> rcfgTrans = new ArrayList<BDD>();
	public List<Pair<String, Pair<Integer, Integer>>> rcfgTransPc = new ArrayList<Pair<String, Pair<Integer, Integer>>>();
	public List<BDD> initialTrans = new ArrayList<BDD>();
	
	List<Integer> needMaxPc;
	Map<IcfgLocation, Integer> locationWithPc;
	
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
			BDDFactory _bdd, BDDDomain[] _v, BDDDomain[] _vprime, Set<String> vo) {
		mServices = services;
		mLogger = logger;
		varOrder = vo;
		threadOrder = rcfg.getProcedureEntryNodes().keySet();
		
		// get edge for each thread in order
		Map<String, List<IcfgEdge>> threadToAllEdges = getThreadToEdges(rcfg.getProcedureEntryNodes());
		assignmentStatementWithPc = new ArrayList<Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>>>();
		assumeStatementWithPc = new ArrayList<Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>>>();

		finishPcForEachThread = new ArrayList<Integer>();
		needMaxPc = new ArrayList<Integer>();
		locationWithPc = new HashMap<IcfgLocation, Integer>();
		getAsWithPc(threadToAllEdges);
		
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
		
		buildRcfgTrans();
//		
//		BDD test = bdd.zero();
//		for (BDD b : rcfgTrans) {
//			mLogger.info(b);
//			test = test.or(b);
//		}
//		mLogger.info(test);
	}
	
	private void dfsRecursive(IcfgLocation initialLoc) {
		if (foundLoc.contains(initialLoc)) {
			return;
		}
		foundLoc.add(initialLoc);
		
		for (IcfgEdge ie : initialLoc.getOutgoingEdges()) {
			foundEdge.add(ie);
		}
		
		if (initialLoc.getOutgoingNodes().isEmpty()) {
			return;
		}
		else {
			for (IcfgLocation il : initialLoc.getOutgoingNodes()) {
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
		return threadToEdges;
	}
	
	private void getAsWithPc(Map<String, List<IcfgEdge>> threadToAllEdges) {
		for (String thread : threadOrder){
			int pcCount =  0;
			for (IcfgEdge edge : threadToAllEdges.get(thread)) {
				IcfgLocation source = edge.getSource();
				IcfgLocation target = edge.getTarget();
				if (edge instanceof StatementSequence) {
					StatementSequence ss = (StatementSequence) edge;
					List<Statement> statements = ss.getStatements();
					for (Statement stmt : statements) {
						if (stmt instanceof AssignmentStatement) {
							AssignmentStatement as = (AssignmentStatement) stmt;
							for (LeftHandSide lhs : as.getLhs()) {
								VariableLHS lhs2 = (VariableLHS) lhs;
								if (varOrder.contains(lhs2.getIdentifier())) {
									if (!locationWithPc.containsKey(source)) {
										locationWithPc.put(source, pcCount);
									}
									if (!locationWithPc.containsKey(target)) {
										locationWithPc.put(target, pcCount+1);
									}
									Pair<Integer, Integer> pcPair = new Pair<>(locationWithPc.get(source), locationWithPc.get(target));
									Pair<String, Pair<Integer, Integer>> pcPairWithThread = new Pair<>(thread, pcPair);
									Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> asWithPc = new Pair<>(as, pcPairWithThread);
									assignmentStatementWithPc.add(asWithPc);
									if (target.getOutgoingNodes().isEmpty()) {
										finishPcForEachThread.add(locationWithPc.get(target));
									}
									pcCount++;
								}
							}
						}
						else if (stmt instanceof AssumeStatement) {
							AssumeStatement as = (AssumeStatement) stmt;
							if (!locationWithPc.containsKey(source)) {
								locationWithPc.put(source, pcCount);
							}
							if (!locationWithPc.containsKey(target)) {
								locationWithPc.put(target, pcCount+1);
							}
							Pair<Integer, Integer> tempPair = new Pair<>(locationWithPc.get(source), locationWithPc.get(target));
							Pair<String, Pair<Integer, Integer>> tempPair2 = new Pair<>(thread, tempPair);
							Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>> tempPair3 = new Pair<>(as, tempPair2);
							assumeStatementWithPc.add(tempPair3);
							if (target.getOutgoingNodes().isEmpty()) {
								finishPcForEachThread.add(locationWithPc.get(target));
							}
							pcCount++;
						}
					}
				}
			}
			needMaxPc.add((int) Math.pow(2, Integer.toBinaryString(pcCount).length()));
			finishPcForEachThread.add(pcCount);
		}
	}
	
	private void buildRcfgTrans() {
		int tempMin = 10000;
		BDD initialTran = bdd.one();
		for (Pair<AssignmentStatement, Pair<String, Pair<Integer, Integer>>> p : assignmentStatementWithPc) {
			AssignmentStatement as = p.getFirst();
			int needVar = -1;
			for (LeftHandSide lhs : as.getLhs()) {
				VariableLHS lhs2 = (VariableLHS) lhs;
				needVar = calculateIndex(lhs2.getIdentifier());
			}
			Expression expr = as.getRhs()[0];
			
			// deal with transitions
			BDD transition = mAssignmentStatementEvalator.buildTran(expr, needVar);

			// deal with PCs
			BDD transitionWithPc = addPc(transition, p.getSecond());
			rcfgTrans.add(transitionWithPc);
			rcfgTransPc.add(p.getSecond());
			
			for (String sss : expr.getPayload().getAnnotations().keySet()) {
				CLocation cl = (CLocation) expr.getPayload().getAnnotations().get(sss);
				if (cl.getStartLine() < tempMin) {
					tempMin = cl.getStartLine();
					initialTran = transition;
				}
			}
		}
		initialTrans.add(initialTran);
		for (Pair<AssumeStatement, Pair<String, Pair<Integer, Integer>>> p : assumeStatementWithPc) {
			Expression expr = p.getFirst().getFormula();
			// deal with transitions
			BDD transition = mAssumeStatementEvalator.buildTran(expr);

			// deal with PCs
			BDD transitionWithPc = addPc(transition, p.getSecond());	
			
//			mLogger.info(transitionWithPc);
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

	public List<Integer> getFinishPcForEachThread() {
		return finishPcForEachThread;
	}
}