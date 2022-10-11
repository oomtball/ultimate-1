package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class ParamAnnotatedTextTemplate extends AnnotatedTextTemplate {

    override annotation() {
        "param"
    }

    override instantiate(AnnotatedText annotatedText) '''
        if (this.param«annotatedText.stringParam.id.CamelCase» != null)
            out.print(this.param«annotatedText.stringParam.id.CamelCase»);
    '''
}
