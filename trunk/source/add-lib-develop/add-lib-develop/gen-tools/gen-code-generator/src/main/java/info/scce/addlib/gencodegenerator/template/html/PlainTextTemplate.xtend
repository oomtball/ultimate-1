package info.scce.addlib.gencodegenerator.template.html

import info.scce.addlib.gencodegenerator.parser.ast.PlainText
import info.scce.addlib.gencodegenerator.template.Template

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class PlainTextTemplate extends Template<PlainText> {

    override instantiate(PlainText plainText) '''
        <span class="plain-text">«plainText.plainText.escHtml.highlightLinebreak»</span>
    '''

    private def highlightLinebreak(String html) {
        html.replace("<br/>", "&#x2936;<br/>")
    }
}
