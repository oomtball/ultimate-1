package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.parser.ast.PlainText
import info.scce.addlib.gencodegenerator.template.Template

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class PlainTextTemplate extends Template<PlainText> {

    override instantiate(PlainText plainText) {
        val lines = plainText.plainText.split(PlainText.NL_REGEX, -1)
        val completeLines = lines.take(lines.size - 1)
        val incompleteLine = lines.last
        '''
            «FOR ln : completeLines»
                out.println(«ln.javaStringLiteral»);
            «ENDFOR»
            «IF !incompleteLine.empty»
                out.print(«incompleteLine.javaStringLiteral»);
            «ENDIF»
        '''
    }
}
