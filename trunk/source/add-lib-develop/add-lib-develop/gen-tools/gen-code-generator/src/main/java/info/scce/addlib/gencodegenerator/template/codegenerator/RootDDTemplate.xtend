package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.gmodel.GModel
import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

class RootDDTemplate extends AnnotatedTextTemplate {

    GModel gModel;

    new(GModel gModel) {
        this.gModel = gModel
    }

    override annotation() {
        "root-dd"
    }

    override instantiate(AnnotatedText annotatedText) '''
        «IF annotatedText.body !== null»
            for (LabelledRegularDD<D> «gModel.getLeadVar(annotatedText).getName» : roots) {
                «parentTextListTemplate.instantiate(annotatedText.body)»
            }
        «ENDIF»
    '''
}
