package tw.ntu.svvrl.ultimate.scantusymbolicplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.models.ModelType;
import de.uni_freiburg.informatik.ultimate.core.lib.results.CounterExampleResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.ResultUtil;
import de.uni_freiburg.informatik.ultimate.core.model.IGenerator;
import de.uni_freiburg.informatik.ultimate.core.model.ITool.ModelQuery;
import de.uni_freiburg.informatik.ultimate.core.model.observers.IObserver;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import tw.ntu.svvrl.ultimate.lib.scantufixpoint.approach.explorer.ProgramStateExplorer;


public class ScantuSymbolicPlugin implements IGenerator{
	private ILogger mLogger;
	private boolean mUseSsp;
	private ScantuSymbolicPluginObserver mScantuSymbolicPluginObserver;
	private boolean mPreviousToolFoundErrors;
	private IUltimateServiceProvider mServices;
	private int mUseful;


	@Override
	public ModelType getOutputDefinition() {
		if (mPreviousToolFoundErrors) {
			return null;
		}

		final List<String> filenames = new ArrayList<>();
		filenames.add("Currently no model");
		return new ModelType(Activator.PLUGIN_ID, ModelType.Type.OTHER, filenames);
	}

	@Override
	public boolean isGuiRequired() {
		return false;
	}

	@Override
	public ModelQuery getModelQuery() {
		if (mPreviousToolFoundErrors) {
			return ModelQuery.LAST;
		}
		return ModelQuery.ALL;
	}

	@Override
	public void setInputDefinition(final ModelType graphType) {
		switch (graphType.getCreator()) {
		case "de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder":
		case "de.uni_freiburg.informatik.ultimate.ltl2aut":
			mUseSsp = true;
			mUseful++;
			break;
		default:
			mUseSsp = false;
			break;
		}
	}

	@Override
	public List<IObserver> getObservers() {
		if (!mPreviousToolFoundErrors && mUseSsp) {
			final ScantuSymbolicPluginObserver observer = getScantuSymbolicPluginObserver();
			return Collections.singletonList(observer);
		}
		return Collections.emptyList();
	}

	private ScantuSymbolicPluginObserver getScantuSymbolicPluginObserver() {
		if (mScantuSymbolicPluginObserver == null) {
			mScantuSymbolicPluginObserver = new ScantuSymbolicPluginObserver(mLogger, mServices);
		}
		return mScantuSymbolicPluginObserver;
	}

	@Override
	public void init() {
		mUseSsp = false;
		mUseful = 0;
	}

	@Override
	public String getPluginName() {
		return Activator.PLUGIN_NAME;
	}

	@Override
	public String getPluginID() {
		return Activator.PLUGIN_ID;
	}

	@Override
	public IElement getModel() {
		return getScantuSymbolicPluginObserver().getModel();
	}

	@Override
	public List<String> getDesiredToolIds() {
		return Collections.emptyList();
	}

	@Override
	public IPreferenceInitializer getPreferences() {
		// currently no preferences
		return null;
	}

	@Override
	public void setServices(final IUltimateServiceProvider services) {
		mServices = services;
		mLogger = mServices.getLoggingService().getLogger(Activator.PLUGIN_ID);
		mPreviousToolFoundErrors = !ResultUtil
				.filterResults(services.getResultService().getResults(), CounterExampleResult.class).isEmpty();
	}

	@Override
	public void finish() {
		if (!mPreviousToolFoundErrors && mUseful == 0) {
			throw new IllegalStateException("Was used in a toolchain and did nothing");
		}
		if (mPreviousToolFoundErrors) {
			mLogger.info("Another plugin discovered errors, skipping...");
		}
	}
}
