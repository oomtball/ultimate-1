package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.CallStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.ForkStatement;
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
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ForkThreadCurrent;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Summary;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDBitVector;
import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm.NwaTransitionBuilder;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm.RcfgTransitionBuilder;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

public class FixpointModelCheckerForBDD {
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private final BoogieIcfgContainer mRcfgRoot;
	private final INestedWordAutomaton<CodeBlock, String> mNwa;
	Set<String> varOrder;
	Set<String> threadOrder;
	
	Set<AssignmentStatement> allAssignmentStatement;
	Set<CallStatement> allCallStatement;
	List<Pair<String, Expression[]>> threadAndInput;
	Map<String, Integer> arrayWithLength;
	
	private final NwaTransitionBuilder mNwaTransitionBuilder;
	private final RcfgTransitionBuilder mRcfgTransitionBuilder;
	
	static BDDFactory bdd;
	BDDDomain[] v; // represents different v bdd variables
	BDDDomain[] vprime; // represents different vprime bdd variables
	BDDDomain[] rcfgPc;
	BDDDomain[] nwaPc;
	BDDDomain[] rcfgPcPrime;
	BDDDomain[] nwaPcPrime;
	BDDPairing bp1;
	BDDPairing bp2;
	BDDPairing bp3;
	BDDPairing bp4;
	BDDPairing bp5;
	BDDPairing bp6;
	
	BDD vset;
	BDD rcfgPcSet;
	BDD nwaPcSet;
	BDD vprimeSet;
	BDD rcfgPcPrimeSet;
	BDD nwaPcPrimeSet;
	BDD allPreVar;
	BDD allPostVar;
	List<Integer> allPreVarSet;
	List<Integer> allPostVarSet;
	
	Set<BDD> productTrans;
	
	public FixpointModelCheckerForBDD(final INestedWordAutomaton<CodeBlock, String> nwa, final BoogieIcfgContainer rcfg,
			final ILogger logger, final IUltimateServiceProvider services) {
		
		// services and logger+
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNwa = nwa;
		// set up BDD factory
		bdd = BDDFactory.init("j", 1000000, 1000000, false);
		
		// set up BDD domain
		allAssignmentStatement = new HashSet<AssignmentStatement>();
		allCallStatement = new HashSet<CallStatement>();
		threadAndInput = new ArrayList<Pair<String, Expression[]>>();
		arrayWithLength = new HashMap<String, Integer>();
		getAllStatements();
		
		Set<String> allVar = getAllVar(allAssignmentStatement, allCallStatement);
//		mLogger.info(arrayWithLength);
		
		int systemNeedBit = 3;
		int propertyNeedBit = findPropertyNeedBit(mNwa);
		int actuallyNeedBit = 0;
		if (systemNeedBit >= propertyNeedBit) {
			actuallyNeedBit = systemNeedBit;
		}
		else {
			actuallyNeedBit = propertyNeedBit;
		}
		
		int[] pam = setPam(actuallyNeedBit, allVar.size()).stream().mapToInt(Integer::intValue).toArray();
		v = bdd.extDomain(pam); // represents different v bdd variables
		vprime = bdd.extDomain(pam); // represents different vprime bdd variables
		varOrder = allVar;
//		mLogger.info(Arrays.toString(varOrder.toArray()));
		
		// create RCFG transition builder which can help getting transitions of the system RCFG
		mRcfgTransitionBuilder = new RcfgTransitionBuilder(mRcfgRoot, mLogger, mServices, bdd, v, vprime, varOrder, threadAndInput, arrayWithLength);
		// create NWA transition builder which can help getting transitions of the property NWA
		mNwaTransitionBuilder = new NwaTransitionBuilder(mNwa, mLogger, mServices, bdd, v, vprime, varOrder);
		
		threadOrder = mRcfgTransitionBuilder.getThreadOrder();
		
		List<BDD> rcfgTrans = mRcfgTransitionBuilder.getRcfgTrans();
		List<BDD> nwaTrans = mNwaTransitionBuilder.getNwaTrans();
//		for (BDD b : nwaTrans) {
//			mLogger.info(b);
//		}
		rcfgPc = mRcfgTransitionBuilder.getRcfgPc();
		nwaPc = mNwaTransitionBuilder.getNwaPc();
		rcfgPcPrime = mRcfgTransitionBuilder.getRcfgPcPrime();
		nwaPcPrime = mNwaTransitionBuilder.getNwaPcPrime();
		bp1 = bdd.makePair();
		bp2 = bdd.makePair();
		bp3 = bdd.makePair();
		bp4 = bdd.makePair();
		bp5 = bdd.makePair();
		bp6 = bdd.makePair();
		bp1.set(vprime, v);
		bp2.set(rcfgPcPrime, rcfgPc);
		bp3.set(nwaPcPrime, nwaPc);
		bp4.set(v, vprime);
		bp5.set(rcfgPc, rcfgPcPrime);
		bp6.set(nwaPc, nwaPcPrime);
		
		vset = bdd.makeSet(v);
		rcfgPcSet = bdd.makeSet(rcfgPc);
		nwaPcSet = bdd.makeSet(nwaPc);
		vprimeSet = bdd.makeSet(vprime);
		rcfgPcPrimeSet = bdd.makeSet(rcfgPcPrime);
		nwaPcPrimeSet = bdd.makeSet(nwaPcPrime);
		allPreVar = vset.and(rcfgPcSet).and(nwaPcSet);
		allPostVar = vprimeSet.and(rcfgPcPrimeSet).and(nwaPcPrimeSet);
		allPreVarSet = Arrays.stream(allPreVar.scanSet()).boxed().collect(Collectors.toList());
		allPostVarSet = Arrays.stream(allPostVar.scanSet()).boxed().collect(Collectors.toList());
		
//		mLogger.info("system : " + Arrays.toString(rcfgTrans.toArray()));
//		mLogger.info("system pc : " + Arrays.toString(mRcfgTransitionBuilder.getRcfgTransPc().toArray()));
//		for (BDD b : nwaTrans) {
//			mLogger.info(b);
//		}
//		mLogger.info("property : " + Arrays.toString(nwaTrans.toArray()));
//		mLogger.info("property pc : " + Arrays.toString(mNwaTransitionBuilder.getNwaTransPc().toArray()));
//		mLogger.info("property final trans : " + Arrays.toString(mNwaTransitionBuilder.getNwaFinalTrans().toArray()));
		
		List<BDD> initialTrans = mRcfgTransitionBuilder.getInitialTrans();
//		for (BDD b : initialTrans) {
//			mLogger.info(b);
//		}
		
		// calculate I 
		Set<BDD> I = createInitial(initialTrans);
		mLogger.info("Finish initialization.");
//		for (BDD b : I) {
//			mLogger.info(translate(b));
//		}
//		mLogger.info("input : " + Arrays.toString(I.toArray()));

		List<BDD> normalTrans = new ArrayList<BDD>();
		for (BDD b : rcfgTrans) {
			if (!initialTrans.contains(b)) {
				normalTrans.add(b);
			}
		}
//		mLogger.info("normal transition : " + Arrays.toString(normalTrans.toArray()));
		
//		// calculate cross product
		productTrans = getProductTrans(normalTrans, nwaTrans);	
//		mLogger.info(productTrans.size());
//		for (BDD b : productTrans) {
//			mLogger.info(b);
//		}
		mLogger.info("Finish cross product.");
		
		// calculate the fixpoint mu x
		Set<BDD> initialFixpoint = calculateMuX(I);
		mLogger.info("mu x size : " + initialFixpoint.size());
//		mLogger.info("Finish calculateMuX().");

		// calculate set of accepting states
		Set<Integer> acceptingStates = mNwaTransitionBuilder.getNwaFinalStatesPc();
//		mLogger.info("accepting states : " + Arrays.toString(acceptingStates.toArray()));
	
		// calculate R_Alpha
		Set<BDD> R_Alpha = calculateR_Alpha(initialFixpoint, acceptingStates);
		mLogger.info("R_Alpha size : " + R_Alpha.size());
		mLogger.info("Finish calculating R_Alpha.");
		
		// calculate nu y
		Set<BDD> finalFixpoint = calculateF_phi(R_Alpha, I);
		mLogger.info("nu y size : " + finalFixpoint.size());
		mLogger.info("Finish calculating F_phi.");

		// check specifications
		finalCheck(finalFixpoint);
	}
	
	private void getAllStatements(){
		for (String s : mRcfgRoot.getProgramPoints().keySet()) {
			for (DebugIdentifier s2 : mRcfgRoot.getProgramPoints().get(s).keySet()) {
				for (IcfgEdge i : mRcfgRoot.getProgramPoints().get(s).get(s2).getOutgoingEdges()) {
					if (i instanceof StatementSequence) {
						StatementSequence newI = (StatementSequence) i;
						for (Statement s3 : newI.getStatements()) {
							String as = "AssignmentStatement";
							if (s3.getClass().getSimpleName().equals(as)) {
								AssignmentStatement news3 = (AssignmentStatement) s3;
								allAssignmentStatement.add(news3);
							}
						}
					}
					else if (i instanceof Summary) {
						Summary newI = (Summary) i;
						CallStatement cs = newI.getCallStatement();
						allCallStatement.add(cs);
					}
					else if (i instanceof ForkThreadCurrent) {
						ForkThreadCurrent newI = (ForkThreadCurrent) i;
						ForkStatement fs = newI.getForkStatement();
//						mLogger.info(fs);
						Pair<String, Expression[]> temp = new Pair<>(fs.getProcedureName(), fs.getArguments());
						threadAndInput.add(temp);
					}
				}
			}
		}
	}
	
	private Set<String> getAllVar(Set<AssignmentStatement> allAssignmentStatements, Set<CallStatement> allCallStatements){
		Set<String> allVar = new HashSet<String>();
		for (AssignmentStatement as : allAssignmentStatements) {
			for (LeftHandSide lhs : as.getLhs()) {
				VariableLHS lhs2 = (VariableLHS) lhs;
				String varCandidate = lhs2.getIdentifier();
//				String c1 = "#";
//				String c2 = "#t~post";
//				String c3 = "offset";
//				String c4 = "base";
//	
//				if (varCandidate.contains(c2) && !varCandidate.contains(c3) && !varCandidate.contains(c4)) {
//					allVar.add(varCandidate);
//				}
//				if (!varCandidate.contains(c1) && !varCandidate.contains(c3) && !varCandidate.contains(c4)) {
//					allVar.add(varCandidate);
//				}
				String pre = "main_#t~pre";
				String nul = "#NULL";
				String val = "#valid";
				String res = "res";
				String ret = "ret";
				String pthr = "#pthreadsForks";
				if (!varCandidate.contains(pre) && !varCandidate.contains(nul) && !varCandidate.contains(res) && 
						!varCandidate.contains(val) && !varCandidate.contains(pthr) && !varCandidate.contains(ret)) {
					allVar.add(varCandidate);
				}
			}
		}
		for (CallStatement cs : allCallStatements) {
			String ua = "#Ultimate.allocOnStack";
			String ri = "read~int";
			if (cs.getMethodName().equals(ua)) {
				String result = "";
				String c = "main_~#t";
				IntegerLiteral il = (IntegerLiteral) cs.getArguments()[0];
				int t = Integer.parseInt(il.getValue());
				for (VariableLHS lhs : cs.getLhs()) {
					if (!lhs.getIdentifier().contains(c)) {
						if (!allVar.contains(lhs.getIdentifier())) {
//							mLogger.info(lhs.getIdentifier());
							allVar.add(lhs.getIdentifier());
						}	
						result = lhs.getIdentifier().replace(".base", "");
						result = result.replace(".offset", "");
						arrayWithLength.put(result, t/4);
						for (int i = 0; i < t/4; i++) {
							String result2 = result + "~"+ i;
							if (!allVar.contains(result2)){
								allVar.add(result2);
							}
						}
					}
				}
			}
			else if (cs.getMethodName().equals(ri)) {
				String c = "main_~#t";
				String c2 = "main_#t~mem";
				for (VariableLHS lhs : cs.getLhs()) {
					if (!lhs.getIdentifier().contains(c) && !lhs.getIdentifier().contains(c2)) {
						allVar.add(lhs.getIdentifier());
					}
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
	
	private BDD setAllZeroInput() {
		BDD input = bdd.one();
		BDDDomain[] rcfgPc = mRcfgTransitionBuilder.getRcfgPc();
		for (int i = 0; i < rcfgPc.length; i++) {
			BDDBitVector pc = bdd.buildVector(rcfgPc[i]);
			BDDBitVector pcv = bdd.constantVector(pc.size(), 0);
			for (int n = 0; n < pc.size(); n++) {
				input = input.and(pc.getBit(n).biimp(pcv.getBit(n)));
			}
		}
		return input;
	}
	
	private Set<BDD> createInitial(List<BDD> initialTrans){
		BDD input = setAllZeroInput();
		Set<BDD> temp = new HashSet<BDD>();
		temp.add(input);
		while (true) {
			Set<BDD> temp2 = new HashSet<BDD>();
			for (BDD b : initialTrans) {
				for (BDD b2 : temp) {
					BDD post = getPost(b2, b);
					if (post != null) {
//						mLogger.info("hi");
						temp2.add(post);
					}
				}
			}
			if (temp2.isEmpty()) {
				break;
			}
			else {
				temp = temp2;
			}
		}
		Set<BDD> I = temp;
		Set<Integer> nwaInitialPc = mNwaTransitionBuilder.getNwaInitialStatesPc();
		Set<BDD> nwaInitialBDD = new HashSet<BDD>();
		for (Integer i : nwaInitialPc) {
			BDD nwaInitial = bdd.one();
			BDDBitVector pc = bdd.buildVector(mNwaTransitionBuilder.getNwaPc()[0]);
			BDDBitVector pcv = bdd.constantVector(pc.size(), i);
			for (int n = 0; n < pc.size(); n++) {
				nwaInitial = nwaInitial.and(pc.getBit(n).biimp(pcv.getBit(n)));
			}
			nwaInitialBDD.add(nwaInitial);
		}
		Set<BDD> ans = new HashSet<BDD>();
		for (BDD a : I) {
			for(BDD b : nwaInitialBDD) {
				BDD o = a.and(b);
				ans.add(o);
			}
		}
		return ans;
	}
	
	private Set<BDD> calculateMuX(Set<BDD> i){
		Set<BDD> postx = new HashSet<>();
		List<Integer> sz = new ArrayList<Integer>();
		while (true) {
			Set<BDD> temp = getPost4(postx, productTrans);
			Set<BDD> postxUnionI = new HashSet<BDD>(i);
			postxUnionI.addAll(temp);
			mLogger.info(postxUnionI.size());
			sz.add(postxUnionI.size());
			if (postxUnionI.equals(postx)) {
				break;
			}
			else {
				postx = postxUnionI;
			}
		}
		mLogger.info(Arrays.toString(sz.toArray()));
		return postx;
	}
	
	private Set<BDD> calculateR_Alpha(Set<BDD> fixpoint, Set<Integer> acceptingStates){
//		mLogger.info(Arrays.toString(acceptingStates.toArray()));
		Set<BDD> R_Alpha = new HashSet<BDD>();
		for (BDD b1 : fixpoint) {
			int c = b1.scanVar(mNwaTransitionBuilder.getNwaPc()[0]).intValue();
			if (acceptingStates.contains(c)) {
				R_Alpha.add(b1);
			}
		}
		return R_Alpha;
	}
	
	private Set<BDD> calculateF_phi(Set<BDD> R_Alpha, Set<BDD> I){
		Set<BDD> postTrue = R_Alpha;
		postTrue.removeAll(I);
		Set<BDD> posty = postTrue;
		while (true) {
			Set<BDD> temp = getPost2(posty, productTrans);
			Set<BDD> postyInterR = new HashSet<BDD>(R_Alpha);
			postyInterR.retainAll(temp);
//			Set<BDD> result = postyInterR;
//			mLogger.info(result.size());
			Set<BDD> result = calculateMuX(postyInterR);
			if (result.equals(posty)) {
//				for (BDD b : posty) {
//					mLogger.info(translate(b));
//				}
				break;
			}
			else {
				posty = result;
			}
//			for (BDD b : posty) {
//				mLogger.info(translate(b));
//			}
//			mLogger.info("finish");
		}
		return posty;
	}
	
	private void finalCheck(Set<BDD> fixpoint2){
		Map<String, Set<Integer>> finishLocation = mRcfgTransitionBuilder.getFinishPcForEachThread();
		BDDDomain[] rcfgPc = mRcfgTransitionBuilder.getRcfgPc();
		boolean propertyHold = true;
		for (BDD b : fixpoint2) {
			boolean allSame = true;
			for (int i = 0; i < rcfgPc.length; i++) {
				String thread = null;
				if (i == 0) {
					thread = "ULTIMATE.start";
				}
				else {
					thread = threadAndInput.get(i-1).getFirst();
				}
				Set<Integer> si = finishLocation.get(thread);
				if (!si.contains(b.scanVar(rcfgPc[i]).intValue())) {
					allSame = false;
				}
			}
			if (allSame) {
				propertyHold = false;
				break;
			}
		}
		if (propertyHold) {
			mLogger.info("All specifications hold.");
		}
		else {
			mLogger.info("Specifications do not hold.");
		}
	}
	
	private void finalCheck2(Set<BDD> fixpoint2){
		if (fixpoint2.isEmpty()) {
			mLogger.info("All specifications hold.");
		}
		else {
			mLogger.info("Specifications do not hold.");
		}
	}
	
	private Set<BDD> getProductTrans(List<BDD> rcfgTrans, List<BDD> nwaTrans){
		Set<BDD> productTrans = new HashSet<BDD>();
		
		for (BDD b1 : rcfgTrans) { // all BDD transitions of system RCFG
			for (BDD b2 : nwaTrans) { // all BDD transitions of property NWA
				BDD a = b1.and(b2); // do AND operations 
				if (a.nodeCount() != 0) { // check whether the result is NULL or not
					productTrans.add(a);
				}
			}
		}
		return productTrans; // return the result
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
	
	public BDD getPost(BDD input, BDD transition) {
		BDD post = null;
		
//		Map<String, Set<Integer>> finishPcForEachThread = mRcfgTransitionBuilder.getFinishPcForEachThread();

		BDD vset = bdd.makeSet(v);
		BDD rcfgPcSet = bdd.makeSet(rcfgPc);
		BDD nwaPcSet = bdd.makeSet(nwaPc);
		BDD preSet = vset.and(rcfgPcSet).and(nwaPcSet);
		
		// check whether the state is the last state
//		boolean last = true;
//		for (int i = 0; i < rcfgPc.length; i++) {
//			String thread = null;
//			if (i == 0) {
//				thread = "ULTIMATE.start";
//			}
//			else {
//				thread = threadAndInput.get(i-1).getFirst();
//			}
//			Set<Integer> fset = finishPcForEachThread.get(thread);
//			int a = input.scanVar(rcfgPc[i]).intValue();
//			if (!(fset.contains(a))) {
//				last = false;
//			}
//		}
//		if (last) {
//			return input;
//		}
		
		// check whether the input will do this transition
		BDD temp = transition.restrict(input);
		int[] preVarNum = preSet.scanSet();
		List<List<Integer>> tempByte = new ArrayList<List<Integer>>();
		for (Object o : temp.allsat()) {
			byte[] btemp = (byte[]) o;
			List<Integer> itemp = new ArrayList<Integer>();
			for (int i = 0; i < btemp.length; i++) {
				if (btemp[i] != -1) {
					itemp.add(i);
				}
			}
			tempByte.add(itemp);
		}
		
		List<byte[]> haveToDo = new ArrayList<byte[]>();
		int count = 0;
		for (List<Integer> li : tempByte) {
			Boolean contain = false;
			for (Integer i1 : preVarNum) {
				for (Integer i2 : li) {
					if (i1 == i2) {
						contain = true;
					}
				}
			}
			if (!contain) {
				haveToDo.add((byte[]) temp.allsat().get(count));
			}
			count++;
		}
		BDD contToDo = bdd.zero();
		if (haveToDo.isEmpty()) {
			return post;
		}
		else {
			for (byte[] b : haveToDo) {
				BDD temp2 = bdd.one();
				for (int i = 0; i < b.length; i++) {
					if (b[i] == 0) {
						temp2 = temp2.and(bdd.nithVar(i));
					}
					else if (b[i] == 1) {
						temp2 = temp2.and(bdd.ithVar(i));
					}
				}
				contToDo = contToDo.or(temp2);
			}
		}
		temp = contToDo;
		temp = temp.replace(bp1).replace(bp2).replace(bp3);
		
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
//		mLogger.info("transition : " + transition);
//		mLogger.info("input : " + input);
//		mLogger.info("post : " + post);
		return post;
	}

	public Set<BDD> getPost2(Set<BDD> inputs, Set<BDD> transitions) {
		Set<BDD> result = new HashSet<BDD>();
		
		BDD vset = bdd.makeSet(v);
		BDD rcfgPcSet = bdd.makeSet(rcfgPc);
		BDD nwaPcSet = bdd.makeSet(nwaPc);
		BDD preSet = vset.and(rcfgPcSet).and(nwaPcSet);
		
		for (BDD input : inputs) {
			for (BDD transition : transitions) {
				BDD post = null;
				
				// check whether the input will do this transition
				BDD temp = transition.restrict(input);
				int[] preVarNum = preSet.scanSet();
				List<List<Integer>> tempByte = new ArrayList<List<Integer>>();
				for (Object o : temp.allsat()) {	
					byte[] btemp = (byte[]) o;
					List<Integer> itemp = new ArrayList<Integer>();
					for (int i = 0; i < btemp.length; i++) {
						if (btemp[i] != -1) {
							itemp.add(i);
						}
					}
					tempByte.add(itemp);
				}
				
				List<byte[]> haveToDo = new ArrayList<byte[]>();
				int count = 0;
				for (List<Integer> li : tempByte) {
					Boolean contain = false;
					for (Integer i1 : preVarNum) {
						for (Integer i2 : li) {
							if (i1 == i2) {
								contain = true;
							}
						}
					}
					if (!contain) {
						haveToDo.add((byte[]) temp.allsat().get(count));
					}
					count++;
				}
				BDD contToDo = bdd.zero();
				if (haveToDo.isEmpty()) {
					continue;
				}
				else {
					for (byte[] b : haveToDo) {
						BDD temp2 = bdd.one();
						for (int i = 0; i < b.length; i++) {
							if (b[i] == 0) {
								temp2 = temp2.and(bdd.nithVar(i));
							}
							else if (b[i] == 1) {
								temp2 = temp2.and(bdd.ithVar(i));
							}
						}
						contToDo = contToDo.or(temp2);
					}
				}
				temp = contToDo;
				temp = temp.replace(bp1).replace(bp2).replace(bp3);
				
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
//				mLogger.info("transition : " + transition);
//				mLogger.info("input : " + input);
//				mLogger.info("post : " + post);
				result.add(post);
			}
		}
		
		return result;
	}
	
	public Set<BDD> getPost3(Set<BDD> inputs, Set<BDD> transitions) {
		Set<BDD> result = new HashSet<BDD>();
		
		for (BDD input : inputs) {
			List<Integer> inputVarUsed = new ArrayList<Integer>();
			byte[] inputAssignment = null;
			for (Object o : input.allsat()) {
				inputAssignment = (byte[]) o;
				for (int i = 0; i < inputAssignment.length; i++) {
					if (inputAssignment[i] == -1) {
						continue;
					}
					else {
						inputVarUsed.add(i);
					}
				}
			}
			
			for (BDD transition : transitions) {
				BDD post = bdd.zero();
				if (input.nodeCount() == 0) {
					continue;
				}
				List<byte[]> transitionAssignment = new ArrayList<byte[]>();
				for (Object o : transition.allsat()) {
					byte[] t = (byte[]) o;
					transitionAssignment.add(t);
				}
				
				for (byte[] b : transitionAssignment) {
					List<Integer> transitionVarUsed = new ArrayList<Integer>();
					for (int i = 0; i < b.length; i++) {
						if (b[i] == -1) {
							continue;
						}
						else {
							transitionVarUsed.add(i);
						}
					}
					List<Integer> transitionOnlyPreVar = new ArrayList<Integer>(allPreVarSet);
					List<Integer> transitionOnlyPostVar = new ArrayList<Integer>(allPostVarSet);
					transitionOnlyPreVar.retainAll(transitionVarUsed);
					transitionOnlyPostVar.retainAll(transitionVarUsed);
					
					List<Integer> check1 = new ArrayList<Integer>(transitionOnlyPreVar);
					check1.removeAll(inputVarUsed);
					if (!check1.isEmpty()) { // the input state can not do this transition
						// post is zero
						continue;
					}
					else { // the input state can do this transition possibly
						boolean canDo = true;
						for (Integer i : transitionOnlyPreVar) { // check whether the value of all pre vars of the input and the transition are the same
							if (b[i] != inputAssignment[i]) {
								canDo = false;
								break;
							}
						}
						if (!canDo) { // cannot do
							continue;
						}
						// can do
						if (transitionOnlyPreVar.equals(inputVarUsed)) { // with no lost variables
							// build one transition BDD and do restrict function with the input BDD
							// post is not zero
							post = transition.restrict(input);
							result.add(post);
							break;
						}
						else { // with some lost variables
							// post is not zero
							BDD tempPost = bdd.one();
							for (Integer i : transitionOnlyPostVar) {
								if (b[i] == 1) {
									tempPost = tempPost.and(bdd.ithVar(i));
								}
								else if (b[i] == 0) {
									tempPost = tempPost.and(bdd.nithVar(i));
								}
							}
							tempPost = tempPost.replace(bp1).replace(bp2).replace(bp3);
							List<Integer> varAlreadyDone = new ArrayList<Integer>();
							for (Object o : tempPost.allsat()) {
								byte[] tempPostAssignment = (byte[]) o;
								for (int i = 0; i < tempPostAssignment.length; i++) {
									if (tempPostAssignment[i] != -1) {
										varAlreadyDone.add(i);
									}
								}
							}
							for (int i : inputVarUsed) {
								if (!varAlreadyDone.contains(i)) {
									if (inputAssignment[i] == 1) {
										tempPost = tempPost.and(bdd.ithVar(i));
									}
									else if (inputAssignment[i] == 0) {
										tempPost = tempPost.and(bdd.nithVar(i));
									}
								}
							}
							result.add(tempPost);
						}
					}
				}
			}
		}
		return result;
	}
	
	public Set<BDD> getPost4(Set<BDD> inputs, Set<BDD> transitions) {
		Set<BDD> result = new HashSet<BDD>();
		
		for (BDD input : inputs) {
			byte[] inputByte = (byte[]) input.allsat().get(0);
			for (BDD transition : transitions) {
				BDD post = null;
				// check whether the input will do this transition
				BDD temp = transition.restrict(input);
				if (temp.allsat().size() == 0) {
					continue;
				}
				boolean canDo = true;
				byte[] tranAfterRestrict = (byte[]) temp.allsat().get(0);
				for (int i = 0; i < tranAfterRestrict.length; i++) {
					if (tranAfterRestrict[i] != -1) {
						if (allPreVarSet.contains(i)) {
							canDo = false;
							break;
						}
					}
				}
				if (canDo) {
					BDD temp2 = temp.replace(bp1).replace(bp1).replace(bp1);
					List<Integer> temp2Used = new ArrayList<Integer>();
					byte[] temp2Byte = (byte[]) temp2.allsat().get(0);
					for (int i = 0; i < temp2Byte.length; i++) {
						if (temp2Byte[i] != -1) {
							temp2Used.add(i);
						}
					}
					BDD inputOnlyUnchanged = bdd.one(); // all unchanged in input
					for (int i = 0; i < inputByte.length; i++) {
						if (!temp2Used.contains(i)) {
							if (inputByte[i] == 1) {
								inputOnlyUnchanged = inputOnlyUnchanged.and(bdd.ithVar(i));
							}
							else if (inputByte[i] == 0) {
								inputOnlyUnchanged = inputOnlyUnchanged.and(bdd.nithVar(i));
							}
						}
					}
					post = inputOnlyUnchanged.and(temp2);
					result.add(post);
				}
			}
		}
		
		return result;
	}
	
	private String translate(BDD input) {
		String result = "";
		for (int c = 0; c < v.length; c++) {
			result = result + varOrder.toArray()[c] + "=" + input.scanVar(v[c]).intValue() + "; ";
		}
		result = result + " at ";
		for (int c = 0; c < mRcfgTransitionBuilder.getRcfgPc().length; c++) {
			result = result + input.scanVar(mRcfgTransitionBuilder.getRcfgPc()[c]).intValue() + ", ";
		}
		result = result + input.scanVar(mNwaTransitionBuilder.getNwaPc()[0]).intValue();
		return result;
	}
}

