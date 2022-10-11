package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimpleState {
	
	private Set<String> mVars;
	private List<Integer> mValues;
	
	public SimpleState(){
	
	}
	
	public SimpleState(final Set<String> vars, final List<Integer> values){
		mVars = vars;
		mValues = values;
	}
	
	public Set<String> getVars(){
		return mVars;
	}
	
	public List<Integer> getValues(){
		return mValues;
	}
}