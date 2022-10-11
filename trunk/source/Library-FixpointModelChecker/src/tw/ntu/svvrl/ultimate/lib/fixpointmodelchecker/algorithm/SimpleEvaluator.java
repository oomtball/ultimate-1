package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
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
	
	public List<Integer> calculatePost(Set<String> vars, List<Integer> values, String lfs, Expression expression, 
			Pair<String, Pair<Integer, Integer>> pc) {
		List<Integer> tempPostTran = new ArrayList<>();
		if (expression.getClass().getSimpleName().equals(il)) {
			tempPostTran = handleIntegerLiteral(vars, values, lfs, expression);
		}
		else if (expression.getClass().getSimpleName().equals(ie)) {
			tempPostTran = handleIdentifierExpression(vars, values, lfs, expression);
		}
		else if (expression.getClass().getSimpleName().equals(be)) {
			tempPostTran = handleBinaryExpression(vars, values, lfs, expression);
		}
		return tempPostTran;
	}
	
	public List<Integer> handleIntegerLiteral(Set<String> vars, List<Integer> values, String lfs, Expression expression) {
		List<Integer> postValues = new ArrayList<>();
		for (int i : values) {
			postValues.add(i);
		}
		IntegerLiteral newEx = (IntegerLiteral) expression;
		int newValue = Integer.parseInt(newEx.getValue());
//		mLogger.info(newValue);
		int index = 0;
		for (String s : vars) {
			if (s.equals(lfs)) {
				break;
			}
			index++;
		}
		postValues.set(index, newValue);
		return postValues;
	}
	
	public List<Integer> handleIdentifierExpression(Set<String> vars, List<Integer> values, String lfs, Expression expression) {
		List<Integer> postValues = new ArrayList<>();
		return postValues;
	}
	
	public List<Integer> handleBinaryExpression(Set<String> vars, List<Integer> values, String lfs, Expression expression) {
		List<Integer> postValues = new ArrayList<>();
		return postValues;
	}
}