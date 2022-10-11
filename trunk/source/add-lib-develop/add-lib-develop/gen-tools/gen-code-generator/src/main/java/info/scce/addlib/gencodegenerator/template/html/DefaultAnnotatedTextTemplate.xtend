package info.scce.addlib.gencodegenerator.template.html

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.template.AnnotatedTextTemplate

class DefaultAnnotatedTextTemplate extends AnnotatedTextTemplate {

    override annotation() {
        "default"
    }

    override instantiate(AnnotatedText annotatedText) '''
        <span class="annotated-text">
            <span class="annotation-param-list">@«annotatedText.annotation»(«annotatedText.paramList.map[serialize].join(", ")»)</span>
            «IF annotatedText.body !== null»
                <span class="body">
                    «parentTextListTemplate.instantiate(annotatedText.body)»
                </span>
            «ENDIF»
        </span>
    '''
}
