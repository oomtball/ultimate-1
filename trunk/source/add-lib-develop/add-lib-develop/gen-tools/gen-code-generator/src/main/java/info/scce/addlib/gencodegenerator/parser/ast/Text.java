package info.scce.addlib.gencodegenerator.parser.ast;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.regex.Pattern;

public abstract class Text {

    public static final String NL_REGEX = "\r\n|\r|\n";
    protected static Pattern nlPattern = null;
    private ParseTree pt;

    public Text(ParseTree pt) {
        this.pt = pt;
    }

    protected Pattern nlPattern() {
        if (nlPattern == null)
            nlPattern = Pattern.compile(NL_REGEX);
        return nlPattern;
    }

    public abstract String getPlainText();

    public abstract String serialize();

    public abstract void visit(TreeVisitor visitor);

    @Override
    public String toString() {
        return pt.toStringTree();
    }

    public static abstract class TreeVisitor {

        public void preTextListRoot(TextListRoot textListRoot) {
        }

        public void postTextListRoot(TextListRoot textListRoot) {
        }

        public void preTextList(TextList textList) {
        }

        public void postTextList(TextList textList) {
        }

        public void preAnnotatedText(AnnotatedText annotatedText) {
        }

        public void postAnnotatedText(AnnotatedText annotatedText) {
        }

        public void preParam(Param param) {
        }

        public void postParam(Param param) {
        }

        public void prePlainText(PlainText plainText) {
        }

        public void postPlainText(PlainText plainText) {
        }
    }
}
