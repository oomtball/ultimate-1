package info.scce.addlib.gencodegenerator.parser;

import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AnnotatedTextParserFacadeTest {

    @Test
    public void testEmptyString() throws IOException {
        String partiallyAnnotatedText = "";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testPlainTextLowerCase() throws IOException {
        String partiallyAnnotatedText = "some lower case plain text";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testPlainTextMixedCase() throws IOException {
        String partiallyAnnotatedText = "SOME miXEd CasE plain TEXT";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testPlainTextUpperCase() throws IOException {
        String partiallyAnnotatedText = "SOME MIXED CASE PLAIN TEXT";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testsPlainTextSpecialChars() throws IOException {
        String partiallyAnnotatedText = "some symbols \"#;:,/\\\\.([{}])_&^%$+=~'!?- and numbers 0123456789";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testsPlainTextMultiline() throws IOException {
        String partiallyAnnotatedText = "one\ntwo\rthree\r\nfour\n\rfive\n\nsix\r\rseven";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testsPlainTextWhitespace() throws IOException {
        String partiallyAnnotatedText = "one two\tthree\t four \tfive  six\t\tseven";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextEmpty() throws IOException {
        String partiallyAnnotatedText = "this first @annotation() is empty";
        String expectedText = "this first  is empty";
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextIdentifier() throws IOException {
        String partiallyAnnotatedText = "some annotations @lower(), @UPPER(), @miXEd(), @with123(), @with_ABZ_019(), @with.and-and_and()";
        String expectedText = "some annotations , , , , , ";
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextParams1() throws IOException {
        String partiallyAnnotatedText = "some @numbers(1, 2, 3)";
        String expectedText = "some ";
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextParams2() throws IOException {
        String partiallyAnnotatedText = "some @numbers(1, 2, 3), @strings(\"Aa\\\"Bb'Cc\", 'Aa\"Bb\\'Cc'), and @ids(Aa, Bb, Cc)";
        String expectedText = "some , , and ";
        String expectedTextWithAnnotations = "some @numbers(1, 2, 3), @strings(\"Aa\\\"Bb'Cc\", \"Aa\\\"Bb'Cc\"), and @ids(Aa, Bb, Cc)";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextParams3() throws IOException {
        String partiallyAnnotatedText = "some more @numbers(-1, 2, +3, -4.5, 6.7, +8.9, -0, 0, +0, -0.0, 0.0, +0.0, -0.01, 0.02, +0.03, -1.2000, 3.4000, 5.6000)";
        String expectedText = "some more ";
        String expectedTextWithAnnotations = "some more @numbers(-1, 2, 3, -4.5, 6.7, 8.9, 0, 0, 0, -0.0, 0.0, 0.0, -0.01, 0.02, 0.03, -1.2, 3.4, 5.6)";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextParams4() throws IOException {
        String partiallyAnnotatedText = "even more @numbers(56.34E12, +56.34E+12, -56.34E-12)";
        String expectedText = "even more ";
        String expectedTextWithAnnotations = "even more @numbers(5.634E13, 5.634E13, -5.634E-11)";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextParamsWhiteSpaceNormalization() throws IOException {
        String partiallyAnnotatedText = "too much whitespace? @ann(  0, \t1,\t 2,\t\t3)";
        String expectedText = "too much whitespace? ";
        String expectedTextWithAnnotations = "too much whitespace? @ann(0, 1, 2, 3)";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextEscapedQuotation() throws IOException {
        String partiallyAnnotatedText = "@literally(\"'''\") and @literally('\"\"\"')";
        String expectedText = " and ";
        String expectedTextWithAnnotations = "@literally(\"'''\") and @literally(\"\\\"\\\"\\\"\")";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextEscapedAtSymbol() throws IOException {
        String partiallyAnnotatedText = "@literally(\"\\@\") and @literally('\\@')";
        String expectedText = " and ";
        String expectedTextWithAnnotations = "@literally(\"\\@\") and @literally(\"\\@\")";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextEmptyBody() throws IOException {
        String partiallyAnnotatedText = "with empty body @ann()'''''' and @ann(1, 2, 3)''''''";
        String expectedText = "with empty body  and ";
        String expectedTextWithAnnotations = "with empty body @ann() and @ann(1, 2, 3)";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextWithBody() throws IOException {
        String partiallyAnnotatedText = "some people: @role('Prof')'''Bernhard''', @role('HiWi')'''Alnis''', @role('WiMi')'''Frederik'''";
        String expectedText = "some people: Bernhard, Alnis, Frederik";
        String expectedTextWithAnnotations = "some people: @role(\"Prof\")'''Bernhard''', @role(\"HiWi\")'''Alnis''', @role(\"WiMi\")'''Frederik'''";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextWithRedundantLinebreakInBody() throws IOException {
        String partiallyAnnotatedText = "some people: @role('Prof')'''\nBernhard\n'''\n, @role('HiWi')'''Alnis''', @role('WiMi')'''\nFrederik'''\n";
        String expectedText = "some people: Bernhard\n, Alnis, Frederik";
        String expectedTextWithAnnotations = "some people: @role(\"Prof\")'''\nBernhard\n'''\n, @role(\"HiWi\")'''Alnis''', @role(\"WiMi\")'''Frederik'''";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextWithLinebreakInBody() throws IOException {
        String partiallyAnnotatedText = "some people:\n@chair('ls5')'''\nBernhard,\nAlnis,\nFrederik\n'''";
        String expectedText = "some people:\nBernhard,\nAlnis,\nFrederik\n";
        String expectedTextWithAnnotations = "some people:\n@chair(\"ls5\")'''\nBernhard,\nAlnis,\nFrederik\n'''\n";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextWithBodyOnly() throws IOException {
        String partiallyAnnotatedText = "some people: @prof'''Bernhard''', @hiwi'''Alnis''', @wimi'''Frederik'''";
        String expectedText = "some people: Bernhard, Alnis, Frederik";
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextWithRedundantWhitespace() throws IOException {
        String partiallyAnnotatedText = "some people: @role \t('Prof')\t '''Bernhard''', @role\t ('HiWi') \t'''Alnis''', @role  ('WiMi')\t\t'''Frederik'''";
        String expectedText = "some people: Bernhard, Alnis, Frederik";
        String expectedTextWithAnnotations = "some people: @role(\"Prof\")'''Bernhard''', @role(\"HiWi\")'''Alnis''', @role(\"WiMi\")'''Frederik'''";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextNested() throws IOException {
        String partiallyAnnotatedText = "@level0()'''l0{@level1()'''l1{@level2()'''l2{core}'''}'''}'''";
        String expectedText = "l0{l1{l2{core}}}";
        String expectedTextWithAnnotations = "@level0'''l0{@level1'''l1{@level2'''l2{core}'''}'''}'''";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextNestedLinebreaks() throws IOException {
        String partiallyAnnotatedText = "@level0()'''\nl0{\n@level1()'''\nl1{\n@level2()'''\nl2{\ncore\n}\n'''\n}\n'''\n}\n'''\n";
        String expectedText = "l0{\nl1{\nl2{\ncore\n}\n}\n}\n";
        String expectedTextWithAnnotations = "@level0'''\nl0{\n@level1'''\nl1{\n@level2'''\nl2{\ncore\n}\n'''\n}\n'''\n}\n'''\n";
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testAnnotatedTextAdditionalImportsExample() throws IOException {
        String partiallyAnnotatedText = "@opt-param(\"import\")'''\nimport @param(\"import\")'''abc.xyz''';\n\n'''\n";
        String expectedText = "import abc.xyz;\n\n";
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testEscapedAt() throws IOException {
        String partiallyAnnotatedText = "sometimes one need the \\@ symbol";
        String expectedText = partiallyAnnotatedText;
        String expectedTextWithAnnotations = partiallyAnnotatedText;
        assertParsingResult(partiallyAnnotatedText, expectedText, expectedTextWithAnnotations);
    }

    @Test
    public void testTripleQuoteUnparsable() throws IOException {
        String unparsable = "tripple'''quotes '''must '''be ''' escaped";
        assertParseException(unparsable);
    }

    @Test
    public void testLonleyAtUnparsable() throws IOException {
        String unparsable = "at symbols @ must be escaped";
        assertParseException(unparsable);
    }

    @Test
    public void testLonleyAtUnparsableInStringLiteral() throws IOException {
        String unparsable = "at \"symbols @ must be escaped even in\" string literals";
        assertParseException(unparsable);
    }

    @Test
    public void testLinebreakInParamlistUnparsable() throws IOException {
        String unparsable = "@paramLists(1, 2, \n4) must be without linebreaks";
        assertParseException(unparsable);
    }

    @Test
    public void testTextInParamlistUnparsable() throws IOException {
        String unparsable = "invalid @ann(#)";
        assertParseException(unparsable);
    }

    @Test
    public void testIncompleteUnparsable1() throws IOException {
        String unparsable = "incomplete @ann(bla bla bla";
        assertParseException(unparsable);
    }

    @Test
    public void testIncompleteUnparsable2() throws IOException {
        String unparsable = "incomplete @ann'''bla bla bla";
        assertParseException(unparsable);
    }

    @Test
    public void testIncompleteUnparsable3() throws IOException {
        String unparsable = "incomplete @ann()'''bla bla bla";
        assertParseException(unparsable);
    }

    private void assertParsingResult(String partiallyAnnotatedText, String expectedText,
                                     String expectedTextWithAnnotations) throws IOException {

        AnnotatedTextParserFacade parser = new AnnotatedTextParserFacade(partiallyAnnotatedText);
        TextListRoot ast = parser.textListRoot();
        String actualText = ast.getPlainText();
        assertEquals(expectedText, actualText);
        String actualTextWithAnnotations = ast.serialize();
        assertEquals(expectedTextWithAnnotations, actualTextWithAnnotations);
    }

    private void assertParseException(String malformedPartiallyAnnotatedText) throws IOException {
        AnnotatedTextParserFacade.ParseException actualException = null;
        try {
            AnnotatedTextParserFacade parser = new AnnotatedTextParserFacade(malformedPartiallyAnnotatedText);
            TextListRoot ast = parser.textListRoot();
            fail("unexpected result " + ast);
        } catch (AnnotatedTextParserFacade.ParseException e) {
            actualException = e;
        }
        assertNotNull(actualException);
    }
}
