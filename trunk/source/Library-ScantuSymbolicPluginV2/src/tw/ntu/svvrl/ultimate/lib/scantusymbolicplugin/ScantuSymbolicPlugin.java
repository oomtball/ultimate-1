package tw.ntu.svvrl.ultimate.lib.scantusymbolicplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.core.model.IAnalysis;
import de.uni_freiburg.informatik.ultimate.core.model.ITool.ModelQuery;
import de.uni_freiburg.informatik.ultimate.core.model.models.ModelType;
import de.uni_freiburg.informatik.ultimate.core.model.observers.IObserver;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.AbstractInterpretationRcfgObserver;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.preferences.AbsIntPrefInitializer;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;



public class ScantuSymbolicPlugin implements IAnalysis{
	protected ILogger mLogger;
	private IUltimateServiceProvider mServices;
	private List<IObserver> mObserver;

	@Override
	public ModelType getOutputDefinition() {
		return null;
	}

	@Override
	public boolean isGuiRequired() {
		return false;
	}

	@Override
	public ModelQuery getModelQuery() {
		return ModelQuery.LAST;
	}

	@Override
	public List<String> getDesiredToolIds() {
		return null;
	}

	@Override
	public void setInputDefinition(final ModelType graphType) {
		final String creator = graphType.getCreator();
		switch (creator) {
		case "de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder":
			mObserver = new ArrayList<>();
			mObserver.add(new AbstractInterpretationRcfgObserver(mServices));
			break;
		default:
			mObserver = null;
			break;
		}
	}

	@Override
	public List<IObserver> getObservers() {
		if (mObserver == null) {
			return Collections.emptyList();
		}
		return mObserver;
	}

	@Override
	public void init() {
		// not used
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
	public IPreferenceInitializer getPreferences() {
		return new AbsIntPrefInitializer();
	}

	@Override
	public void setServices(final IUltimateServiceProvider services) {
		mServices = services;
		mLogger = mServices.getLoggingService().getLogger(Activator.PLUGIN_ID);
	}

	@Override
	public void finish() {
		// not used
	}
}
