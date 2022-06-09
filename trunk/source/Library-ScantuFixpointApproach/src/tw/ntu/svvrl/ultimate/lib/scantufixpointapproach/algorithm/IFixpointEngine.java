package tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.algorithm;

import java.util.Collection;

import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.absint.IAbstractState;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;

public interface IFixpointEngine<STATE extends IAbstractState<STATE>, ACTION, VARDECL, LOC> {
	
	boolean run(final IIcfg<? extends IcfgLocation> rcfg, final ILogger logger);
}
