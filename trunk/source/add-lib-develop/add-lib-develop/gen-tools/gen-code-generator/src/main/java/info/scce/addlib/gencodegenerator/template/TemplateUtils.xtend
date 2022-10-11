package info.scce.addlib.gencodegenerator.template

import java.util.ArrayList

import static java.lang.Math.*

class TemplateUtils {

    static def javaStringLiteral(String str) {
        "\"" + str.escBackslash.escDoubleQuote.escWhitespace + "\""
    }

    private static def escBackslash(String str) {
        str.replace("\\", "\\\\")
    }

    private static def escDoubleQuote(String str) {
        str.replace("\"", "\\\"")
    }

    private static def escWhitespace(String str) {
        str.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
    }

    static def escHtml(String str) {
        str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace("\n",
        "<br/>").replace("\n", "");
    }

    static def UNDERSCORE_SEPARATED(Iterable<String> id) {
        id.singleCharsMerged.map[toUpperCase].join("_")
    }

    static def underscore_separated(Iterable<String> id) {
        id.singleCharsMerged.map[toLowerCase].join("_")
    }

    private static def singleCharsMerged(Iterable<String> id) {
        val idSenatized = new ArrayList
        var buffer = "";
        for(e : id) {
            if(e.length > 1) {
                if(!buffer.empty) {
                    idSenatized.add(buffer)
                    buffer = ""
                }
                idSenatized.add(e)
            } else {
                buffer += e
            }
        }
        if(!buffer.empty)
            idSenatized.add(buffer)
        idSenatized
    }

    static def camelCase(Iterable<String> id) {
        if(!id.empty)
            id.head.toLowerCase + id.tail.CamelCase
        else
            ""
    }

    static def CamelCase(Iterable<String> id) {
        id.filter[!empty].map[substring(0, 1).toUpperCase + substring(1).toLowerCase].join
    }

    static def id(String str) {
        str.split("[^a-zA-Z0-9]").toList.filter[!empty]
    }

    static def firstn(String str, int n) {
        str.substring(0, min(str.length, n))
    }
}
