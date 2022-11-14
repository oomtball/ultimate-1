package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDBitVector;
import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm.NwaTransitionBuilder;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm.RcfgTransitionBuilder;

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
	
	Set<BDD> productTrans;
	
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
		
		List<BDD> rcfgTrans = mRcfgTransitionBuilder.getRcfgTrans();
		List<BDD> nwaTrans = mNwaTransitionBuilder.getNwaTrans();
		
		mLogger.info("system : " + Arrays.toString(rcfgTrans.toArray()));
		mLogger.info("system pc : " + Arrays.toString(mRcfgTransitionBuilder.getRcfgTransPc().toArray()));
		mLogger.info("property : " + Arrays.toString(nwaTrans.toArray()));
		mLogger.info("property pc : " + Arrays.toString(mNwaTransitionBuilder.getNwaTransPc().toArray()));
//		mLogger.info("property final trans : " + Arrays.toString(mNwaTransitionBuilder.getNwaFinalTrans().toArray()));
		
		List<BDD> initialTrans = mRcfgTransitionBuilder.getInitialTrans();
//		mLogger.info(Arrays.toString(initialTrans.toArray()));
		
		
		// calculate I (to do)
		BDD input = setInput2();
		Set<BDD> I = new HashSet<BDD>();
		I.add(input);
		mLogger.info("input : " + Arrays.toString(I.toArray()));

		List<BDD> normalTrans = new ArrayList<BDD>();
		for (BDD b : rcfgTrans) {
			if (!initialTrans.contains(b)) {
				normalTrans.add(b);
			}
		}
		mLogger.info("normal transition : " + Arrays.toString(normalTrans.toArray()));
		
		// calculate cross product
		productTrans = getProductTrans(normalTrans, nwaTrans);	
		mLogger.info("product : " + Arrays.toString(productTrans.toArray()));
		
		// calculate the fixpoint mu x
		Set<BDD> initialFixpoint = calculateMuX(I);
		mLogger.info("mu x : " + Arrays.toString(initialFixpoint.toArray()));

		// calculate set of accepting states
		Set<Integer> acceptingStates = mNwaTransitionBuilder.getNwaFinalStatesPc();
		mLogger.info("accepting states : " + Arrays.toString(acceptingStates.toArray()));
	
		// calculate R_Alpha
		Set<BDD> R_Alpha = calculateR_Alpha(initialFixpoint, acceptingStates);
		mLogger.info("R_Alpha : " + Arrays.toString(R_Alpha.toArray()));
	
		// calculate Post(true)
//		Set<BDD> postTrue = calculatePostTrue(productTrans);
//		mLogger.info("post(True) : " + Arrays.toString(postTrue.toArray()));
		
		// calculate nu y
		Set<BDD> finalFixpoint = calculateNuY(R_Alpha);
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
			for (BDD productTran : productTrans) {
				for (BDD b : postxUnionI) {
					BDD post = getPost(b, productTran);
					if (post == null) {
						continue;
					}
					if (!temp.contains(post)) {
						temp.add(post);
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
	
	private Set<BDD> calculateR_Alpha(Set<BDD> fixpoint, Set<Integer> acceptingStates){
		Set<BDD> R_Alpha = new HashSet<BDD>();
		for (BDD b1 : fixpoint) {
			int c = b1.scanVar(mNwaTransitionBuilder.getNwaPc()[0]).intValue();
			if (acceptingStates.contains(c)) {
				R_Alpha.add(b1);
			}
		}
		return R_Alpha;
	}
	
	private Set<BDD> calculatePostTrue(Set<BDD> productTrans){
		Set<BDD> postTrue = new HashSet<BDD>();
		BDDPairing bp = bdd.makePair();
		bp.set(vprime, v);
		BDDPairing bp2 = bdd.makePair();
		bp.set(mRcfgTransitionBuilder.getRcfgPcPrime(), mRcfgTransitionBuilder.getRcfgPc());
		BDDPairing bp3 = bdd.makePair();
		bp.set(mNwaTransitionBuilder.getNwaPcPrime(), mNwaTransitionBuilder.getNwaPc());
		for (BDD b : productTrans) {
			BDD test = b;
//			mLogger.info(test);
			for (Object o : test.allsat()) {
				byte[] by = (byte[]) o;
				BDD test2 = bdd.one();
				int count = 0;
				for (byte b3 : by) {
					if (b3 == 0) {;
						test2 = test2.and(bdd.nithVar(count));
					}
					else if (b3 == 1) {
						test2 = test2.and(bdd.ithVar(count));
					}
					count++;
				}
				for (BDDDomain b2 : v) {
					test2 = test2.exist(b2.set());
				}
				for (BDDDomain b2 : mRcfgTransitionBuilder.getRcfgPc()) {
					test2 = test2.exist(b2.set());
				}
				for (BDDDomain b2 : mNwaTransitionBuilder.getNwaPc()) {
					test2 = test2.exist(b2.set());
				}
				test2 = test2.replace(bp);
				test2 = test2.replace(bp2);
				test2 = test2.replace(bp3);
				postTrue.add(test2);
			}
//			for (BDDDomain b2 : v) {
//				test.exist(b2.set());
//			}
//			for (BDDDomain b2 : mRcfgTransitionBuilder.getRcfgPc()) {
//				test.exist(b2.set());
//			}
//			for (BDDDomain b2 : mNwaTransitionBuilder.getNwaPc()) {
//				test.exist(b2.set());
//			}
			
//			List<Object> list = new ArrayList<Object>();
//			test.iterator(varset4).forEachRemaining(list::add);
		}
		return postTrue;
	}
	
	private Set<BDD> calculateNuY(Set<BDD> R_Alpha){
		Set<BDD> posty = R_Alpha;
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
	
	private Set<BDD> getProductTrans(List<BDD> rcfgTrans, List<BDD> nwaTrans){
		Set<BDD> productTrans = new HashSet<BDD>();
		
		for (BDD b1 : rcfgTrans) {
			for (BDD b2 : nwaTrans) {
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
		BDDBitVector pc1 = bdd.buildVector(mRcfgTransitionBuilder.getRcfgPc()[0]);
		BDDBitVector pc2 = bdd.buildVector(mRcfgTransitionBuilder.getRcfgPc()[1]);
		BDDBitVector pc3 = bdd.buildVector(mRcfgTransitionBuilder.getRcfgPc()[2]);
		BDDBitVector pc4 = bdd.buildVector(mNwaTransitionBuilder.getNwaPc()[0]);
		BDDBitVector xv = bdd.constantVector(2, 0);
		BDDBitVector pc1v = bdd.constantVector(2, 1);
		BDDBitVector pc2v = bdd.constantVector(2, 3);
		BDDBitVector pc3v = bdd.constantVector(5, 10);
		BDDBitVector pc4v = bdd.constantVector(1, 0);
		
//		BDDBitVector tPost0 = bdd.buildVector(v[1]);
//		BDDBitVector tPost0v = bdd.constantVector(2, 0);
		
		
		for (int n = 0; n < xPre.size(); n++) {
			input = input.and(xPre.getBit(n).biimp(xv.getBit(n)));
		}
		for (int n = 0; n < pc1.size(); n++) {
			input = input.and(pc1.getBit(n).biimp(pc1v.getBit(n)));
		}
		for (int n = 0; n < pc2.size(); n++) {
			input = input.and(pc2.getBit(n).biimp(pc2v.getBit(n)));
		}
		for (int n = 0; n < pc3.size(); n++) {
			input = input.and(pc3.getBit(n).biimp(pc3v.getBit(n)));
		}
		for (int n = 0; n < pc4.size(); n++) {
			input = input.and(pc4.getBit(n).biimp(pc4v.getBit(n)));
		}
//		for (int n = 0; n < tPost0.size(); n++) {
//			input = input.and(tPost0.getBit(n).biimp(tPost0v.getBit(n)));
//		}
//			
		return input;
	}
	
	public BDD getPost(BDD input, BDD transition) {
		BDD post = null;

		BDDDomain[] rcfgPc = mRcfgTransitionBuilder.getRcfgPc();
		BDDDomain[] nwaPc = mNwaTransitionBuilder.getNwaPc();
		BDDDomain[] rcfgPcPrime = mRcfgTransitionBuilder.getRcfgPcPrime();
		BDDDomain[] nwaPcPrime = mNwaTransitionBuilder.getNwaPcPrime();
		
		BDDPairing bp1 = bdd.makePair();
		BDDPairing bp2 = bdd.makePair();
		BDDPairing bp3 = bdd.makePair();
		bp1.set(vprime, v);
		bp2.set(rcfgPcPrime, rcfgPc);
		bp3.set(nwaPcPrime, nwaPc);
		
		BDD temp = transition.restrict(input).replace(bp1).replace(bp2).replace(bp3);
		
		if (temp.scanAllVar() != null) {
			byte[] changed = null;
			byte[] notChanged = null;
			byte[] newOne = null;
			for (Object o : temp.allsat()) {
				changed = (byte[]) o;
				newOne = changed;
			}
			for (Object o : input.allsat()) {
				notChanged = (byte[]) o;
			}
			for (int i = 0; i < changed.length; i++) {
				if (changed[i] == -1) {
					newOne[i] = notChanged[i];
				}
				else {
					newOne[i] = changed[i];
				}
			}
			BDD temp2 = bdd.one();
			for (int i = 0; i < newOne.length; i++) {
				if (newOne[i] == 0) {
					temp2 = temp2.and(bdd.nithVar(i));
				}
				else if (newOne[i] == 1) {
					temp2 = temp2.and(bdd.ithVar(i));
				}
			}
			post = temp2;
		}
		return post;
	}
}

//private boolean checkProperty (BDD input, BDD property) {
//	boolean sat = false;
//	byte[] inputByte = null;
//	for (Object i1 : input.allsat()) {
//		inputByte = (byte[]) i1;
//	}
//	for (Object b2 : property.allsat()) {
//		byte[] b3 = (byte[]) b2;
//		boolean temp = true;
//		for (int i = 0; i < b3.length; i++) {
//			if (b3[i] != -1) {
//				if (inputByte[i] != b3[i]) {
//					temp = false;
//				}
//			}
//		}
//		if (temp) {
//			sat = true;
//			break;
//		}
//	}
//	return sat;
//}

//private Set<BDD> calculateAccepting(){
//	Set<BDD> acceptingTrans = new HashSet<BDD>();
//	
//	for (BDD b1 : mRcfgTransitionBuilder.getRcfgTrans()) {
//		for (BDD b2 : mNwaTransitionBuilder.getNwaTrans()) {
//			if (mNwaTransitionBuilder.getNwaFinalTrans().contains(b2)) {
//				BDD a = b1.and(b2);
////				mLogger.info(a);
//				acceptingTrans.add(a);
//			}
//		}
//	}
//	return calculatePostTrue(acceptingTrans);
//}
