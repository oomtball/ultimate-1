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

public class AssignmentStatementEvalator {
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
	
	public AssignmentStatementEvalator(final ILogger logger, final IUltimateServiceProvider services, 
			BDDFactory _bdd, BDDDomain[] _v, BDDDomain[] _vprime, Set<String> _varOrder) {
		mServices = services;
		mLogger = logger;
		
		bdd = _bdd;
		v = _v; // represents different bdd variables
		vprime = _vprime; // represents different bdd variables
		varOrder = _varOrder;
	}
	
	public BDD buildTran(Expression expr, int needVar) {
		BDD transition = bdd.one();
		
//		mLogger.info(var + " = " + expr + " at " + s2.getSecond());
		// deal with transitions
		if (expr.getClass().getSimpleName().equals(il)) {
			transition = caseIL(expr, needVar, bdd);
		}
		else if (expr.getClass().getSimpleName().equals(ie)) {
			transition = caseIE(expr, needVar, varOrder, bdd);
		}
		else if (expr.getClass().getSimpleName().equals(be)) {
			transition = caseBE(expr, needVar, varOrder, bdd);
		} 
		
		return transition;
	}
	
	private BDD caseIL(Expression expr, int needVar, BDDFactory bdd) {
		IntegerLiteral newExpr = (IntegerLiteral) expr;
		int expValue = Integer.parseInt(newExpr.getValue());
		BDD transition = bdd.one();
		BDDBitVector leftVar = bdd.buildVector(vprime[needVar]);
		BDDBitVector rightValue = bdd.constantVector(leftVar.size(), expValue);
		for (int j = 0; j < leftVar.size(); j++) {
			transition = transition.andWith(leftVar.getBit(j).biimp(rightValue.getBit(j)));
		}
		leftVar.free();
		rightValue.free();
		return transition;
	}
	
	private BDD caseIE(Expression expr, int needVar, Set<String> varOrder, BDDFactory bdd) {
		IdentifierExpression newExpr = (IdentifierExpression) expr;
		String rightv = newExpr.getIdentifier();
		BDD transition = bdd.one();
		int needVar2 = calculateIndex(rightv);
		
		BDDBitVector leftVar = bdd.buildVector(vprime[needVar]);
		BDDBitVector rightVar = bdd.buildVector(v[needVar2]);
		
		for (int i = 0; i < leftVar.size(); i++) {
			transition = transition.andWith(leftVar.getBit(i).biimp(rightVar.getBit(i)));
		}
		leftVar.free();
		rightVar.free();
		return transition;
	}
	
	private BDD caseBE(Expression expr, int needVar, Set<String> varOrder, BDDFactory bdd) {
		BinaryExpression newExpr = (BinaryExpression) expr;
		BDD transition = bdd.one();
		Expression binaryLeft = newExpr.getLeft();
		Expression binaryRight = newExpr.getRight();
		String leftClass = binaryLeft.getClass().getSimpleName();
		String rightClass = binaryRight.getClass().getSimpleName();
		
		BDDBitVector leftVar = bdd.buildVector(vprime[needVar]);
		String ope = newExpr.getOperator().toString();
//		mLogger.info(ope);
		
		BDDBitVector binaryLeftThing = null; 
		BDDBitVector binaryRightThing = null; 
		BDDBitVector rightVar = null;
		if (leftClass.equals(il)) {
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryLeftThing = bdd.constantVector(leftVar.size(), Integer.parseInt(newBinaryLeft.getValue()));
				binaryRightThing = bdd.constantVector(leftVar.size(), Integer.parseInt(newBinaryRight.getValue()));
			}
			else if (rightClass.equals(ie)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;		
				int count = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[count]);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
			}
			else if (rightClass.equals(be)) {
				IntegerLiteral newBinaryLeft = (IntegerLiteral) binaryLeft;
				binaryRightThing = caseBEforBvec(binaryRight);
				binaryLeftThing = bdd.constantVector(binaryRightThing.size(), Integer.parseInt(newBinaryLeft.getValue()));
			}
			rightVar = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(ie)) {
			IdentifierExpression newBinaryLeft = (IdentifierExpression) binaryLeft;
			int count = calculateIndex(newBinaryLeft.getIdentifier());
			binaryLeftThing = bdd.buildVector(v[count]);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int count2 = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[count2]);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
			}
			rightVar = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		else if (leftClass.equals(be)) {
			binaryLeftThing = caseBEforBvec(binaryLeft);
			if (rightClass.equals(il)) {
				IntegerLiteral newBinaryRight = (IntegerLiteral) binaryRight;
				binaryRightThing = bdd.constantVector(binaryLeftThing.size(), Integer.parseInt(newBinaryRight.getValue()));
			}
			else if (rightClass.equals(ie)) {
				IdentifierExpression newBinaryRight = (IdentifierExpression) binaryRight;
				int count = calculateIndex(newBinaryRight.getIdentifier());
				binaryRightThing = bdd.buildVector(v[count]);
			}
			else if (rightClass.equals(be)) {
				binaryRightThing = caseBEforBvec(binaryRight);
			}
			rightVar = dealWithAssignmentOperator(binaryLeftThing, binaryRightThing, ope);
			binaryLeftThing.free();
			binaryRightThing.free();
		}
		
		for (int i = 0; i < leftVar.size(); i++) {
			transition = transition.andWith(leftVar.getBit(i).biimp(rightVar.getBit(i)));
		}
		leftVar.free();
		rightVar.free();
		
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
