package info.scce.addlib.gencodegenerator.checks;

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText;
import info.scce.addlib.gencodegenerator.parser.ast.Text.TreeVisitor;
import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AnnotationsExistenceCheck extends Check {

    private static final String[] RREQUIRED_ANNOTATIONS = {"root-dd", "internal-dd", "constant-dd"};

    @Override
    public void check(TextListRoot textListRoot) {
        Set<String> missingAnnotations = new HashSet<String>(Arrays.asList(RREQUIRED_ANNOTATIONS));
        textListRoot.visit(new TreeVisitor() {

            @Override
            public void preAnnotatedText(AnnotatedText annotatedText) {
                String annotation = annotatedText.getAnnotation();
                missingAnnotations.remove(annotation);
            }
        });
        for (String annotation : missingAnnotations)
            addError("\"" + annotation + "\" annotation must exist at least once");
    }
}
