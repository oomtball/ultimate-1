package tw.ntu.svvrl.ultimate.lib.scantusymbolicplugin.algorithm;

import java.util.Collection;

import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.IAbstractState;
import de.uni_freiburg.informatik.ultimate.logic.Script;

@FunctionalInterface
public interface IFixpointEngine<STATE extends IAbstractState<STATE>, ACTION, VARDECL, LOC> {

	AbsIntResult<STATE, ACTION, LOC> run(final Collection<? extends LOC> start,
			final Script script);
}
