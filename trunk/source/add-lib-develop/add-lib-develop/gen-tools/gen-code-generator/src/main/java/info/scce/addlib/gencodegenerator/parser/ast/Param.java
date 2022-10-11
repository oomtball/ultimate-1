package info.scce.addlib.gencodegenerator.parser.ast;

import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.ParamContext;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;


public class Param extends Text {

    private ParamContext pt;

    public Param(ParamContext pt) {
        super(pt);
        this.pt = pt;
    }

    public Type getType() {
        if (pt.intValue != null)
            return Type.INT;
        else if (pt.doubleValue != null)
            return Type.DOUBLE;
        else if (pt.booleanValue != null)
            return Type.BOOLEAN;
        else if (pt.idValue != null)
            return Type.ID;
        else if (pt.stringValue != null)
            return Type.STRING;
        return null;
    }

    public int getIntValue() {
        if (getType() == Type.INT)
            return parseInt(pt.intValue.getText());
        return 0;
    }

    public double getDoubleValue() {
        if (getType() == Type.DOUBLE)
            return parseDouble(pt.doubleValue.getText());
        return 0;
    }

    public boolean getBooleanValue() {
        if (getType() == Type.BOOLEAN)
            return parseBoolean(pt.booleanValue.getText());
        return false;
    }

    public String getIdValue() {
        if (getType() == Type.ID)
            return parseId(pt.idValue.getText());
        return null;
    }

    public String getStringValue() {
        if (getType() == Type.STRING)
            return parseString(pt.stringValue.getText());
        return null;
    }

    private String parseId(String en) {
        return en;
    }

    private String parseString(String strEscapedQuoted) {
        String strEscaped = strEscapedQuoted.substring(1, strEscapedQuoted.length() - 1);
        return strEscaped
                .replace("\\\"", "\"")
                .replace("\\'", "'")
                .replace("\\@", "@")
                .replace("\\\\", "\\");
    }

    @Override
    public String getPlainText() {
        return "";
    }

    @Override
    public String serialize() {
        Type t = getType();
        if (t == Type.INT)
            return serializeInt(getIntValue());
        else if (t == Type.DOUBLE)
            return serializeDouble(getDoubleValue());
        else if (t == Type.BOOLEAN)
            return serializeBoolean(getBooleanValue());
        else if (t == Type.ID)
            return serializeId(getIdValue());
        else if (t == Type.STRING)
            return serializeString(getStringValue());
        return null;
    }

    private String serializeInt(int intValue) {
        return "" + intValue;
    }

    private String serializeDouble(double doubleValue) {
        return "" + doubleValue;
    }

    private String serializeBoolean(boolean booleanValue) {
        return "" + booleanValue;
    }

    private String serializeId(String idValue) {
        return idValue;
    }

    private String serializeString(String stringValue) {
        String strEscaped = stringValue
                .replace("\\", "\\\\")
                .replace("@", "\\@")
                .replace("\"", "\\\"");
        String strEscapedQuoted = "\"" + strEscaped + "\"";
        return strEscapedQuoted;
    }

    @Override
    public void visit(TreeVisitor visitor) {
        visitor.preParam(this);
        visitor.postParam(this);
    }

    public static enum Type {
        INT, DOUBLE, BOOLEAN, ID, STRING
    }
}