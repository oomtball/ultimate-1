package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.ast.*;
import de.uni_freiburg.informatik.ultimate.boogie.preprocessor.PreprocessorAnnotation;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.boogie.IBoogieSymbolTableVariableProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm.rcfg.RcfgStatementExtractor;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.INonrelationalValue;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.NonrelationalState;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.NonrelationalStatementProcessor;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.explicit.ExplicitValueEvaluator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.nonrelational.NonrelationalEvaluator;
import jdd.bdd.BDD;

public class ProgramStateExplorerForBDD<STATE extends NonrelationalState<STATE, V>, V extends INonrelationalValue<V>> {
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	/*---------------RCFG fields---------------*/
	private final Map<String, BoogieIcfgLocation> mEntryNodes;
	private final Map<String, BoogieIcfgLocation> mExitNodes;
	private final Map<String, Map<DebugIdentifier, BoogieIcfgLocation>> mLocNodes;
	private final Set<BoogieIcfgLocation> mInitialNodes;
	/*------------End of RCFG fields-----------*/
	
	private final NonrelationalStatementProcessor<STATE, V> mStatementProcessor;
	private final RcfgStatementExtractor mStatementExtractor;
	
	/*------------BDD field--------------------*/
	private final BDD systemBDD;
	/*------------End of BDD field-------------*/

	@SuppressWarnings("null")
	public ProgramStateExplorerForBDD(final BoogieIcfgContainer rcfg, final ILogger logger, final IUltimateServiceProvider services) {
		mServices = services;
		mLogger = logger;
		systemBDD = new BDD(1000,100);
		
		/*---------------RCFG fields---------------*/
		mEntryNodes = rcfg.getProcedureEntryNodes();
		mExitNodes = rcfg.getProcedureExitNodes();
		mLocNodes = rcfg.getProgramPoints();
		mInitialNodes = rcfg.getInitialNodes();
		/*------------End of RCFG fields-----------*/

		
		// statement evaluator and processes
		final int maxParallelStates = 2;
		final int maxRecursionDepth = -1;
		final PreprocessorAnnotation pa = PreprocessorAnnotation.getAnnotation(rcfg);
		IBoogieSymbolTableVariableProvider mVariableProvider = rcfg.getBoogie2SMT().getBoogie2SmtSymbolTable();
		final ExplicitValueEvaluator evaluator = new ExplicitValueEvaluator(mLogger, pa.getSymbolTable(), mVariableProvider,
				maxParallelStates, maxRecursionDepth);
		final NonrelationalEvaluator evaluator2 = (NonrelationalEvaluator) evaluator;
		
		mStatementProcessor = new NonrelationalStatementProcessor<>(mLogger, mVariableProvider, evaluator2);
		mStatementExtractor = new RcfgStatementExtractor();
		
		
		// set up the program counters for each thread -- pc
		Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc = computeProgramCounter(mLocNodes);

		// get all assignments we need to build BDD transition 
		Map<String, Set<Expression>> allAssignments = getAllAssignments(allStatesWithPc);
		mLogger.info(allAssignments);
	}
	
	private Map<String, Map<BoogieIcfgLocation, Integer>> computeProgramCounter(Map<String, Map<DebugIdentifier, BoogieIcfgLocation>> mLocNodes){
		Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc = new HashMap<>();
		int maxThreadStates = -1; // is used to decide the bit number of program counters 
		for (String s : mLocNodes.keySet()) { // each thread
			Map<BoogieIcfgLocation, Integer> temp2 = new HashMap<>();
			int tempPc = 0;
			Map<DebugIdentifier, BoogieIcfgLocation> temp = mLocNodes.get(s);
			for (DebugIdentifier d : temp.keySet()) {
				temp2.put(temp.get(d), tempPc);
				allStatesWithPc.put(temp.get(d).getProcedure(), temp2);
				tempPc += 1;
//				allRcfgStates.add(temp.get(d));
			}
			if (tempPc > maxThreadStates) {
				maxThreadStates = tempPc;
			}
		}
		return allStatesWithPc;
	}
	
	private Map<String, Set<Expression>> getAllAssignments(Map<String, Map<BoogieIcfgLocation, Integer>> allStatesWithPc){
		Map<String, Set<Expression>> allAssignments = new HashMap<>();
		Set<String> allVar = new HashSet<>();
		for (String thread : allStatesWithPc.keySet()) {
			Map<BoogieIcfgLocation, Integer> tempStateMap = allStatesWithPc.get(thread);
			for (BoogieIcfgLocation tempState : tempStateMap.keySet()) {
				for (IcfgEdge edge : tempState.getOutgoingEdges()) {
//					mLogger.info(tempState);
					String ss = "class de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence";
					if (edge.getClass().toString().equals(ss)) {
						// all assignment
						final List<Statement> statements = mStatementExtractor.process(edge);
						for (Statement stmt : statements) {
							if (stmt instanceof AssignmentStatement) {
								AssignmentStatement temp = (AssignmentStatement) stmt;
								for (LeftHandSide lhs : temp.getLhs()) {
									VariableLHS lhs2 = (VariableLHS) lhs;
									for (Expression rhs : temp.getRhs()) {
										Set<Expression> newExpression = new HashSet<>();
										if (!allVar.contains(lhs2.getIdentifier())) {
											newExpression.add(rhs);
											allAssignments.put(lhs2.getIdentifier(), newExpression);
											allVar.add(lhs2.getIdentifier());
										}
										else {
											newExpression = allAssignments.get(lhs2.getIdentifier());
											newExpression.add(rhs);
											allAssignments.replace(lhs2.getIdentifier(), newExpression);
										}
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return allAssignments;
	}
}
