package info.scce.addlib.gencodegenerator.gmodel;

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText;
import info.scce.addlib.gencodegenerator.parser.ast.Text.TreeVisitor;
import info.scce.addlib.gencodegenerator.parser.ast.TextList;
import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;

import java.util.*;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;

public class GModel {

    /* Underlying model */
    private String generatorName;
    private TextListRoot model;

    /* Parameters */
    private Map<String, Param> paramsByName = null;
    private Set<Param> params = null;
    private Map<String, EnableableParam> enableableParamsByName = null;
    private Set<EnableableParam> enableableParams = null;

    /* Variable binding */
    private int uniqueVarNameIdx = 0;
    private Map<AnnotatedText, List<TypedVar>> boundVarsByAnnotatedText = null;
    private Map<String, InjectableMethod> injectableMethodsByName = null;
    private Set<InjectableMethod> injectableMethods = null;

    public GModel(String generatorName, TextListRoot model) {
        this.generatorName = generatorName;
        this.model = model;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public TextListRoot getModel() {
        return model;
    }

    public Map<String, Param> getParamsByName() {
        if (paramsByName == null) {
            paramsByName = new HashMap<String, Param>();

            /* Collect optional parameters first to have them without default value */
            model.visit(new TreeVisitor() {

                @Override
                public void preAnnotatedText(AnnotatedText annotatedText) {
                    String annotation = annotatedText.getAnnotation();
                    if (annotation.equals("opt-param")) {
                        String paramName = annotatedText.getStringParam();
                        if (!paramsByName.containsKey(paramName)) {
                            Param param = new Param(paramName, null);
                            paramsByName.put(paramName, param);
                        }
                    }
                }
            });

            /* Collect remaining parameters, first ocurence defines default value */
            model.visit(new TreeVisitor() {

                @Override
                public void preAnnotatedText(AnnotatedText annotatedText) {
                    String annotation = annotatedText.getAnnotation();
                    if (annotation.equals("param")) {
                        String paramName = annotatedText.getStringParam();
                        if (!paramsByName.containsKey(paramName)) {
                            TextList body = annotatedText.getBody();
                            String defaultValue = body == null ? null : body.getPlainText();
                            Param param = new Param(paramName, defaultValue);
                            paramsByName.put(paramName, param);
                        }
                    }
                }
            });
        }
        return paramsByName;
    }

    public Param getParam(String name) {
        return getParamsByName().get(name);
    }

    public Set<Param> getParams() {
        if (params == null)
            params = new HashSet<Param>(paramsByName.values());
        return params;
    }

    public boolean hasParams() {
        return !getParamsByName().isEmpty();
    }

    public Map<String, EnableableParam> getEnableableParamsByName() {
        if (enableableParamsByName == null) {
            enableableParamsByName = new HashMap<String, EnableableParam>();
            model.visit(new TreeVisitor() {

                @Override
                public void preAnnotatedText(AnnotatedText annotatedText) {
                    String annotation = annotatedText.getAnnotation();
                    if (annotation.equals("enabled-param") || annotation.equals("disabled-param")) {
                        String paramName = annotatedText.getStringParam();
                        if (!enableableParamsByName.containsKey(paramName)) {
                            EnableableParam enableableParam = new EnableableParam(paramName);
                            enableableParamsByName.put(paramName, enableableParam);
                        }
                    }
                }
            });
        }
        return enableableParamsByName;
    }

    public EnableableParam getEnableableParam(String name) {
        return getEnableableParamsByName().get(name);
    }

    public Set<EnableableParam> getEnableableParams() {
        if (enableableParams == null)
            enableableParams = new HashSet<EnableableParam>(getEnableableParamsByName().values());
        return enableableParams;
    }

    public boolean hasEnableableParams() {
        return !getEnableableParamsByName().isEmpty();
    }

    public Map<AnnotatedText, List<TypedVar>> getBoundVarsByAnnotatedText() {
        if (boundVarsByAnnotatedText == null) {
            boundVarsByAnnotatedText = new HashMap<AnnotatedText, List<TypedVar>>();
            Stack<TypedVar> boundVarsStack = new Stack<TypedVar>();
            model.visit(new TreeVisitor() {

                @Override
                public void preAnnotatedText(AnnotatedText annotatedText) {

                    /* Push new variable or null to stack */
                    TypedVar newlyBoundVar = newlyBoundVar(annotatedText);
                    boundVarsStack.push(newlyBoundVar);

                    /* Register bound variables for visited annotated text */
                    List<TypedVar> boundVars = boundVarsStack.stream().filter(Objects::nonNull).collect(toList());
                    boundVarsByAnnotatedText.put(annotatedText, boundVars);
                }

                @Override
                public void postAnnotatedText(AnnotatedText annotatedText) {

                    /* Pop variable or null from stack */
                    boundVarsStack.pop();
                }
            });
        }
        return boundVarsByAnnotatedText;
    }

    private TypedVar newlyBoundVar(AnnotatedText annotatedText) {
        String annotation = annotatedText.getAnnotation();
        if (annotation.equals("root-dd"))
            return new TypedVar("LabelledRegularDD<D>", newUniqueVarName());
        else if (annotation.equals("internal-dd") || annotation.equals("constant-dd"))
            return new TypedVar("D", newUniqueVarName());
        return null;
    }

    private String newUniqueVarName() {
        return "x" + (uniqueVarNameIdx++);
    }

    public List<TypedVar> getBoundVars(AnnotatedText annotatedText) {
        return getBoundVarsByAnnotatedText().get(annotatedText);
    }

    public TypedVar getLeadVar(AnnotatedText annotatedText) {
        List<TypedVar> boundVars = getBoundVars(annotatedText);
        int lastIdx = boundVars.size() - 1;
        if (lastIdx >= 0)
            return boundVars.get(lastIdx);
        return null;
    }

    public Map<String, InjectableMethod> getInjectableMethodsByName() {
        if (injectableMethodsByName == null) {
            injectableMethodsByName = new HashMap<String, InjectableMethod>();
            model.visit(new TreeVisitor() {

                @Override
                public void preAnnotatedText(AnnotatedText annotatedText) {
                    String annotation = annotatedText.getAnnotation();
                    if (annotation.equals("injectable")) {
                        String name = annotatedText.getStringParam();
                        List<TypedVar> paramList = getBoundVars(annotatedText);
                        if (injectableMethodsByName.containsKey(name)) {
                            InjectableMethod injectableMethod = injectableMethodsByName.get(name);
                            List<TypedVar> paramListBefore = injectableMethod.getParamList();
                            if (!paramList.equals(paramListBefore)) {
                                TextList body = injectableMethod.getBody();
                                paramList = longestCommonPrefix(paramList, paramListBefore);
                                injectableMethod = new InjectableMethod(name, paramList, body);
                                injectableMethodsByName.put(name, injectableMethod);
                            }
                        } else {
                            InjectableMethod injectableMethod = new InjectableMethod(name, paramList,
                                    annotatedText.getBody());
                            injectableMethodsByName.put(name, injectableMethod);
                        }
                    }
                }
            });
        }
        return injectableMethodsByName;
    }

    private List<TypedVar> longestCommonPrefix(List<TypedVar> a, List<TypedVar> b) {
        int n = min(a.size(), b.size());
        for (int i = 0; i < n; i++) {
            if (!a.get(i).equals(b.get(i)))
                return a.subList(0, i);
        }
        return a.subList(0, n);
    }

    public InjectableMethod getInjectableMethod(String name) {
        return getInjectableMethodsByName().get(name);
    }

    public Set<InjectableMethod> getInjectableMethods() {
        if (injectableMethods == null)
            injectableMethods = new HashSet<InjectableMethod>(getInjectableMethodsByName().values());
        return injectableMethods;
    }
}
