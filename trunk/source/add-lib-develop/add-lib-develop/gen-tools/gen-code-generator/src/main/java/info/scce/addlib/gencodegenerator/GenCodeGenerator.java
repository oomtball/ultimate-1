package info.scce.addlib.gencodegenerator;

import info.scce.addlib.gencodegenerator.checks.AllChecks;
import info.scce.addlib.gencodegenerator.checks.Check;
import info.scce.addlib.gencodegenerator.gmodel.GModel;
import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParserFacade;
import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;
import info.scce.addlib.gencodegenerator.template.Template;
import info.scce.addlib.gencodegenerator.template.codegenerator.CodeGeneratorTemplate;
import info.scce.addlib.gencodegenerator.template.html.HtmlTemplate;

import java.io.*;

public class GenCodeGenerator {

    private InputStream exampleCodeIStream = System.in;
    private OutputStream targetOStream = System.out;
    private String generatorClassName = "some-generator";

    public static void main(String... args) throws IOException {
        ArgsParser argsParser = new ArgsParser();
        if (argsParser.tryParse(args)) {
            GenCodeGenerator genCodeGenerator = new GenCodeGenerator();

            /* Find input stream */
            String inputPath = argsParser.getInputPath();
            if (inputPath != null)
                genCodeGenerator.from(new FileInputStream(inputPath));

            /* Find output stream */
            String outputPath = argsParser.getOutputPath();
            if (outputPath != null)
                genCodeGenerator.to(new FileOutputStream(outputPath));

            /* Invoke */
            if (argsParser.isPrintVersion()) {
                genCodeGenerator.printVersion();
                System.exit(0);
            } else if (argsParser.isGenHtml()) {
                genCodeGenerator.generateHtml();
                System.exit(0);
            } else {
                String gName = argsParser.getGeneratorClassName();
                if (gName != null)
                    genCodeGenerator.withGeneratorClassName(gName);
                genCodeGenerator.generateCodeGenerator();
                System.exit(0);
            }
        } else {
            AppProperties appProperties = AppProperties.getInstance();
            argsParser.printHelp(appProperties.getAppName());
            System.exit(0);
        }
    }

    public GenCodeGenerator from(InputStream exampleCodeIStream) {
        this.exampleCodeIStream = exampleCodeIStream;
        return this;
    }

    public GenCodeGenerator to(OutputStream targetOStream) {
        this.targetOStream = targetOStream;
        return this;
    }

    public GenCodeGenerator withGeneratorClassName(String generatorClassName) {
        this.generatorClassName = generatorClassName;
        return this;
    }

    private TextListRoot parseExampleCode() throws IOException {
        AnnotatedTextParserFacade codeParser = new AnnotatedTextParserFacade(exampleCodeIStream);
        TextListRoot exampleCode = codeParser.textListRoot();
        return exampleCode;
    }

    private void checkExampleCode(TextListRoot exampleCode) {
        Check allChecks = new AllChecks();
        allChecks.check(exampleCode);
        for (String err : allChecks.getErrors())
            System.err.println("Malformed input: " + err);
    }

    private void generate(TextListRoot exampleCode, Template<TextListRoot> template) {
        PrintStream target = new PrintStream(targetOStream);
        target.print(template.instantiate(exampleCode));
        target.close();
    }

    public void generateCodeGenerator() throws IOException {
        TextListRoot exampleCode = parseExampleCode();
        checkExampleCode(exampleCode);
        GModel gModel = new GModel(generatorClassName, exampleCode);
        CodeGeneratorTemplate template = new CodeGeneratorTemplate(gModel);
        generate(exampleCode, template);
    }

    public void generateHtml() throws IOException {
        TextListRoot exampleCode = parseExampleCode();
        checkExampleCode(exampleCode);
        HtmlTemplate template = new HtmlTemplate();
        generate(exampleCode, template);
    }

    public void printVersion() throws IOException {
        AppProperties p = AppProperties.getInstance();
        System.out.println(p.getAppName());
        System.out.println(p.getAppVersion());
    }
}
