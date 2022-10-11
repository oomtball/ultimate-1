package info.scce.addlib.gencodegenerator.parser.ast;

import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.TextListRootContext;

public class TextListRoot extends Text {

    private TextListRootContext pt;
    private TextList root = null;

    public TextListRoot(TextListRootContext pt) {
        super(pt);
        this.pt = pt;
    }

    public TextList getRoot() {
        if (root == null)
            root = new TextList(pt.root);
        return root;
    }

    @Override
    public String getPlainText() {
        return getRoot().getPlainText();
    }

    @Override
    public String serialize() {
        return getRoot().serialize();
    }

    @Override
    public void visit(TreeVisitor visitor) {
        visitor.preTextListRoot(this);
        getRoot().visit(visitor);
        visitor.postTextListRoot(this);
    }
}
