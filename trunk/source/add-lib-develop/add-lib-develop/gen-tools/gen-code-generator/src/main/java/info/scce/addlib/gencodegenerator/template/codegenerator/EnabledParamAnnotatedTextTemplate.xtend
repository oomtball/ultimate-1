package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class EnabledParamAnnotatedTextTemplate extends AnnotatedTextTemplate {

    override annotation() {
        "enabled-param"
    }

    override instantiate(AnnotatedText annotatedText) '''
        «IF annotatedText.body !== null»
            if (this.enableableParam«annotatedText.stringParam.id.CamelCase») {
                «parentTextListTemplate.instantiate(annotatedText.body)»
            }
        «ENDIF»
    '''
}
