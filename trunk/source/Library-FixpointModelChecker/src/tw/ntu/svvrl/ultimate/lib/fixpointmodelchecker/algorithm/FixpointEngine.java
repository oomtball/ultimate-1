package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.algorithm;

import java.util.Collection;

import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.IAbstractState;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.AbsIntResult;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.IFixpointEngine;

public class FixpointEngine<STATE extends IAbstractState<STATE>, ACTION, VARDECL, LOC>
implements IFixpointEngine<STATE, ACTION, VARDECL, LOC> {

	public AbsIntResult<STATE, ACTION, LOC> run(Collection<? extends LOC> start, Script script) {
		// TODO Auto-generated method stub
		return null;
	}
}