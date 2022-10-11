package info.scce.addlib.gencodegenerator.parser.ast;

import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.AnnotatedTextContext;
import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.PlainTextContext;
import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.TextListContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;


public class TextList extends Text {

    private TextListContext pt;
    private List<Text> textList = null;

    public TextList(TextListContext pt) {
        super(pt);
        this.pt = pt;
    }

    public List<Text> getTextList() {
        if (textList == null) {
            textList = new ArrayList<Text>();
            if (pt.children != null) {
                for (ParseTree c : pt.children) {
                    if (c instanceof PlainTextContext)
                        textList.add(new PlainText((PlainTextContext) c));
                    else if (c instanceof AnnotatedTextContext)
                        textList.add(new AnnotatedText((AnnotatedTextContext) c));
                }
            }
        }
        return textList;
    }

    @Override
    public String getPlainText() {
        return getTextList().stream().map(Text::getPlainText).collect(joining());
    }

    @Override
    public String serialize() {
        return getTextList().stream().map(Text::serialize).collect(joining());
    }

    @Override
    public void visit(TreeVisitor visitor) {
        visitor.preTextList(this);
        for (Text t : getTextList()) {
            if (t instanceof PlainText) {
                PlainText pt = (PlainText) t;
                pt.visit(visitor);
            } else if (t instanceof AnnotatedText) {
                AnnotatedText at = (AnnotatedText) t;
                at.visit(visitor);
            }
        }
        visitor.postTextList(this);
    }
}
