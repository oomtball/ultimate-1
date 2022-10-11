package info.scce.addlib.gencodegenerator.parser.ast;

import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.AnnotatedTextContext;
import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.ParamContext;
import info.scce.addlib.gencodegenerator.parser.ast.Param.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class AnnotatedText extends Text {

    private AnnotatedTextContext pt;
    private String annotation = null;
    private List<Param> paramList = null;
    private TextList body = null;

    public AnnotatedText(AnnotatedTextContext pt) {
        super(pt);
        this.pt = pt;
    }

    public String getAnnotation() {
        if (annotation == null)
            annotation = pt.annotation.getText();
        return annotation;
    }

    public List<Param> getParamList() {
        if (paramList == null) {
            paramList = new ArrayList<Param>();
            if (pt.paramList != null) {
                for (ParamContext p : pt.paramList.paramList)
                    paramList.add(new Param(p));
            }
        }
        return paramList;
    }

    public boolean hasNoParams() {
        return getParamList().isEmpty();
    }

    public boolean hasParams() {
        return !hasNoParams();
    }

    public List<Integer> getIntParamList() {
        return getParamList().stream().filter(p -> p.getType() == Type.INT).map(p -> p.getIntValue()).collect(toList());
    }

    public int getIntParam() {
        return getIntParamList().get(0);
    }

    public List<Double> getDoubleParamList() {
        return getParamList().stream().filter(p -> p.getType() == Type.DOUBLE).map(p -> p.getDoubleValue())
                .collect(toList());
    }

    public double getDoubleParam() {
        return getDoubleParamList().get(0);
    }

    public List<Boolean> getBooleanParamList() {
        return getParamList().stream().filter(p -> p.getType() == Type.BOOLEAN).map(p -> p.getBooleanValue())
                .collect(toList());
    }

    public boolean getBooleanParam() {
        return getBooleanParamList().get(0);
    }

    public List<String> getIdParamList() {
        return getParamList().stream().filter(p -> p.getType() == Type.ID).map(p -> p.getIdValue()).collect(toList());
    }

    public String getIdParam() {
        return getIdParamList().get(0);
    }

    public List<String> getStringParamList() {
        return getParamList().stream().filter(p -> p.getType() == Type.STRING).map(p -> p.getStringValue())
                .collect(toList());
    }

    public String getStringParam() {
        return getStringParamList().get(0);
    }

    public TextList getBody() {
        if (body == null && pt.body != null)
            body = new TextList(pt.body.body);
        return body;
    }

    @Override
    public String getPlainText() {
        TextList body = getBody();
        return body != null ? body.getPlainText() : "";
    }

    @Override
    public String serialize() {

        /* Compose annotation */
        String annotation = getAnnotation();
        String annotationStr = "@" + annotation;

        /* Compose body */
        TextList body = getBody();
        String bodyStr = "";
        if (body != null) {
            bodyStr = body.serialize();
            if (!bodyStr.isEmpty()) {
                Matcher nlMatcher = nlPattern().matcher(bodyStr);
                if (nlMatcher.find()) {
                    String nl = nlMatcher.group();
                    bodyStr = "'''" + nl + bodyStr + "'''" + nl;
                } else {
                    bodyStr = "'''" + bodyStr + "'''";
                }
            }
        }

        /* Compose parameter list */
        List<Param> paramList = getParamList();
        String paramListStr = "";
        if (!paramList.isEmpty() || bodyStr.isEmpty())
            paramListStr = "(" + paramList.stream().map(Param::serialize).collect(joining(", ")) + ")";

        /* Compose */
        return annotationStr + paramListStr + bodyStr;
    }

    @Override
    public void visit(TreeVisitor visitor) {
        visitor.preAnnotatedText(this);
        for (Param p : getParamList())
            p.visit(visitor);
        TextList body = getBody();
        if (body != null)
            body.visit(visitor);
        visitor.postAnnotatedText(this);
    }
}
