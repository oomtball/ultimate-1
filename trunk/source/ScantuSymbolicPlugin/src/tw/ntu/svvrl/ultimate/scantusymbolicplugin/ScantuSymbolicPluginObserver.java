package tw.ntu.svvrl.ultimate.scantusymbolicplugin;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomataSizeBenchmark;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.models.ModelType;
import de.uni_freiburg.informatik.ultimate.core.model.observers.IUnmanagedObserver;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.ltl2aut.never2nwa.NWAContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.util.IcfgSizeBenchmark;
import tw.ntu.svvrl.ultimate.scantusymbolicplugin.Activator;
import tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.*;
import tw.ntu.svvrl.ultimate.lib.scantufixpointapproach.ScantuFixpointApproach;

public class ScantuSymbolicPluginObserver implements IUnmanagedObserver{
	private final ILogger mLogger;
	private BoogieIcfgContainer mRcfg;
	private NWAContainer mNeverClaimNWAContainer;
	private final IUltimateServiceProvider mServices;
	
	private ScantuFixpointApproach mScantuFixpointApproach;

	public ScantuSymbolicPluginObserver(final ILogger logger, final IUltimateServiceProvider services) {
		mLogger = logger;
		mServices = services;
		mRcfg = null;
		
		mScantuFixpointApproach = null;
	}

	@Override
	public void init(final ModelType modelType, final int currentModelIndex, final int numberOfModels) {
		// no initialization needed
	}
	
	@Override
	public void finish() throws Throwable {
		if (mRcfg == null) {
			return;
		}

		reportSizeBenchmark("Initial RCFG", mRcfg);
		
		mLogger.info("Start calculating fixpoint for Buchi product...");
		mScantuFixpointApproach = new ScantuFixpointApproach(mRcfg, mLogger, mServices);
	}
	
	@Override
	public boolean performedChanges() {
		return false;
	}

	// No model generated so far.
	public IElement getModel() {
		return null;
	}
	

	private void reportSizeBenchmark(final String message, final BoogieIcfgContainer root) {
		final IcfgSizeBenchmark bench = new IcfgSizeBenchmark(root, message);
		mLogger.info(message + " " + bench);
		bench.reportBenchmarkResult(mServices.getResultService(), Activator.PLUGIN_ID, message);
	}
	
	@Override
	public boolean process(final IElement root) throws Exception {

		// collect root node of program's RCFG
		if (root instanceof BoogieIcfgContainer) {
			mLogger.debug("Collecting RCFG RootNode");
			mRcfg = (BoogieIcfgContainer) root;
			return false;
		}
		return true;
	}
}
