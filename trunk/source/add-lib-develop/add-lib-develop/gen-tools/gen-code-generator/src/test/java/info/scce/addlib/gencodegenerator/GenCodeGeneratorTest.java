package info.scce.addlib.gencodegenerator;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class GenCodeGeneratorTest {

    @Test
    public void testGenerateJavaCodeGenerator() throws IOException {
        ClassLoader cl = getClass().getClassLoader();

        /* Get expected Java implementation */
        String expectedJavaJavaCodeGeneratorFilename = "JavaGenerator.java";
        InputStream expectedJavaCodeGeneratorIStream = cl.getResourceAsStream(expectedJavaJavaCodeGeneratorFilename);
        String expectedJavaCodeGenerator = IOUtils.toString(expectedJavaCodeGeneratorIStream, Charset.defaultCharset());

        /* Get example code */
        String exampleCodeFilename = "ADDLibDecisionService.java.example";
        InputStream exampleCodeIStream = cl.getResourceAsStream(exampleCodeFilename);

        /* Generate actual Java implementation */
        ByteArrayOutputStream actualJavaCodeGeneratorOStream = new ByteArrayOutputStream();
        GenCodeGenerator g = new GenCodeGenerator();
        g.from(exampleCodeIStream).to(actualJavaCodeGeneratorOStream).withGeneratorClassName("java-generator")
                .generateCodeGenerator();
        String actualJavaCodeGenerator = actualJavaCodeGeneratorOStream.toString();

        /* Assert equality */
        assertEquals(expectedJavaCodeGenerator, actualJavaCodeGenerator);
    }
}
