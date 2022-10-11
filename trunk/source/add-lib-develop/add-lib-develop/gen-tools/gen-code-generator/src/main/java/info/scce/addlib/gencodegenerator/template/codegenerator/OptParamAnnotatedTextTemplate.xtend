package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class OptParamAnnotatedTextTemplate extends AnnotatedTextTemplate {

    override annotation() {
        "opt-param"
    }

    override instantiate(AnnotatedText annotatedText) '''
        «IF annotatedText.body !== null»
            if (this.param«annotatedText.stringParam.id.CamelCase» != null) {
                «parentTextListTemplate.instantiate(annotatedText.body)»
            }
        «ENDIF»
    '''
}
