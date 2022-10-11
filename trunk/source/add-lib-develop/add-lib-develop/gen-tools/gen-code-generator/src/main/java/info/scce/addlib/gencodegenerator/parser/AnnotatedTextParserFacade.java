package info.scce.addlib.gencodegenerator.parser;

import info.scce.addlib.gencodegenerator.parser.AnnotatedTextParser.TextListRootContext;
import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.InputStream;

public class AnnotatedTextParserFacade {

    private AnnotatedTextParser parser;

    public AnnotatedTextParserFacade(String input) {
        this(new ANTLRInputStream(input));
    }

    public AnnotatedTextParserFacade(InputStream inputStream) throws IOException {
        this(new ANTLRInputStream(inputStream));
    }

    private AnnotatedTextParserFacade(ANTLRInputStream inputStream) {
        BaseErrorListener errorListener = new BaseErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int ln, int cn, String msg,
                                    RecognitionException e) throws ParseCancellationException {

                throw new ParseException(msg, ln, cn);
            }
        };
        AnnotatedTextLexer lexer = new AnnotatedTextLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        parser = new AnnotatedTextParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
    }

    public TextListRoot textListRoot() {
        TextListRootContext textListRoot = parser.textListRoot();
        return new TextListRoot(textListRoot);
    }

    public static class ParseException extends RuntimeException {

        private static final long serialVersionUID = 3487461133146518584L;

        public ParseException(String msg, int ln, int cn) {
            super("syntax error at " + ln + ":" + cn + " " + msg);
        }
    }
}
