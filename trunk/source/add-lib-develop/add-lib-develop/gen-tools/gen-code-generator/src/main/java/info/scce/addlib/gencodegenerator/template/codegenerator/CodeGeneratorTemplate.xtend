package info.scce.addlib.gencodegenerator.template.codegenerator

import info.scce.addlib.gencodegenerator.gmodel.GModel
import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot
import info.scce.addlib.gencodegenerator.template.Template
import info.scce.addlib.gencodegenerator.template.TextListTemplate

import static extension info.scce.addlib.gencodegenerator.template.TemplateUtils.*

class CodeGeneratorTemplate extends Template<TextListRoot> {

    GModel gModel
    TextListTemplate methodBodyTemplate

    new(GModel gModel) {
        this.gModel = gModel
        methodBodyTemplate = new TextListTemplate().withPlainTextTemplate(new PlainTextTemplate).withAnnotationTemplate(
                new AttrAnnotatedTextTemplate(gModel)).withAnnotationTemplate(new ConstantDDTemplate(gModel)).
        withAnnotationTemplate(new RootDDTemplate(gModel)).withAnnotationTemplate(
                new DisabledParamAnnotatedTextTemplate).withAnnotationTemplate(new EnabledParamAnnotatedTextTemplate).
        withAnnotationTemplate(new InjectableAnnotatedTextTemplate(gModel)).withAnnotationTemplate(
                new InternalDDTemplate(gModel)).withAnnotationTemplate(new LiterallyAnnotatedTextTemplate).
        withAnnotationTemplate(new OptParamAnnotatedTextTemplate).withAnnotationTemplate(
                new ParamAnnotatedTextTemplate)
    }

    override instantiate(TextListRoot exampleCode) {
        val className = gModel.getGeneratorName.id.CamelCase
        '''
            /* This file was generated with the ADD-Lib Gen Tools
             * http://add-lib.scce.info/ */

            package info.scce.addlib.codegenerator;

            import info.scce.addlib.dd.LabelledRegularDD;
            import info.scce.addlib.dd.RegularDD;
            import info.scce.addlib.traverser.PreorderTraverser;

            import java.io.PrintStream;
            import java.util.List;

            public class «className»<D extends RegularDD<?, D>> extends CodeGenerator<D> {
                «IF gModel.hasParams || gModel.hasEnableableParams»

                    «FOR p : gModel.getParams»
                        private String param«p.name.id.CamelCase»«IF p.defaultValue !== null» = «p.defaultValue.javaStringLiteral»«ENDIF»;
                    «ENDFOR»
                    «FOR p : gModel.getEnableableParams»
                        private boolean enableableParam«p.name.id.CamelCase»;
                    «ENDFOR»
                «ENDIF»
                «FOR p : gModel.getParams»

                    public «className»<D> with«p.name.id.CamelCase»(String «p.name.toLowerCase.firstn(3)») {
                        this.param«p.name.id.CamelCase» = «p.name.toLowerCase.firstn(3)»;
                        return this;
                    }
                «ENDFOR»
                «FOR p : gModel.getEnableableParams»

                    public «className»<D> with«p.name.id.CamelCase»Enabled(boolean «p.name.toLowerCase.firstn(3)») {
                        this.enableableParam«p.name.id.CamelCase» = «p.name.toLowerCase.firstn(3)»;
                        return this;
                    }

                    public «className»<D> with«p.name.id.CamelCase»Enabled() {
                        return with«p.name.id.CamelCase»Enabled(true);
                    }

                    public «className»<D> with«p.name.id.CamelCase»Disabled() {
                        return with«p.name.id.CamelCase»Enabled(false);
                    }
                «ENDFOR»

                @Override
                public void generate(PrintStream out, List<LabelledRegularDD<D>> roots) {
                    «methodBodyTemplate.instantiate(exampleCode.root)»
                }
                «FOR inj : gModel.getInjectableMethods»

                    protected void «inj.name.id.camelCase»(PrintStream out«inj.paramList.map[", " + type + " " + name].join») {
                        «methodBodyTemplate.instantiate(inj.body)»
                    }
                «ENDFOR»
            }
        '''
    }
}
