package tw.ntu.svvrl.ultimate.scantu.actions.toolchain_actions;

import org.eclipse.ui.IWorkbenchWindow;
import de.uni_freiburg.informatik.ultimate.core.lib.toolchain.RunDefinition;
import de.uni_freiburg.informatik.ultimate.core.model.ICore;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import tw.ntu.svvrl.ultimate.scantu.ScantuController;
import tw.ntu.svvrl.ultimate.scantu.actions.RunToolchainAction;

public class RunDoubleDFSReductionToolchainAction extends RunToolchainAction {
	
	private static final String LABEL = "Run Double DFS with Reduction";
	private static final String TOOLCHAIN_NAME = "DoubleDFSReduction";

	public RunDoubleDFSReductionToolchainAction(ICore<RunDefinition> icore, ILogger logger, ScantuController controller,
			IWorkbenchWindow window) {
		super(icore, logger, controller, window, RunDoubleDFSReductionToolchainAction.class.getName(), LABEL, TOOLCHAIN_NAME);
	}
	
}