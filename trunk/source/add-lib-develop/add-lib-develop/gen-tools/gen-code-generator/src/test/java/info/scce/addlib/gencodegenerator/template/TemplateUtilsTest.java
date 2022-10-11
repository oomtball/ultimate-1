package info.scce.addlib.gencodegenerator.template;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TemplateUtilsTest {

    @Test
    public void testJavaStringLiteral() throws IOException {
        String str = "Simple string";
        String expectedLiteral = "\"Simple string\"";
        String actualLiteral = TemplateUtils.javaStringLiteral(str);
        assertEquals(expectedLiteral, actualLiteral);
    }

    @Test
    public void testJavaStringLiteralLinebreak() throws IOException {
        String str = "One\nTwo\nThre";
        String expectedLiteral = "\"One\\nTwo\\nThre\"";
        String actualLiteral = TemplateUtils.javaStringLiteral(str);
        assertEquals(expectedLiteral, actualLiteral);
    }

    @Test
    public void testJavaStringLiteralTabBackslashAndLinebreak() throws IOException {
        String str = "One tab\t, two tabs\t\t, three backslashes\\\\\\, four linebreaks\n\n\n\n";
        String expectedLiteral = "\"One tab\\t, two tabs\\t\\t, three backslashes\\\\\\\\\\\\, four linebreaks\\n\\n\\n\\n\"";
        String actualLiteral = TemplateUtils.javaStringLiteral(str);
        assertEquals(expectedLiteral, actualLiteral);
    }

    @Test
    public void testCamelCaseDelimiterSeparated() throws IOException {
        String id = "one-two-three";
        String expectedLowerCamelCase = "oneTwoThree";
        String expectedUpperCamelCase = "OneTwoThree";
        String actualLowerCamelCase = TemplateUtils.camelCase(TemplateUtils.id(id));
        String actualUpperCamelCase = TemplateUtils.CamelCase(TemplateUtils.id(id));
        assertEquals(expectedLowerCamelCase, actualLowerCamelCase);
        assertEquals(expectedUpperCamelCase, actualUpperCamelCase);
    }

    @Test
    public void testCamelCaseMixedDelimiterSeparated() throws IOException {
        String id = "lower-UPPER-MiXed_and#some~123";
        String expectedLowerCamelCase = "lowerUpperMixedAndSome123";
        String expectedUpperCamelCase = "LowerUpperMixedAndSome123";
        String actualLowerCamelCase = TemplateUtils.camelCase(TemplateUtils.id(id));
        String actualUpperCamelCase = TemplateUtils.CamelCase(TemplateUtils.id(id));
        assertEquals(expectedLowerCamelCase, actualLowerCamelCase);
        assertEquals(expectedUpperCamelCase, actualUpperCamelCase);
    }

    @Test
    public void testUnderscoreSeparatedMixedDelimiterSeparated() throws IOException {
        String id = "lower-UPPER-MiXed_and#some~123";
        String expectedUpperUnderscoreSeparated = "lower_upper_mixed_and_some_123";
        String expectedLowerUnderscoreSeparated = "LOWER_UPPER_MIXED_AND_SOME_123";
        String actualLowerUnderscoreSeparated = TemplateUtils.underscore_separated(TemplateUtils.id(id));
        String actualUpperUnderscoreSeparated = TemplateUtils.UNDERSCORE_SEPARATED(TemplateUtils.id(id));
        assertEquals(expectedUpperUnderscoreSeparated, actualLowerUnderscoreSeparated);
        assertEquals(expectedLowerUnderscoreSeparated, actualUpperUnderscoreSeparated);
    }

    @Test
    public void testUnderscoreSeparatedAcronymsMerged() throws IOException {
        String id = "acronym-a-d-d";
        String expectedUpperUnderscoreSeparated = "acronym_add";
        String expectedLowerUnderscoreSeparated = "ACRONYM_ADD";
        String actualLowerUnderscoreSeparated = TemplateUtils.underscore_separated(TemplateUtils.id(id));
        String actualUpperUnderscoreSeparated = TemplateUtils.UNDERSCORE_SEPARATED(TemplateUtils.id(id));
        assertEquals(expectedUpperUnderscoreSeparated, actualLowerUnderscoreSeparated);
        assertEquals(expectedLowerUnderscoreSeparated, actualUpperUnderscoreSeparated);
    }

    @Test
    public void testCamelCaseAcronymsNotMerged() throws IOException {
        String id = "acronym-a-d-d";
        String expectedLowerCamelCase = "acronymADD";
        String expectedUpperCamelCase = "AcronymADD";
        String actualLowerCamelCase = TemplateUtils.camelCase(TemplateUtils.id(id));
        String actualUpperCamelCase = TemplateUtils.CamelCase(TemplateUtils.id(id));
        assertEquals(expectedLowerCamelCase, actualLowerCamelCase);
        assertEquals(expectedUpperCamelCase, actualUpperCamelCase);
    }
}
