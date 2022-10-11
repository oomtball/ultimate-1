package info.scce.addlib.gencodegenerator.template

import info.scce.addlib.gencodegenerator.parser.ast.AnnotatedText
import info.scce.addlib.gencodegenerator.parser.ast.PlainText
import info.scce.addlib.gencodegenerator.parser.ast.Text
import info.scce.addlib.gencodegenerator.parser.ast.TextList
import java.util.HashMap
import java.util.Map

class TextListTemplate extends Template<TextList> {

    Template<PlainText> plainTextTemplate
    Map<String, AnnotatedTextTemplate> annotationTemplates
    AnnotatedTextTemplate defaultAnnotationTemplate;

    new() {
        annotationTemplates = new HashMap
    }

    def withPlainTextTemplate(Template<PlainText> tmpl) {
        plainTextTemplate = tmpl
        this
    }

    def withAnnotationTemplate(AnnotatedTextTemplate tmpl) {
        annotationTemplates.put(tmpl.annotation, tmpl.withParentTextListTemplate(this))
        this
    }

    def withDefaultAnnotationTemplate(AnnotatedTextTemplate tmpl) {
        defaultAnnotationTemplate = tmpl.withParentTextListTemplate(this)
        this
    }

    override instantiate(TextList textList) {
        textList.textList.map[instantiateText].join
    }

    private def dispatch instantiateText(PlainText plainText) {
        plainTextTemplate.tryInstantiateText(plainText)
    }

    private def dispatch instantiateText(AnnotatedText annotatedText) {
        val template = annotationTemplates.getOrDefault(annotatedText.annotation, defaultAnnotationTemplate)
        template.tryInstantiateText(annotatedText)
    }

    private def <T extends Text> tryInstantiateText(Template<T> template, T text) {
        if(template !== null) template.instantiate(text) else ""
    }
}
