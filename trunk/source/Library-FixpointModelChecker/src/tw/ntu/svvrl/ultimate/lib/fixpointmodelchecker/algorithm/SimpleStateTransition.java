package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimpleStateTransition {
	
	private Set<String> mVars;
	private List<Integer> mPreValues;
	private List<Integer> mPostValues;
	private String procedure;
	private int prePc;
	private int postPc;
	
	public SimpleStateTransition(){
	}
	
	public SimpleStateTransition(Set<String> vars, List<Integer> preValues, List<Integer> postValues){
		mVars = vars;
		mPreValues = preValues;
		mPostValues = postValues;
	}
	
	public Set<String> getVars(){
		return mVars;
	}
	
	public List<Integer> getPreValues(){
		return mPreValues;
	}
	
	public List<Integer> getPostValues(){
		return mPostValues;
	}
	
	public String getProcedure(){
		return procedure;
	}
	
	public int getPrePc(){
		return prePc;
	}
	
	public int getPostPc(){
		return postPc;
	}
	
	public void setPostValueByVar(String target, int value) {
		assert mVars.contains(target);
		int index = -10000;
		for (var i = 0; i < mVars.toArray().length; i++) {
			if (mVars.toArray()[i].equals(target)) {
				index = i;
			}
		}
		mPostValues.set(index, value);
	}
	
	
	public void setPostValue(List<Integer> values) {
		mPostValues = values;
	}
	
}