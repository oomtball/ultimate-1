package tw.ntu.svvrl.ultimate.lib.fixpointmodelchecker.explorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BooleanLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.UnaryExpression;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import net.sf.javabdd.BDDDomain;
import net.sf.javabdd.BDDFactory;

public class NwaTransitionBuilder {
	final INestedWordAutomaton<CodeBlock, String> mNwa; 
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	
	static BDDFactory bdd;
	BDDDomain[] preVar;
	BDDDomain[] postVar;
	
	String il = "IntegerLiteral";
	String ie = "IdentifierExpression";
	String bl = "BooleanLiteral";
	String ue = "UnaryExpression";
	String be = "BinaryExpression";
	
	public NwaTransitionBuilder(INestedWordAutomaton<CodeBlock, String> nwa, final ILogger logger, final IUltimateServiceProvider services, 
			BDDFactory bdd, BDDDomain[] pre, BDDDomain[] post, Set<String> varOrder) {
		mNwa = nwa;
		mServices = services;
		mLogger = logger;
		
		List<Expression> allExpression = getNwaExpression(mNwa.getAlphabet());
		preVar = pre;
		postVar = post;
		
	}
	
	private List<Expression> getNwaExpression(Set<CodeBlock> al) {
		List<Expression> allExpression = new ArrayList<>();
		for (CodeBlock cb : al) {
			StatementSequence ss = (StatementSequence) cb;
			for (Statement s : ss.getStatements()) {
				if (s instanceof AssumeStatement) {
					AssumeStatement as = (AssumeStatement) s;
					allExpression.add(as.getFormula());
				}
			}
		}
		return allExpression;
	}
	
}
