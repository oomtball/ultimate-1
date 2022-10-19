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
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
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
	private final INestedWordAutomaton<CodeBlock, String> mNWA;
	
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
		
		// services and logger
		mServices = services;
		mLogger = logger;
		mRcfgRoot = rcfg;
		mNWA = nwa;
		// set up BDD factory
		bdd = BDDFactory.init("j", 100000, 100000, false);
		
		// set up BDD domain
		Map<String, Set<Integer>> varAndValue = getVarAndValue();
		int[] pam = findRcfgNeededBits(varAndValue).stream().mapToInt(Integer::intValue).toArray();
		v = bdd.extDomain(pam); // represents different v bdd variables
		vprime = bdd.extDomain(pam); // represents different vprime bdd variables
		Set<String> varOrder = varAndValue.keySet();
		
		
		// create RCFG transition builder which can help getting transitions of the system RCFG
		mRcfgTransitionBuilder = new RcfgTransitionBuilder(rcfg, mLogger, mServices, bdd, v, vprime, varOrder);
		// create NWA transition builder which can help getting transitions of the property NWA
		mNwaTransitionBuilder = new NwaTransitionBuilder(nwa, mLogger, mServices, bdd, v, vprime, varOrder);
		
		BDD input = setInput();
		
		mLogger.info(Arrays.toString(mRcfgTransitionBuilder.getRcfgTrans().toArray()));
		mLogger.info(Arrays.toString(mNwaTransitionBuilder.getNwaTrans().toArray()));
		
		Set<BDD> productTrans = new HashSet<BDD>();
		for (BDD b1 : mRcfgTransitionBuilder.getRcfgTrans()) {
			for (BDD b2 : mNwaTransitionBuilder.getNwaTrans()) {
				mLogger.info(b1.and(b2));
				productTrans.add(b1.and(b2));
			}
		}
		mLogger.info(Arrays.toString(productTrans.toArray()));
	}
	
	private Map<String, Set<Integer>> getVarAndValue(){
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
		BDDBitVector xv = bdd.constantVector(2, 2);
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
}
