package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
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

public class AssumeStatementEvalator {
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	static BDDFactory bdd;
	BDDDomain[] v; // represents different bdd variables
	BDDDomain[] vprime; // represents different bdd variables
	
	Set<String> varOrder;
	
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String bl = "BooleanLiteral";
	String ue = "UnaryExpression";
	String be = "BinaryExpression";
	
	public AssumeStatementEvalator(final ILogger logger, final IUltimateServiceProvider services, 
			BDDFactory _bdd, BDDDomain[] _v, BDDDomain[] _vprime, Set<String> _varOrder) {
		mServices = services;
		mLogger = logger;
		
		bdd = _bdd;
		v = _v; // represents different bdd variables
		vprime = _vprime; // represents different bdd variables
		varOrder = _varOrder;
	}
	
	public BDD buildTran(Expression expr) {
		BDD transition = bdd.one();
//		mLogger.info(expr);
		if (expr instanceof BooleanLiteral) {
			transition = caseBL(expr);
		}
		else if (expr instanceof UnaryExpression) {
			transition = caseUE(expr);
		}
		else if (expr instanceof BinaryExpression) {
			transition = caseBE(expr);
		}
		
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
	
	private BDD dealWithComparisonOperator(BDDBitVector binaryLeftThing, BDDBitVector binaryRightThing, String ope) {
		BDD opeResult = bdd.one();
		String COMPLT = "COMPLT"; // <
		String COMPGT = "COMPGT";  // >
		String COMPLEQ = "COMPLEQ";  // <=
		String COMPGEQ = "COMPGEQ";  // >=
		String COMPEQ = "COMPEQ";  // ==
		String COMPNEQ = "COMPNEQ";  // !=
		String COMPPO = "COMPPO";  
		
//		mLogger.info(binaryLeftThing);
//		mLogger.info(binaryRightThing);
		if (ope.equals(COMPLT)) {
			opeResult = binaryRightThing.lte(binaryLeftThing);
			opeResult = opeResult.not();
		}
		else if (ope.equals(COMPGT)) {
			opeResult = binaryLeftThing.lte(binaryRightThing);
			opeResult = opeResult.not();
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
	
	private BDD caseBL(Expression expr) {
		BDD tempBdd = bdd.one();
		BooleanLiteral newExpr = (BooleanLiteral) expr;
		Boolean newExprBooleanValue = newExpr.getValue();
		if (!newExprBooleanValue) {
			tempBdd = bdd.zero();
		}
		return tempBdd;
	}
	
	private BDD caseUE(Expression expr) {
		BDD tempBdd = bdd.one();
		UnaryExpression newExpr = (UnaryExpression) expr;
		String checkNot = newExpr.getOperator().toString();
		String LOGICNEG = "LOGICNEG";
		String LOGICAND = "LOGICAND";
		String LOGICOR = "LOGICOR";
		String LOGICIMPLIES = "LOGICIMPLIES"; // -> 
		String LOGICIFF = "LOGICIFF"; // 
		if (newExpr.getExpr().getClass().getSimpleName().equals(be)) {
			BinaryExpression newBE = (BinaryExpression) newExpr.getExpr();
			if (newBE.getOperator().toString().equals(LOGICAND) || newBE.getOperator().toString().equals(LOGICOR) 
					|| newBE.getOperator().toString().equals(LOGICIMPLIES) || newBE.getOperator().toString().equals(LOGICIFF)) {
				tempBdd = caseBE(newExpr.getExpr());
			}
			else {
				tempBdd = caseBEforBDD(newExpr.getExpr());
			}
		}
		else if (newExpr.getExpr().getClass().getSimpleName().equals(ue)) {
			tempBdd = caseUE(newExpr.getExpr());
		}
		if (checkNot.equals(LOGICNEG)) {
			tempBdd = tempBdd.not();
		}
		return tempBdd;	
	}
	
	private BDD caseBE(Expression expr) {
		BDD tempBdd = bdd.one();
		BinaryExpression newExpr = (BinaryExpression) expr;
		Expression binaryLeft = newExpr.getLeft();
		Expression binaryRight = newExpr.getRight();
		String ope = newExpr.getOperator().toString();
		BDD leftResult = bdd.one();
		BDD rightResult = bdd.one();
		String LOGICAND = "LOGICAND";
		String LOGICOR = "LOGICOR";
		String LOGICIMPLIES = "LOGICIMPLIES"; // -> 
		String LOGICIFF = "LOGICIFF"; //  <->
		if (binaryLeft.getClass().getSimpleName().equals(bl)) {
			leftResult = caseBL(binaryLeft);
		}
		else if (binaryLeft.getClass().getSimpleName().equals(ue)) {
			leftResult = caseUE(binaryLeft);
		}
		else if (binaryLeft.getClass().getSimpleName().equals(be)) {
			BinaryExpression newBE = (BinaryExpression) binaryLeft;
			if (newBE.getOperator().toString().equals(LOGICAND) || newBE.getOperator().toString().equals(LOGICOR) 
					|| newBE.getOperator().toString().equals(LOGICIMPLIES) || newBE.getOperator().toString().equals(LOGICIFF)) {
				leftResult = caseBE(binaryLeft);
			}
			else {
				leftResult = caseBEforBDD(binaryLeft);
			}
		}
		if (binaryRight.getClass().getSimpleName().equals(bl)) {
			rightResult = caseBL(binaryRight);
		}
		else if (binaryRight.getClass().getSimpleName().equals(ue)) {
			rightResult = caseUE(binaryRight);
		}
		else if (binaryRight.getClass().getSimpleName().equals(be)) {
			BinaryExpression newBE = (BinaryExpression) binaryRight;
			if (newBE.getOperator().toString().equals(LOGICAND) || newBE.getOperator().toString().equals(LOGICOR) 
					|| newBE.getOperator().toString().equals(LOGICIMPLIES) || newBE.getOperator().toString().equals(LOGICIFF)) {
				rightResult = caseBE(binaryRight);
			}
			else {
				rightResult = caseBEforBDD(binaryRight);
			}
		}
		if (ope.equals(LOGICAND)) {
			tempBdd = leftResult.and(rightResult);
			leftResult.free();
			rightResult.free();
		}
		else if (ope.equals(LOGICOR)) {
			tempBdd = leftResult.or(rightResult);
			leftResult.free();
			rightResult.free();
		}
		else if (ope.equals(LOGICIMPLIES)) {
			tempBdd = leftResult.imp(rightResult);
			leftResult.free();
			rightResult.free();
		}
		else if (ope.equals(LOGICIFF)) {
			tempBdd = leftResult.biimp(rightResult);
			leftResult.free();
			rightResult.free();
		}
		return tempBdd;
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
	
	private BDD caseBEforBDD(Expression expr) {
		BDD result = null;
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
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(be)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				binaryRightThing = caseBEforBvec(binaryRight);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
		}
		else if (leftClass.equals(ie)) {
			IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
			int index  = calculateIndex(newBinaryLeft.getIdentifier());
			binaryLeftThing = bdd.buildVector(v[index]);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int index2  = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[index2]);
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
		}
		else if (leftClass.equals(be)) {
			binaryLeftThing = caseBEforBvec(binaryLeft);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int index2  = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[index2]);
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
				result = dealWithComparisonOperator(binaryLeftThing, binaryRightThing, ope);
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
