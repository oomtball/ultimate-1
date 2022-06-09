/*
 * Copyright (C) 2013-2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 * Copyright (C) 2013-2015 Vincent Langenfeld (langenfv@informatik.uni-freiburg.de)
 *
 * This file is part of the ULTIMATE BuchiProgramProduct plug-in.
 *
 * The ULTIMATE BuchiProgramProduct plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE BuchiProgramProduct plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE BuchiProgramProduct plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE BuchiProgramProduct plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE BuchiProgramProduct plug-in grant you additional permission
 * to convey the resulting work.
 */
package tw.ntu.svvrl.ultimate.debugplugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.core.lib.results.CounterExampleResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.ResultUtil;
import de.uni_freiburg.informatik.ultimate.core.model.IGenerator;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.models.ModelType;
import de.uni_freiburg.informatik.ultimate.core.model.observers.IObserver;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.logic.Term;

/**
 * This plugin is for the SVVRL research's debugging purpose.
 *
 * @author Hong-Yang Lin
 */
public class DebugPlugin implements IGenerator {
	private ILogger mLogger;
	private DebugPluginObserver mDebugPluginObserver;
	private boolean mUseDebugPluginObserver;
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
			mUseDebugPluginObserver = true;
			mUseful++;
			break;
		default:
			mUseDebugPluginObserver = false;
			break;
		}
	}

	@Override
	public List<IObserver> getObservers() {
		if (!mPreviousToolFoundErrors && mUseDebugPluginObserver) {
			final DebugPluginObserver observer = getDebugObserver();
			return Collections.singletonList(observer);
		}
		return Collections.emptyList();
	}

	private DebugPluginObserver getDebugObserver() {
		if (mDebugPluginObserver == null) {
			mDebugPluginObserver = new DebugPluginObserver(mLogger, mServices);
		}
		return mDebugPluginObserver;
	}

	@Override
	public void init() {
		mUseDebugPluginObserver = false;
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
		return getDebugObserver().getModel();
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
