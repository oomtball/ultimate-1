package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.nevertransitiontoolkit;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;

import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.neverstate.NeverState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.state.programstate.ProgramState;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.CodeBlockExecutor;
import tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.transitiontoolkit.nevertransitiontoolkit.ProgramStatementsChecker;

public class NeverCodeBlockExecutor extends CodeBlockExecutor<NeverState> {
	/**
	 * Only for NeverClaim Automata.
	 */
	private final ProgramState mCorrespondingProgramState;
	
	/**
	 * For Never Claim Automata.
	 * @param codeBlock
	 * @param state
	 * @param autType
	 * @param correspondingProgramState
	 */
	public NeverCodeBlockExecutor(final CodeBlock codeBlock, final NeverState state
			, final ProgramState correspondingProgramState) {
		mCodeBlock = codeBlock;
		mCurrentState = state;
		mCorrespondingProgramState = correspondingProgramState;
	}
	

	public boolean checkEnabled() {
		if(mCodeBlock instanceof StatementSequence) {
			List<Statement> stmts = ((StatementSequence) mCodeBlock).getStatements();
			final ProgramStatementsChecker statementChecker = new ProgramStatementsChecker(stmts, mCorrespondingProgramState);
			return statementChecker.checkStatementsEnable();
		} else {
			throw new UnsupportedOperationException("Suppose the CodeBlock Type: "
					+ mCodeBlock.getClass().getSimpleName() + " should not be in Never Claim"
							+ " Automata.");
		}
	}

}
