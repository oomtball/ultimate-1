/* Copyright (c) 2017-2022, TU Dortmund University
 * This file is part of ADD-Lib, https://add-lib.scce.info/.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the TU Dortmund University nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package info.scce.addlib.codegenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDException;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertNotNull;

public class JavaGeneratorTest extends CodeGeneratorTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public JavaGeneratorTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testGeneratedJava() throws IOException, InterruptedException {
        /* Run test only if javac and java are installed */
        requireBinary("javac", "-version");
        requireBinary("java", "-version");

        /* Generate code */
        String className = "SumOfPredicatesABC";
        File genFile = new File(workdir, className + ".java");
        JavaGenerator<ADD> generator = new JavaGenerator<ADD>().withClassName(className).withStaticMethodsEnabled();
        generator.generateToFileSystem(genFile, sumOfPredicatesABC);

        /* Copy test program */
        File testProgramFile = new File(workdir, "TestProgram.java");
        File predicatesFile = new File(workdir, "Predicates.java");
        URL testProgramResource = getClass().getResource("TestProgram.java");
        FileUtils.copyURLToFile(testProgramResource, testProgramFile);
        URL predicatesResource = getClass().getResource("Predicates.java");
        FileUtils.copyURLToFile(predicatesResource, predicatesFile);

        /* Compile test program */
        File testProgramClasses = new File(workdir, "classes");
        boolean folderCreated = testProgramClasses.mkdir();
        if (!folderCreated && !testProgramClasses.exists()) {
            throw new IOException("Folder could not be created!");
        }
        assertSuccessfulExecution("javac",
                                  genFile.getAbsolutePath(),
                                  testProgramFile.getAbsolutePath(),
                                  predicatesFile.getAbsolutePath(),
                                  "-d",
                                  testProgramClasses.getAbsolutePath());

        /* Execute test program */
        assertSuccessfulExecution("java", "-cp", testProgramClasses.getAbsolutePath(), "TestProgram");
    }

    @Test
    public void testCodeGenerator() throws IOException {
        /* Code from above generating the Java implementation to a string */
        LabelledRegularDD<XDD<String>> root = new LabelledRegularDD<>(helloWorld, "helloWorld");
        JavaGenerator<XDD<String>> g = new JavaGenerator<XDD<String>>().withClassName("HelloWorld");
        String actualJavaCode = g.generateToString(root);

        /* Get expected Java code */
        try (InputStream expectedJavaCodeIStream = getClass().getResourceAsStream("HelloWorld.java")) {
            String expectedJavaCode = IOUtils.toString(expectedJavaCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualJavaCode),
                                                            withoutConcreteIds(expectedJavaCode));
        }
    }

    @Test
    public void testArbitraryPredicatesCodeGeneration() throws IOException {

        /* Generate the Java implementation to a string */
        JavaGenerator<ADD> g = new JavaGenerator<ADD>().withClassName("ArbitraryPredicates");
        g.setLabelTransformMode(LabelTransformMode.TRANSFORM_SPECIAL_TO_UTF16);
        String actualJavaCode = g.generateToString(sumOfArbitraryPredicates);

        /* Get expected Java code */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        try (InputStream expectedJavaCodeIStream = cl.getResourceAsStream(
                "info/scce/addlib/codegenerator/ArbitraryPredicates.java")) {
            String expectedJavaCode = IOUtils.toString(expectedJavaCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualJavaCode),
                                                            withoutConcreteIds(expectedJavaCode));
        }
    }

    @Test
    public void testArbitraryPredicatesDiscardCodeGeneration() throws IOException {

        /* Generate the Java implementation to a string */
        JavaGenerator<ADD> g = new JavaGenerator<ADD>().withClassName("ArbitraryPredicates");
        g.setLabelTransformMode(LabelTransformMode.DISCARD_SPECIAL);
        String actualJavaCode = g.generateToString(sumOfArbitraryPredicates);

        /* Get expected Java code */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        try (InputStream expectedJavaCodeIStream = cl.getResourceAsStream(
                "info/scce/addlib/codegenerator/ArbitraryPredicatesDiscard.java")) {
            String expectedJavaCode = IOUtils.toString(expectedJavaCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualJavaCode),
                                                            withoutConcreteIds(expectedJavaCode));
        }
    }

    @Test(expectedExceptions = DDException.class)
    public void testForbidSpecialCharactersException() throws UnsupportedEncodingException {

        /* Forbid special characters and trigger exception */
        JavaGenerator<ADD> g = new JavaGenerator<ADD>().withClassName("ArbitraryPredicates");
        g.setLabelTransformMode(LabelTransformMode.FORBID_SPECIAL);
        g.generateToString(sumOfArbitraryPredicates);
    }

}
