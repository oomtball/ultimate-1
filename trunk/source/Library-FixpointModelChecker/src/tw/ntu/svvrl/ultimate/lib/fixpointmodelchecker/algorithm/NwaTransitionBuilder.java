package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.IncomingInternalTransition;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BooleanLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDBitVector;
import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDFactory;

public class NwaTransitionBuilder {
	final INestedWordAutomaton<CodeBlock, String> mNwa; 
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	static BDDFactory bdd;
	BDDDomain[] v;
	BDDDomain[] vprime;
	Set<String> varOrder;
	int pcNeedBit;
	BDDDomain[] nwaPc;
	BDDDomain[] nwaPcPrime;
	Set<String> nwaStateOrder;
	
	private final AssumeStatementEvalator mAssumeStatementEvalator;
	
	public List<BDD> nwaTrans = new ArrayList<BDD>();
	public Set<Integer> nwaInitialStatesPc = new HashSet<Integer>();
	public Set<Integer> nwaFinalStatesPc = new HashSet<Integer>();
	public Map<String, Integer> nwaStateToPc = new HashMap<String, Integer>();
	public List<Pair<Integer, Integer>> nwaTransPc = new ArrayList<>();
	
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String bl = "BooleanLiteral";
	String ue = "UnaryExpression";
	String be = "BinaryExpression";
	
	public NwaTransitionBuilder(INestedWordAutomaton<CodeBlock, String> nwa, final ILogger logger, final IUltimateServiceProvider services, 
			BDDFactory _bdd, BDDDomain[] _v, BDDDomain[] _vprime, Set<String> _varOrder) {
		mNwa = nwa;
		mServices = services;
		mLogger = logger;
		
		bdd = _bdd;
		v = _v;
		vprime = _vprime;
		varOrder = _varOrder;
		
		List<Integer> temp = new ArrayList<Integer>();
		temp.add((int) Math.pow(2, pcNeedBit));
		int[] pam = temp.stream().mapToInt(Integer::intValue).toArray();
		nwaPc = bdd.extDomain(pam);
		nwaPcPrime = bdd.extDomain(pam);
		nwaStateOrder = nwa.getStates();
		
		mAssumeStatementEvalator = new AssumeStatementEvalator(logger, services, 
				bdd, _v, _vprime, varOrder);
		
		nwaStateToPc = setNwaPc();
		List<Expression> allExpression = getNwaExpression(mNwa.getAlphabet());
//		mLogger.info(Arrays.toString(allExpression.toArray()));
		buildNwaTrans(allExpression);
		
//		BDD test = bdd.zero();
//		for (BDD b : nwaTrans) {
//			test = test.or(b);
//		}
//		mLogger.info(test);
	}
	
	private List<Expression> getNwaExpression(Set<CodeBlock> al) {
		List<Expression> allExpression = new ArrayList<>();
		for (String s : nwaStateOrder) {
			for (IncomingInternalTransition i : mNwa.internalPredecessors(s)) {
				String test = (String) i.getPred();
				Pair<Integer, Integer> p =  new Pair<Integer, Integer>(nwaStateToPc.get(test), nwaStateToPc.get(s));
				nwaTransPc.add(p);
				if (mNwa.isFinal(s)) {
					nwaFinalStatesPc.add(nwaStateToPc.get(s));
				}
				else if (mNwa.isInitial(s)) {
					nwaInitialStatesPc.add(nwaStateToPc.get(s));
				}
				StatementSequence ss = (StatementSequence) i.getLetter();
				
				for (Statement s2 : ss.getStatements()) {
					if (s2 instanceof AssumeStatement) {
						AssumeStatement as = (AssumeStatement) s2;
						allExpression.add(as.getFormula());
					}
				}
			}
		}
		return allExpression;
	}
	
	private Map<String, Integer> setNwaPc(){
		Map<String, Integer> nwaPc = new HashMap<String, Integer>();
		int tempPc = 0;
		for (String s : nwaStateOrder) {
			nwaPc.put(s, tempPc);
			tempPc++;
		}
		pcNeedBit = Integer.toBinaryString(tempPc).length();
		return nwaPc;
	}
	
	private void buildNwaTrans(List<Expression> allExpression) {
		int count = 0;
		for (Expression expr : allExpression) {
			// deal with transitions
			BDD transition = mAssumeStatementEvalator.buildTran(expr);
			
			// deal with PCs
			BDD transitionWithPc = addPc(transition, count);
			
			nwaTrans.add(transitionWithPc);
			count++;
		}
	}
	
	private BDD addPc(BDD t, int count) {
		BDD transition = t;
		BDDBitVector prePc = bdd.buildVector(nwaPc[0]);
		BDDBitVector postPc = bdd.buildVector(nwaPcPrime[0]);
		BDDBitVector preBl = bdd.constantVector(prePc.size(), nwaTransPc.get(count).getFirst());
		BDDBitVector postBl = bdd.constantVector(postPc.size(), nwaTransPc.get(count).getSecond());
		
		for (int i = 0; i < prePc.size(); i++) {
			transition = transition.andWith(prePc.getBit(i).biimp(preBl.getBit(i)));
		}
		for (int i = 0; i < postPc.size(); i++) {
			transition = transition.andWith(postPc.getBit(i).biimp(postBl.getBit(i)));
		}
		return transition;
	}
	
	public List<BDD> getNwaTrans(){
		return nwaTrans;
	}
	
	public List<Pair<Integer, Integer>> getNwaTransPc(){
		return nwaTransPc;
	}

	public BDDDomain[] getNwaPc() {
		return nwaPc;
	}
	
	public BDDDomain[] getNwaPcPrime() {
		return nwaPcPrime;
	}
	
	public Set<Integer> getNwaFinalStatesPc() {
		return nwaFinalStatesPc;
	}
	
	public Set<Integer> getNwaInitialStatesPc() {
		return nwaInitialStatesPc;
	}
}
