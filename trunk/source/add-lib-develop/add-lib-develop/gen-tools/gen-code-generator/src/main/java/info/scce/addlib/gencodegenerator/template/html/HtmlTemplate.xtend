package info.scce.addlib.gencodegenerator.template.html

import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot
import info.scce.addlib.gencodegenerator.template.Template
import info.scce.addlib.gencodegenerator.template.TextListTemplate

class HtmlTemplate extends Template<TextListRoot> {

    TextListTemplate htmlBodyTemplate

    new() {
        htmlBodyTemplate = new TextListTemplate().withPlainTextTemplate(new PlainTextTemplate).
        withDefaultAnnotationTemplate(new DefaultAnnotatedTextTemplate)
    }

    override instantiate(TextListRoot exampleCode) '''
        <!DOCTYPE html>
        <html>
            <head>
                <style>
                    body {
                        font-size: 0;
                        padding: 2px;
                        margin: 2px;
                    }
                    .plain-text {
                        vertical-align: top;
                        font: 16px/32px monospace;
                        white-space: pre;
                    }

                    .annotated-text {
                        position: relative;
                        display: inline;
                        padding: 0 8px 32px 8px;
                        background: #44b3c2;
                        transition: background 1s;
                    }
                    .annotated-text .annotation-param-list {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 8px;
                        height: 8px;
                        cursor: default;
                        font: 6px/8px monospace;
                        display: block;
                        color: transparent;
                        background: linear-gradient(135deg,  black 0%,  black 50%,   transparent 51%,  transparent 100%);
                        white-space: nowrap;
                        overflow: hidden;
                        z-index: 1;
                    }
                    .annotated-text .annotation-param-list:hover {
                        position: absolute;
                        top: -8px;
                        font: 12px/16px monospace;
                        width: auto;
                        height: 16px;
                        padding: 0 4px;
                        color: white;
                        z-index: 2;
                        background: black;
                    }
                    .annotated-text: hover {
                        background: #5ECDDC;
                    }
                    .annotated-text .annotated-text {
                        background: #f1a94e;
                        padding-bottom: 28px;
                    }
                    .annotated-text .annotated-text:hover {
                        background: #FFC368;
                    }
                    .annotated-text .annotated-text .annotated-text {
                        background: #e45641;
                        padding-bottom: 24px;
                    }
                    .annotated-text .annotated-text .annotated-text:hover {
                        background: #FE705B;
                    }
                </style>
            </head>
            <body>
                «htmlBodyTemplate.instantiate(exampleCode.root)»
            </body>
        </html>
    '''
}
