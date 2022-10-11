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
import java.net.URL;
import java.nio.charset.Charset;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertNotNull;

public class PythonGeneratorTest extends CodeGeneratorTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public PythonGeneratorTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testGeneratedPython() throws IOException, InterruptedException {
        /* Run test only if python is installed */
        requireBinary("python", "-V");

        /* Generate code */
        String className = "SumOfPredicatesABC";
        File genFile = new File(workdir, className + ".py");
        PythonGenerator<ADD> generator = new PythonGenerator<ADD>().withClassName(className);
        generator.generateToFileSystem(genFile, sumOfPredicatesABC);

        /* Copy test program */
        File testProgramFile = new File(workdir, "TestProgram.py");
        File predicatesFile = new File(workdir, "Predicates.py");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        URL testProgramResource = cl.getResource("TestProgram.py");
        if (testProgramResource != null) {
            FileUtils.copyURLToFile(testProgramResource, testProgramFile);
        }
        URL predicatesResource = cl.getResource("Predicates.py");
        if (predicatesResource != null) {
            FileUtils.copyURLToFile(predicatesResource, predicatesFile);
        }

        /* Execute test program */
        assertSuccessfulExecution("python", testProgramFile.getAbsolutePath());
    }

    @Test
    public void testResultOfCodeGenerationExampleForDocumentation() throws IOException {
        /* Generate the Python implementation to a string */
        LabelledRegularDD<XDD<String>> root = new LabelledRegularDD<>(helloWorld, "helloWorld");
        PythonGenerator<XDD<String>> g = new PythonGenerator<XDD<String>>().withClassName("HelloWorld");
        String actualPythonCode = g.generateToString(root);

        /* Get expected Python code */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        try (InputStream expectedPythonCodeIStream = cl.getResourceAsStream("HelloWorld.py")) {
            String expectedPythonCode = IOUtils.toString(expectedPythonCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualPythonCode),
                                                            withoutConcreteIds(expectedPythonCode));
        }
    }

    @Test
    public void testArbitraryPredicatesCodeGeneration() throws IOException {

        /* Generate the Python implementation to a string */
        PythonGenerator<ADD> g = new PythonGenerator<ADD>().withClassName("ArbitraryPredicates");
        g.setLabelTransformMode(LabelTransformMode.TRANSFORM_SPECIAL_TO_UTF16);
        String actualPythonCode = g.generateToString(sumOfArbitraryPredicates);

        /* Get expected Python code */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        try (InputStream expectedPythonCodeIStream = cl.getResourceAsStream(
                "info/scce/addlib/codegenerator/ArbitraryPredicates.py")) {
            String expectedPythonCode = IOUtils.toString(expectedPythonCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualPythonCode),
                                                            withoutConcreteIds(expectedPythonCode));
        }
    }
}
