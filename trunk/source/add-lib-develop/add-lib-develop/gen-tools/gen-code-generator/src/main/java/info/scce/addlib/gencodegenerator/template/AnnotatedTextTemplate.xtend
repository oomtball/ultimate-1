package info.scce.addlib.gencodegenerator.template

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText

abstract class AnnotatedTextTemplate extends Template<AnnotatedText> {

    protected TextListTemplate parentTextListTemplate;

    def withParentTextListTemplate(TextListTemplate parentTextListTemplate) {
        this.parentTextListTemplate = parentTextListTemplate
        this
    }

    abstract def String annotation()
}
