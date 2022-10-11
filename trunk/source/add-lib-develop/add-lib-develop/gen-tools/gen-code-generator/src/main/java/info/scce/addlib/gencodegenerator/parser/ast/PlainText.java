package info.scce.addlib.gencodegenerator.parser.ast;

import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.PlainTextContext;

public class PlainText extends Text {

    private PlainTextContext pt;

    public PlainText(PlainTextContext pt) {
        super(pt);
        this.pt = pt;
    }

    @Override
    public String getPlainText() {
        return pt.getText();
    }

    @Override
    public String serialize() {
        return pt.getText();
    }

    @Override
    public void visit(TreeVisitor visitor) {
        visitor.prePlainText(this);
        visitor.postPlainText(this);
    }
}
