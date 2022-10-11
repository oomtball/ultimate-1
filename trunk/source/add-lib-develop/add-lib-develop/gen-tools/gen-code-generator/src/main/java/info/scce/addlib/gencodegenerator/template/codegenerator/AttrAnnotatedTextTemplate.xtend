package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.gmodel.GModel
import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class AttrAnnotatedTextTemplate extends AnnotatedTextTemplate {

    GModel gModel

    new(GModel gModel) {
        this.gModel = gModel
    }

    override annotation() {
        "attr"
    }

    override instantiate(AnnotatedText annotatedText) '''
        out.print(«gModel.getLeadVar(annotatedText).getName»«annotatedText.idParamList.map["." + id.camelCase + "()"].join»);
    '''
}
