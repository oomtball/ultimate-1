package info.scce.addlib.gencodegenerator;

import org.apache.commons.cli.*;

public class ArgsParser {

    private CommandLineParser parser;
    private Options options;
    private CommandLine result = null;

    public ArgsParser() {
        parser = new DefaultParser();

        /* Initialize available options */
        options = new Options();
        Option inputPath = new Option("i", "input", true,
                "path to input file: this is where the annotated example implementation is expected");
        options.addOption(inputPath);
        Option generatorClassName = new Option("n", "name", true, "generator class name");
        options.addOption(generatorClassName);
        Option outputPath = new Option("o", "output", true,
                "path to target file: this is where the generated code generator will be implemented in Java");
        options.addOption(outputPath);
        Option printVersion = new Option("v", "version", false, "print tool name and version");
        options.addOption(printVersion);
        Option genHtml = new Option("html", false, "generate annotated example code as HTML");
        options.addOption(genHtml);
    }

    public boolean tryParse(String... args) {
        try {
            parse(args);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void parse(String[] args) throws ParseException {
        result = parser.parse(options, args);
    }

    public String getInputPath() {
        return result.getOptionValue("i");
    }

    public String getGeneratorClassName() {
        return result.getOptionValue("n");
    }

    public String getOutputPath() {
        return result.getOptionValue("o");
    }

    public boolean isPrintVersion() {
        return result.hasOption("v");
    }

    public boolean isGenHtml() {
        return result.hasOption("html");
    }

    public void printHelp(String appName) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(appName, options);
    }
}
