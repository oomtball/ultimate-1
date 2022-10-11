package info.scce.addlib.gencodegenerator.checks;

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText;
import info.scce.addlib.gencodegenerator.parser.ast.Param;
import info.scce.addlib.gencodegenerator.parser.ast.Text.TreeVisitor;
import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;

import java.util.Arrays;
import java.util.List;

public class AnnotationsKnownCheck extends Check {

    private static final String[] ONE_STRING_PARAM_ANNOTATIONS = {"opt-param", "param", "enabled-param",
            "disabled-param", "injectable"};
    private static final String[] NO_PARAMS_ANNOTATIONS = {"root-dd", "internal-dd", "constant-dd"};
    private static final String[] ID_PARAMS_ANNOTATIONS = {"attr"};

    @Override
    public void check(TextListRoot textListRoot) {
        textListRoot.visit(new TreeVisitor() {

            @Override
            public void preAnnotatedText(AnnotatedText annotatedText) {
                String annotation = annotatedText.getAnnotation();
                if (Arrays.asList(ONE_STRING_PARAM_ANNOTATIONS).contains(annotation)) {
                    if (!hasOneStringParam(annotatedText))
                        addError("\"" + annotation + "\" annotations must have one string parameter");
                } else if (Arrays.asList(NO_PARAMS_ANNOTATIONS).contains(annotation)) {
                    if (!hasNoParam(annotatedText))
                        addError("\"" + annotation + "\" annotations must not have a parameter");
                } else if (Arrays.asList(ID_PARAMS_ANNOTATIONS).contains(annotation)) {
                    if (!hasIdParams(annotatedText))
                        addError("\"" + annotation + "\" annotations must have id parameters");
                } else {
                    addError("\"" + annotation + "\" annotation unknown");
                }
            }
        });
    }

    private boolean hasOneStringParam(AnnotatedText annotatedText) {
        List<Param> paramList = annotatedText.getParamList();
        if (paramList.size() != 1)
            return false;
        Param param = paramList.get(0);
        return param.getType() == Param.Type.STRING;
    }

    private boolean hasNoParam(AnnotatedText annotatedText) {
        List<Param> paramList = annotatedText.getParamList();
        return paramList.isEmpty();
    }

    private boolean hasIdParams(AnnotatedText annotatedText) {
        List<Param> paramList = annotatedText.getParamList();
        return paramList.stream().allMatch((p) -> p.getType() == Param.Type.ID);
    }
}
