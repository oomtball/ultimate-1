package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;


public class SimpleEvaluator {
	ILogger mLogger;
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String be = "BinaryExpression";
	
	public SimpleEvaluator(){
		
	}
	
	public SimpleEvaluator(ILogger logger) {
		mLogger = logger;
	}
	
	public SimpleState calculatePost(SimpleState preState, String lfs, Expression expression, Pair<String, Pair<Integer, Integer>> pc) {
		SimpleState postState = new SimpleState();
		if (expression.getClass().getSimpleName().equals(il)) {
			postState = handleIntegerLiteral(preState, lfs, expression);
		}
		else if (expression.getClass().getSimpleName().equals(ie)) {
			postState = handleIdentifierExpression(preState, lfs, expression);
		}
		else if (expression.getClass().getSimpleName().equals(be)) {
			postState = handleBinaryExpression(preState, lfs, expression);
		}
		return postState;
	}
	
	public SimpleState handleIntegerLiteral(SimpleState preState, String lfs, Expression expression) {
		SimpleState postState = new SimpleState();
		return postState;
	}
	
	public SimpleState handleIdentifierExpression(SimpleState preState, String lfs, Expression expression) {
		SimpleState postState = new SimpleState();
		return postState;
	}
	
	public SimpleState handleBinaryExpression(SimpleState preState, String lfs, Expression expression) {
		SimpleState postState = new SimpleState();
		return postState;
	}
}