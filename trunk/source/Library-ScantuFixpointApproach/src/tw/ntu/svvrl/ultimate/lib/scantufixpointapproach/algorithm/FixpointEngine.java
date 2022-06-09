package tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.algorithm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.DisjunctiveAbstractState;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.IAbstractState;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;

public class FixpointEngine <STATE extends IAbstractState<STATE>, ACTION, VARDECL, LOC> 
	implements IFixpointEngine<STATE, ACTION, VARDECL, LOC>{
	
	private boolean acceptProperty;
	public FixpointEngine() {
		acceptProperty = false;
	}
	
	public boolean run(final IIcfg<? extends IcfgLocation> rcfg, final ILogger logger) {
		logger.info(rcfg);
		calculateFixpoint(rcfg);
		return acceptProperty;
	}
	
	private void calculateFixpoint(final IIcfg<? extends IcfgLocation> rcfg) {
		acceptProperty = true;
	}
	
	private DisjunctiveAbstractState<STATE> calculateAbstractPost() {
		DisjunctiveAbstractState<STATE> postState = new DisjunctiveAbstractState();
		return postState;
	}
	
}
