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

public class JSGeneratorTest extends CodeGeneratorTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public JSGeneratorTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testGeneratedJavaScript() throws IOException, InterruptedException {
        /* Run test only if node is installed */
        requireBinary("node", "-v");

        /* Generate code */
        String objName = "SumOfPredicatesABC";
        File genFile = new File(workdir, objName + ".js");
        JSGenerator<ADD> generator = new JSGenerator<ADD>().withObjName(objName).withNodeExportEnabled();
        generator.generateToFileSystem(genFile, sumOfPredicatesABC);

        /* Copy test program */
        File testProgramFile = new File(workdir, "TestProgram.js");
        URL testProgramResource = getClass().getResource("TestProgram.js");
        FileUtils.copyURLToFile(testProgramResource, testProgramFile);

        /* Execute test program */
        assertSuccessfulExecution("node", testProgramFile.getAbsolutePath());
    }

    @Test
    public void testCodeGeneratorHelloWorld() throws IOException {
        /* Generate the JavaScript implementation to a string */
        LabelledRegularDD<XDD<String>> root = new LabelledRegularDD<>(helloWorld, "helloWorld");
        JSGenerator<XDD<String>> g = new JSGenerator<XDD<String>>().withObjName("HelloWorld");
        String actualJavaScriptCode = g.generateToString(root);

        /* Get expected JavaScript code */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        try (InputStream expectedJavaScriptCodeIStream = cl.getResourceAsStream(
                "info/scce/addlib/codegenerator/HelloWorld.js")) {
            String expectedJavaScriptCode = IOUtils.toString(expectedJavaScriptCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualJavaScriptCode),
                                                            withoutConcreteIds(expectedJavaScriptCode));
        }

    }

    @Test
    public void testArbitraryPredicatesCodeGeneration() throws IOException {

        /* Generate the JavaScript implementation to a string */
        JSGenerator<ADD> g = new JSGenerator<ADD>().withObjName("ArbitraryPredicates");
        g.setLabelTransformMode(LabelTransformMode.TRANSFORM_SPECIAL_TO_UTF16);
        String actualJavaScriptCode = g.generateToString(sumOfArbitraryPredicates);

        /* Get expected JavaScript code */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl);
        try (InputStream expectedJavaScriptCodeIStream = cl.getResourceAsStream(
                "info/scce/addlib/codegenerator/ArbitraryPredicates.js")) {
            String expectedJavaScriptCode = IOUtils.toString(expectedJavaScriptCodeIStream, Charset.defaultCharset());
            /* Assert equality except for varying method names */
            assertEqualsIgnorePlatformSpecificLineSeparator(withoutConcreteIds(actualJavaScriptCode),
                                                            withoutConcreteIds(expectedJavaScriptCode));
        }

    }
}
