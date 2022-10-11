package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.gmodel.GModel
import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

class InternalDDTemplate extends AnnotatedTextTemplate {

    GModel gModel;

    new(GModel gModel) {
        this.gModel = gModel
    }

    override annotation() {
        "internal-dd"
    }

    override instantiate(AnnotatedText annotatedText) '''
        «IF annotatedText.body !== null»
            for (D «gModel.getLeadVar(annotatedText).getName» : new PreorderTraverser<D>(unlabelledRoots(roots))) {
                if (!«gModel.getLeadVar(annotatedText).getName».isConstant()) {
                    «parentTextListTemplate.instantiate(annotatedText.body)»
                }
            }
        «ENDIF»
    '''
}
