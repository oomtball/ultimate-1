package info.scce.addlib.gencodegenerator.checks;

import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;

import java.util.ArrayList;
import java.util.List;

public class AllChecks extends Check {

    private List<Check> checks;

    public AllChecks() {
        checks = new ArrayList<Check>();
        checks.add(new AnnotationsExistenceCheck());
        checks.add(new AnnotationsKnownCheck());
    }

    @Override
    public void check(TextListRoot textListRoot) {
        for (Check c : checks) {
            c.check(textListRoot);
            for (String err : c.getErrors())
                addError(err);
        }
    }
}
