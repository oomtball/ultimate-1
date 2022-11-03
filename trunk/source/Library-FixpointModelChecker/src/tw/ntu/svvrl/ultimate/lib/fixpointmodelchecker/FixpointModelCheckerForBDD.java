package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.variables.IProgramVarOrConst;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.FixpointEngineParameters;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.ILoopDetector;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.ITransitionProvider;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.IcfgTransitionProvider;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.RCFGLiteralCollector;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.RcfgLoopDetector;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.RcfgStatementExtractor;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.tool.initializer.FixpointEngineParameterFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDBitVector;
import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm.RcfgTransitionBuilder;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer.NwaTransitionBuilder;

public class FixpointModelCheckerForBDD {
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private final BoogieIcfgContainer mRcfgRoot;
	private final INestedWordAutomaton<CodeBlock, String> mNwa;
	
	private final NwaTransitionBuilder mNwaTransitionBuilder;
	private final RcfgTransitionBuilder mRcfgTransitionBuilder;
	
	static BDDFactory bdd;
	BDDDomain[] v; // represents different v bdd variables
	BDDDomain[] vprime; // represents different vprime bdd variables
	
//	public FixpointModelCheckerForBDD(final BoogieIcfgContainer rcfg,
//			final ILogger logger, final IUltimateServiceProvider services) {
//		
//		// services and logger
//		mServices = services;
//		mLogger = logger;
//		mRcfgRoot = rcfg;
//		mNWA = null;
//		
//
//
//		mRcfgTransitionBuilder = new RcfgTransitionBuilder(rcfg, mLogger, mServices, bdd);
//		mNwaTransitionBuilder = null;
//	}
	
	public FixpointModelCheckerForBDD(final INestedWordAutomaton<CodeBlock, String> nwa, final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger+
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNwa = nwa;
		// set up BDD factory
		bdd = BDDFactory.init("j", 100000, 100000, false);
		
		// set up BDD domain
		Set<AssignmentStatement> allAssignment = getAllAssignments();
		
		Set<String> allVar = getAllVar(allAssignment);
		
		mLogger.info(Arrays.toString(allVar.toArray()));
		
		int systemNeedBit = 2;
		int propertyNeedBit = findPropertyNeedBit(mNwa);
		int actuallyNeedBit = 0;
		if (systemNeedBit >= propertyNeedBit) {
			actuallyNeedBit = systemNeedBit;
		}
		else {
			actuallyNeedBit = propertyNeedBit;
		}
		
		int[] pam = setPam(actuallyNeedBit, allVar.size()).stream().mapToInt(Integer::intValue).toArray();
		mLogger.info(Arrays.toString(pam));
		v = bdd.extDomain(pam); // represents different v bdd variables
		vprime = bdd.extDomain(pam); // represents different vprime bdd variables
		Set<String> varOrder = allVar;
		
		
		// create RCFG transition builder which can help getting transitions of the system RCFG
		mRcfgTransitionBuilder = new RcfgTransitionBuilder(mRcfgRoot, mLogger, mServices, bdd, v, vprime, varOrder);
		// create NWA transition builder which can help getting transitions of the property NWA
		mNwaTransitionBuilder = new NwaTransitionBuilder(mNwa, mLogger, mServices, bdd, v, vprime, varOrder);
		
		mLogger.info("system : " + Arrays.toString(mRcfgTransitionBuilder.getRcfgTrans().toArray()));
		mLogger.info("property : " + Arrays.toString(mNwaTransitionBuilder.getNwaTrans().toArray()));
		mLogger.info("property final trans : " + Arrays.toString(mNwaTransitionBuilder.getNwaFinalTrans().toArray()));
		
		// calculate I
		BDD input = setInput2();
		Set<BDD> I = new HashSet<BDD>();
		I.add(input);
		mLogger.info("input : " + Arrays.toString(I.toArray()));

		// calculate the fixpoint mu x
		Set<BDD> initialFixpoint = calculateMuX(I);
		mLogger.info("mu x : " + Arrays.toString(initialFixpoint.toArray()));
		
		// calculate R_Alpha
		Set<BDD> R_Alpha = calculateR_Alpha(initialFixpoint);
		mLogger.info("R_Alpha : " + Arrays.toString(R_Alpha.toArray()));
		
		// calculate Post(true)
		Set<BDD> productTrans = getProductTrans();	
		mLogger.info("product : " + Arrays.toString(productTrans.toArray()));
		Set<BDD> postTrue = calculatePostTrue(productTrans);
		mLogger.info("post(True) : " + Arrays.toString(postTrue.toArray()));
		
		// calculate nu y
		Set<BDD> finalFixpoint = calculateNuY(postTrue, R_Alpha);
		mLogger.info("nu y : " + Arrays.toString(finalFixpoint.toArray()));
		
		// check specifications
		finalCheck(finalFixpoint);
	}
	
	private Set<AssignmentStatement> getAllAssignments(){
		Set<AssignmentStatement> allAssignment = new HashSet<AssignmentStatement>();
		for (String s : mRcfgRoot.getProgramPoints().keySet()) {
			for (DebugIdentifier s2 : mRcfgRoot.getProgramPoints().get(s).keySet()) {
				for (IcfgEdge i : mRcfgRoot.getProgramPoints().get(s).get(s2).getOutgoingEdges()) {
					String ss = "StatementSequence";
					if (i.getClass().getSimpleName().equals(ss)) {
						StatementSequence newI = (StatementSequence) i;
//						mLogger.info(i);
						for (Statement s3 : newI.getStatements()) {
							String as = "AssignmentStatement";
							if (s3.getClass().getSimpleName().equals(as)) {
								AssignmentStatement news3 = (AssignmentStatement) s3;
								allAssignment.add(news3);
							}
						}
					}
				}
			}
		}
		return allAssignment;
	}
	
	private Set<String> getAllVar(Set<AssignmentStatement> allAssignment){
		Set<String> allVar = new HashSet<String>();
		for (AssignmentStatement as : allAssignment) {
//			mLogger.info(as);
			for (LeftHandSide lhs : as.getLhs()) {
				VariableLHS lhs2 = (VariableLHS) lhs;
//				mLogger.info(lhs2.getIdentifier());
				String varCandidate = lhs2.getIdentifier();
				String c1 = "#";
				String c2 = "#t~post";
				String c3 = "~_.offset";
				String c4 = "~_.base";
				if (varCandidate.contains(c2) && !varCandidate.contains(c3) && !varCandidate.contains(c4)) {
					allVar.add(varCandidate);
				}
				if (!varCandidate.contains(c1) && !varCandidate.contains(c3) && !varCandidate.contains(c4)) {
					allVar.add(varCandidate);
				}
			}
		}
		return allVar;
	}
	
	private List<Integer> setPam(int actuallyNeedBit, int len) {
		List<Integer> bitArray = new ArrayList<Integer>();
		for (int i = 0; i < len; i++) {
			bitArray.add((int) Math.pow(2, actuallyNeedBit));
		}
		return bitArray;
	}
	
	private Set<BDD> calculateMuX(Set<BDD> i){
		Set<BDD> postx = i;
		Set<BDD> I = i;
		while (true) {
			Set<BDD> postxUnionI = new HashSet<BDD>();
			for (BDD b : postx) {
				postxUnionI.add(b);
			}
			for (BDD b : I) {
				postxUnionI.add(b);
			}
			Set<BDD> temp = new HashSet<BDD>();
			for (BDD rcfgTran : mRcfgTransitionBuilder.getRcfgTrans()) {
				for (BDD property : mNwaTransitionBuilder.getNwaTrans()) {
					for (BDD b : postxUnionI) {
						if (checkProperty(b, property)) {
							BDD post = getPost(b, rcfgTran);
							BDDPairing bp = bdd.makePair();
							bp.set(vprime, v);
							BDD input2 = post.replace(bp);
							if (!temp.contains(input2)) {
								temp.add(input2);
							}
						}
					}
				}
			}
			if (temp.equals(postx)) {
				break;
			}
			else {
				postx = temp;
			}
//			mLogger.info(Arrays.toString(fixpoint.toArray()));
		}
		return postx;
	}
	
	private Set<BDD> calculateR_Alpha(Set<BDD> fixpoint){
		Set<BDD> R_Alpha = new HashSet<BDD>();
		for (BDD b1 : mNwaTransitionBuilder.getNwaFinalTrans()) {
			for (BDD b2 : fixpoint) {
				if (checkProperty(b2, b1)) {
					R_Alpha.add(b2);
				}
			}
		}
		return R_Alpha;
	}
	
	private Set<BDD> calculatePostTrue(Set<BDD> productTrans){
		Set<BDD> postTrue = new HashSet<BDD>();
		BDD varset = bdd.makeSet(v).and(bdd.makeSet(vprime));
		for (BDD b : productTrans) {
			List<Object> list = new ArrayList<Object>();
			b.iterator(varset).forEachRemaining(list::add);
			
			for (Object o : list) {
				BDD test = (BDD) o;
				BDD varset2 = v[0].set();
				BDDPairing bp = bdd.makePair();
				bp.set(vprime, v);
				postTrue.add(test.exist(varset2).replace(bp));
			}
		}
		return postTrue;
	}
	
	private Set<BDD> calculateNuY(Set<BDD> f, Set<BDD> R_Alpha){
		Set<BDD> posty = f;
		Set<BDD> R = R_Alpha;
		while (true) {
			Set<BDD> postyInterR = new HashSet<BDD>();
			for (BDD b1 : R) {
				if (posty.contains(b1)) {
					postyInterR.add(b1);
				}
			}
			Set<BDD> temp = calculateMuX(postyInterR);
			if (temp.equals(posty)) {
				break;
			}
			else {
				posty = temp;
			}
		}
		return posty;
	}
	
	private void finalCheck(Set<BDD> fixpoint2){
		if (fixpoint2.isEmpty()) {
			mLogger.info("All specifications hold.");
		}
		else {
			mLogger.info("Fixpoint found :");
			mLogger.info(Arrays.toString(fixpoint2.toArray()));
		}
	}
	
	private Set<BDD> getProductTrans(){
		Set<BDD> productTrans = new HashSet<BDD>();
		
		for (BDD b1 : mRcfgTransitionBuilder.getRcfgTrans()) {
			for (BDD b2 : mNwaTransitionBuilder.getNwaTrans()) {
				BDD a = b1.and(b2);
				productTrans.add(a);
			}
		}
		return productTrans;
	}
	
	private int findPropertyNeedBit(INestedWordAutomaton<CodeBlock, String> mNwa) {
		List<Expression> allExpression = getNwaExpression(mNwa.getAlphabet());

		int needMaxBit = 0;
		for (Expression test : allExpression) {
			String be = "BinaryExpression";
			String ue = "UnaryExpression";
			if (test.getClass().getSimpleName().equals(ue)) {
				UnaryExpression u = (UnaryExpression) test;
				if (findBENeedBit(u.getExpr()) > needMaxBit) {
					needMaxBit = findBENeedBit(u.getExpr());
				}
			}
			else if (test.getClass().getSimpleName().equals(be)) {
				if (findBENeedBit(test) > needMaxBit) {
					needMaxBit = findBENeedBit(test);
				}
			}
		}
		return needMaxBit;
	}
	
	private int findBENeedBit(Expression e) {
		int needBit  = 0;
		BinaryExpression newE = (BinaryExpression) e;
		String be = "BinaryExpression";
		String il = "IntegerLiteral";
		if (newE.getLeft().getClass().getSimpleName().equals(be)) {
			if (findBENeedBit(newE.getLeft()) > needBit) {
				needBit = findBENeedBit(newE.getLeft());
			}
		}
		else if (newE.getLeft().getClass().getSimpleName().equals(il)) {
			IntegerLiteral i = (IntegerLiteral) newE.getLeft();
			int value = Integer.parseInt(i.getValue());
			int valueLength = Integer.toBinaryString(value).length();
			if (valueLength > needBit) {
				needBit = valueLength;
			}
		}
		if (newE.getRight().getClass().getSimpleName().equals(be)) {
			if (findBENeedBit(newE.getRight()) > needBit) {
				needBit = findBENeedBit(newE.getRight());
			}
		}
		else if (newE.getRight().getClass().getSimpleName().equals(il)) {
			IntegerLiteral i = (IntegerLiteral) newE.getRight();
			int value = Integer.parseInt(i.getValue());
			int valueLength = Integer.toBinaryString(value).length();
			if (valueLength > needBit) {
				needBit = valueLength;
			}
		}
		return needBit;
	}
	
	private List<Expression> getNwaExpression(Set<CodeBlock> al) {
		List<Expression> allExpression = new ArrayList<>();
		for (CodeBlock cb : al) {
			StatementSequence ss = (StatementSequence) cb;
			for (Statement s : ss.getStatements()) {
				if (s instanceof AssumeStatement) {
					AssumeStatement as = (AssumeStatement) s;
					allExpression.add(as.getFormula());
				}
			}
		}
		return allExpression;
	}
	
	private boolean checkProperty (BDD input, BDD property) {
		boolean sat = false;
		byte[] inputByte = null;
		for (Object i1 : input.allsat()) {
			inputByte = (byte[]) i1;
		}
		for (Object b2 : property.allsat()) {
			byte[] b3 = (byte[]) b2;
			boolean temp = true;
			for (int i = 0; i < b3.length; i++) {
				if (b3[i] != -1) {
					if (inputByte[i] != b3[i]) {
						temp = false;
					}
				}
			}
			if (temp) {
				sat = true;
				break;
			}
		}
		return sat;
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
		BDDBitVector turnv = bdd.constantVector(2, 0);
		BDDBitVector xv = bdd.constantVector(2, 0);
		BDDBitVector t1v = bdd.constantVector(2, 0);
		BDDBitVector flag1v = bdd.constantVector(2, 0);
		BDDBitVector tpost1v = bdd.constantVector(2, 0);
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
		
		BDDBitVector xPre = bdd.buildVector(v[0]);
		BDDBitVector xv = bdd.constantVector(2, 0);
		
//		BDDBitVector tPost0 = bdd.buildVector(v[1]);
//		BDDBitVector tPost0v = bdd.constantVector(2, 0);
		
		
		for (int n = 0; n < xPre.size(); n++) {
			input = input.and(xPre.getBit(n).biimp(xv.getBit(n)));
		}
//		for (int n = 0; n < tPost0.size(); n++) {
//			input = input.and(tPost0.getBit(n).biimp(tPost0v.getBit(n)));
//		}
//		
		
		return input;
	}
	
	public BDD getPost(BDD input, BDD transition) {
		BDD post = bdd.one();
		
//		mLogger.info(transition);
//		mLogger.info(input);
//		mLogger.info(transition.restrict(input));
		BDDPairing bp = bdd.makePair();
		bp.set(v, vprime);
		BDD input2 = input.replace(bp);
		List<Integer> needDomains = new ArrayList<Integer>();
		for (int i : transition.restrict(input).scanSetDomains()) {
			needDomains.add(i-v.length);
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
	
//	private Map<String, Set<Integer>> getVarAndValue(){
//		Map<String, Set<Integer>> varAndValue = new HashMap<>();
//		String s1 = "~turn~0";
//		Set<Integer> temp1 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s1, temp1);
//		String s5 = "~t1~0";
//		Set<Integer> temp5 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s5, temp5);
//		String s7 = "~t2~0";
//		Set<Integer> temp7 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s7, temp7);
//		String s8 = "~f12~0";
//		Set<Integer> temp8 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s8, temp8);
//		String s10 = "~y2~0";
//		Set<Integer> temp10 = new HashSet<>(Arrays.asList(0, 1, 2));
//		varAndValue.put(s10, temp10);
//		String s11 = "~f21~0";
//		Set<Integer> temp11 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s11, temp11);
//		String s12 = "~y1~0";
//		Set<Integer> temp12 = new HashSet<>(Arrays.asList(0, 1, 2));
//		varAndValue.put(s12, temp12);
//		String s16 = "~x~0";
//		Set<Integer> temp16 = new HashSet<>(Arrays.asList(0, 1, 2));
//		varAndValue.put(s16, temp16);
//		String s17 = "~flag1~0";
//		Set<Integer> temp17 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s17, temp17);
//		String s18 = "#t~post1";
//		Set<Integer> temp18 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s18, temp18);
//		String s19 = "~flag2~0";
//		Set<Integer> temp19 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s19, temp1);
//		String s23 = "#t~post0";
//		Set<Integer> temp23 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s23, temp23);
//		return varAndValue;
//	}
	
//	private Map<String, Set<Integer>> getVarAndValue2() {
//		Map<String, Set<Integer>> varAndValue = new HashMap<>();
//		String s1 = "~x~0";
//		Set<Integer> temp1 = new HashSet<>(Arrays.asList(0, 1, 2));
//		varAndValue.put(s1, temp1);
//		String s2 = "#t~post0";
//		Set<Integer> temp2 = new HashSet<>(Arrays.asList(0, 1));
//		varAndValue.put(s2, temp2);
//		return varAndValue;
//	}
	
//	private List<Integer> findRcfgNeededBits(Map<String, Set<Integer>> varAndValue) {
//		List<Integer> bitArray = new ArrayList<Integer>();
//		for (String s : varAndValue.keySet()) {
////			mLogger.info(s);
//			bitArray.add(Integer.toBinaryString(Collections.max(varAndValue.get(s))).length());
//		}
//		int maxv = Collections.max(bitArray);
//		
//		List<Integer> v = new ArrayList<Integer>(); 
//		for (int i = 0; i < bitArray.size(); i++) {
//			v.add((int) Math.pow(2, maxv));
//		}
//		return v;
//	}
}
