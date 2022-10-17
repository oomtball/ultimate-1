package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
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
	
	public List<BDD> nwaTrans = new ArrayList<BDD>();
	
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
		
		List<Expression> allExpression = getNwaExpression(mNwa.getAlphabet());
		buildNwaTrans(allExpression, bdd);
		
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
	
	private void buildNwaTrans(List<Expression> allExpression, BDDFactory bdd) {
		for (Expression expr : allExpression) {
			BDD transition = bdd.one();
			mLogger.info(expr);
			if (expr instanceof BooleanLiteral) {
				BooleanLiteral newExpr = (BooleanLiteral) expr;
				Boolean newExprBooleanValue = newExpr.getValue();
				if (!newExprBooleanValue) {
					transition = bdd.zero();
				}
			}
			else if (expr instanceof UnaryExpression) {
				UnaryExpression newExpr = (UnaryExpression) expr;
				String checkNot = newExpr.getOperator().toString();
				String LOGICNEG = "LOGICNEG";
				BDD tempBdd = null;
				if (newExpr.getExpr().getClass().getSimpleName().equals(be)) {
					BinaryExpression newBe = (BinaryExpression) newExpr.getExpr();
					Expression binaryLeft = newBe.getLeft();
					Expression binaryRight = newBe.getRight();
					String ope = newBe.getOperator().toString();
					String leftClass = binaryLeft.getClass().getSimpleName();
					String rightClass = binaryRight.getClass().getSimpleName();
					BDDBitVector binaryLeftThing = null; 
					BDDBitVector binaryRightThing = null; 
					if (leftClass.equals(ie) && rightClass.equals(il)) {
						IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
						IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
						int count = 0;
						for (String s : varOrder) {
							if (s.equals(newBinaryLeft.getIdentifier())) {
								break;
							}
							count++;
						}
						binaryLeftThing = bdd.buildVector(v[count]);
						binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
						tempBdd = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
						binaryLeftThing.free();
						binaryRightThing.free();
					}
					else if (leftClass.equals(il) && rightClass.equals(ie)) {
						IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
						IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
						int count = 0;
						for (String s : varOrder) {
							if (s.equals(newBinaryRight.getIdentifier())) {
								break;
							}
							count++;
						}
						binaryRightThing = bdd.buildVector(v[count]);
						binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
						tempBdd = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
						binaryLeftThing.free();
						binaryRightThing.free();
					}
					else if (leftClass.equals(ie) && rightClass.equals(ie)) {
						IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
						IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
						int count1 = 0;
						int count2 = 0;
						for (String s : varOrder) {
							if (s.equals(newBinaryLeft.getIdentifier())) {
								break;
							}
							count1++;
						}
						for (String s : varOrder) {
							if (s.equals(newBinaryRight.getIdentifier())) {
								break;
							}
							count2++;
						}
						binaryLeftThing = bdd.buildVector(v[count1]);
						binaryRightThing = bdd.buildVector(v[count2]);
						tempBdd = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
						binaryLeftThing.free();
						binaryRightThing.free();
					}
					if (checkNot.equals(LOGICNEG)) {
						tempBdd = tempBdd.not();
					}
				}
				transition = tempBdd;
			}
			else if (expr instanceof BinaryExpression) {
				BinaryExpression newExpr = (BinaryExpression) expr;
				mLogger.info(newExpr.getLeft());
				mLogger.info(newExpr.getRight());
			}
			
//			nwaTrans.add(transition);
			mLogger.info("transition : " + transition);
		}
	}
	private BDDBitVector dealWithOperatorForAssign(BDDBitVector binaryLeftThing, BDDBitVector binaryRightThing, String ope) {
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
	
	private BDD dealWithComparisonOperator(BDDBitVector binaryLeftThing, BDDBitVector binaryRightThing, String ope) {
		BDD opeResult = bdd.one();
		String LOGICIFF = "LOGICIFF"; //  <->
		String LOGICIMPLIES = "LOGICIMPLIES"; // -> 
		String LOGICAND = "LOGICAND"; // &&
		String LOGICOR = "LOGICOR"; // ||
		String COMPLT = "COMPLT"; // <
		String COMPGT = "COMPGT";  // >
		String COMPLEQ = "COMPLEQ";  // <=
		String COMPGEQ = "COMPGEQ";  // >=
		String COMPEQ = "COMPEQ";  // <=
		String COMPNEQ = "COMPNEQ";  // >=
		String COMPPO = "COMPPO";  
		
//		mLogger.info(binaryLeftThing);
//		mLogger.info(binaryRightThing);
		if (ope.equals(COMPLT)) {
			opeResult = binaryLeftThing.lth(binaryRightThing);
		}
		else if (ope.equals(COMPGT)) {
			opeResult = binaryRightThing.lth(binaryLeftThing);
		}
		else if (ope.equals(COMPLEQ)) {
			opeResult = binaryLeftThing.lte(binaryRightThing);
		}
		else if (ope.equals(COMPGEQ)) {
			opeResult = binaryRightThing.lte(binaryLeftThing);
		}
		else if (ope.equals(COMPEQ)) {
			opeResult = binaryRightThing.eq(binaryLeftThing);
		}
		else if (ope.equals(COMPNEQ)) {
			opeResult = binaryRightThing.eq(binaryLeftThing);
			opeResult = opeResult.not();
		}
		
		return opeResult;
	}
	
	public List<BDD> getNwaTrans(){
		return nwaTrans;
	}
	
}
