package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class LiterallyAnnotatedTextTemplate extends AnnotatedTextTemplate {

    override annotation() {
        "literally"
    }

    override instantiate(AnnotatedText annotatedText) '''
        print(«annotatedText.stringParam.javaStringLiteral»);
    '''
}
