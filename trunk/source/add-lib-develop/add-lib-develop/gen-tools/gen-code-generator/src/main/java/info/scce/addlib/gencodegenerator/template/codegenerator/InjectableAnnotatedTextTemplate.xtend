package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.gmodel.GModel
import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class InjectableAnnotatedTextTemplate extends AnnotatedTextTemplate {

    GModel gModel

    new(GModel gModel) {
        this.gModel = gModel
    }

    override annotation() {
        "injectable"
    }

    override instantiate(AnnotatedText atxt) '''
        «atxt.stringParam.id.camelCase»(out«gModel.getInjectableMethod(atxt.stringParam).paramList.map[", " + name.id.camelCase].join»);
    '''
}
