package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimpleState {
	
	Set<String> mVars = new HashSet<>();;
	List<Integer> mValues = new ArrayList<>();
	String procedure;
	int pc;
	public SimpleState(){
	}
	
	public SimpleState(Set vars, List values){
		mVars = vars;
		mValues = values;
	}
	
	public Set getVars() {
		return mVars;
	}
	
	public List getValues() {
		return mValues;
	}
	
	public String getProcedure() {
		return procedure;
	}
	
	public int getPc() {
		return pc;
	}
	
	public int getValueByVar(String target) {
		assert mVars.contains(target);
		int index = -10000;
		for (var i = 0; i < mVars.toArray().length; i++) {
			if (mVars.toArray()[i].equals(target)) {
				index = i;
			}
		}
		return mValues.indexOf(index);
	}
}