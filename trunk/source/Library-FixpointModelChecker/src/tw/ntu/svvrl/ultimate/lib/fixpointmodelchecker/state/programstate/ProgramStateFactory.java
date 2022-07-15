package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate;


import java.util.Map;

import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.boogie.Boogie2SmtSymbolTable;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.threadstate.ThreadState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.threadstate.ThreadStateFactory;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer.ProgramStateExplorer;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.Valuation;

public class ProgramStateFactory {
	private final VarAndParamAdder mVarAdder;
	private final ThreadStateFactory mThreadStateFactory;

	
	public ProgramStateFactory(final Boogie2SmtSymbolTable boogie2SmtSymbolTable
			, final CfgSmtToolkit cfgSmtToolkit
			, final Map<String, BoogieIcfgLocation> entryNodes
			, final Map<String, BoogieIcfgLocation> exitNodes
			, final ProgramStateExplorer pe) {
		mVarAdder = new VarAndParamAdder(boogie2SmtSymbolTable);
		mThreadStateFactory = new ThreadStateFactory(boogie2SmtSymbolTable, cfgSmtToolkit, pe);
	}


	public ProgramState createStartState(final BoogieIcfgLocation loc) {
		Valuation globalValuation = new Valuation();
		mVarAdder.addGlobalVars2Valuation(globalValuation);
		mVarAdder.addOldGlobalVars2Valuation(globalValuation);
		mVarAdder.addPthreadsForks(globalValuation);
		
		final ThreadState startThreadState = mThreadStateFactory.createStartState(loc, globalValuation);
		
		return new ProgramState(startThreadState, globalValuation);
	}
	
}
