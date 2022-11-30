package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression.Operator;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BooleanLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
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
	
	String COMPEQ = "COMPEQ";  // ==
	String COMPNEQ = "COMPNEQ";  // !=
	String COMPLT = "COMPLT"; // <
	String COMPGT = "COMPGT";  // >
	String COMPLEQ = "COMPLEQ";  // <=
	String COMPGEQ = "COMPGEQ";  // >=
	
	String LOGICNEG = "LOGICNEG";
	String LOGICAND = "LOGICAND";
	String LOGICOR = "LOGICOR";
	String LOGICIMPLIES = "LOGICIMPLIES"; // -> 
	String LOGICIFF = "LOGICIFF"; // 
	
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
			BinaryExpression be = (BinaryExpression) expr;
			
			transition = caseBE(be.getLeft(), be.getRight(), be.getOperator());
			if (be.getOperator().toString().equals(COMPEQ) || be.getOperator().toString().equals(COMPNEQ) || be.getOperator().toString().equals(COMPLT) 
					|| be.getOperator().toString().equals(COMPGT) || be.getOperator().toString().equals(COMPLEQ) || 
					be.getOperator().toString().equals(COMPGEQ)) {
				transition = caseBEforBDD(be.getLeft(), be.getRight(), be.getOperator());
			}
			else {
				transition = caseBE(be.getLeft(), be.getRight(), be.getOperator());
			}
		}
		
		return transition;
	}
	
	public BDD buildTran2(Expression leftExpr, Expression rightExpr, Operator op) {
		BDD transition = bdd.one();
		if (op.toString().equals(COMPEQ) || op.toString().equals(COMPNEQ) || op.toString().equals(COMPLT) || op.toString().equals(COMPGT) ||
				op.toString().equals(COMPLEQ) || op.toString().equals(COMPGEQ)) {
			transition = caseBEforBDD(leftExpr, rightExpr, op);
		}
		else {
			transition = caseBE(leftExpr, rightExpr, op);
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
		if (newExpr.getExpr().getClass().getSimpleName().equals(be)) {
			BinaryExpression newBE = (BinaryExpression) newExpr.getExpr();
			if (newBE.getOperator().toString().equals(LOGICAND) || newBE.getOperator().toString().equals(LOGICOR) 
					|| newBE.getOperator().toString().equals(LOGICIMPLIES) || newBE.getOperator().toString().equals(LOGICIFF)) {
				tempBdd = caseBE(newBE.getLeft(), newBE.getRight(), newBE.getOperator());
			}
			else {
				tempBdd = caseBEforBDD(newBE.getLeft(), newBE.getRight(), newBE.getOperator());
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
	
	private BDD caseBE(Expression leftExpr, Expression rightExpr, Operator op) {
		BDD tempBdd = bdd.one();
		Expression binaryLeft = leftExpr;
		Expression binaryRight = rightExpr;
		String ope = op.toString();
		BDD leftResult = bdd.one();
		BDD rightResult = bdd.one();
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
				leftResult = caseBE(newBE.getLeft(), newBE.getRight(), newBE.getOperator());
			}
			else {
				leftResult = caseBEforBDD(newBE.getLeft(), newBE.getRight(), newBE.getOperator());
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
				rightResult = caseBE(newBE.getLeft(), newBE.getRight(), newBE.getOperator());
			}
			else {
				rightResult = caseBEforBDD(newBE.getLeft(), newBE.getRight(), newBE.getOperator());
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
	
	private BDD caseBEforBDD(Expression leftExpr, Expression rightExpr, Operator op) {
		BDD result = null;
		Expression binaryLeft = leftExpr;
		Expression binaryRight = rightExpr;
		String ope = op.toString();
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
