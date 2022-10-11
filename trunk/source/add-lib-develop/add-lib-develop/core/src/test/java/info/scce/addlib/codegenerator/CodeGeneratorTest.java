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

import com.google.common.io.Files;
import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.grouplikedd.example.StringMonoidDDManager;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import static org.testng.Assert.assertEquals;

public abstract class CodeGeneratorTest extends DDManagerTest {

    private static final String NL_REGEX = "\\r\\n?|\\n";
    private static final String EVAL_ID_REGEX = "eval[0-9]*";

    protected ADDBackend addBackend;
    protected ADDManager ddManager;
    protected File workdir;
    protected File redirectedOutput;
    protected File redirectedError;
    protected LabelledRegularDD<ADD> sumOfPredicatesABC;
    protected LabelledRegularDD<ADD> sumOfArbitraryPredicates;
    protected LabelledRegularDD<ADD> xorAB;
    protected XDD<String> helloWorld;
    private StringMonoidDDManager stringMonoidDDManager;

    protected CodeGeneratorTest(ADDBackend addBackend) {
        this.addBackend = addBackend;
    }

    public static void requireBinary(String... command) {
        try {
            Process process = new ProcessBuilder(command).start();
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new SkipException("Binary terminated with error-code != 0");
            }
        } catch (IOException | InterruptedException e) {
            throw new SkipException("Error while checking binary", e);
        }
    }

    @BeforeClass
    public void setUp() throws IOException {
        /* Create managers */
        ddManager = new ADDManager(addBackend);
        stringMonoidDDManager = new StringMonoidDDManager(addBackend);

        final File tmpFolder = Files.createTempDir();
        tmpFolder.deleteOnExit();

        /* Create test DDs*/
        sumOfPredicatesABC = sumOfPredicatesABC();
        sumOfArbitraryPredicates = sumOfArbitraryPredicates();
        helloWorld = helloWorld();
        xorAB = xorAB();
        workdir = new File(tmpFolder, "workdir_" + getClass().getSimpleName());
        boolean folderCreated = workdir.mkdir();
        if (!folderCreated && !workdir.exists()) {
            throw new IOException("Folder could not be created!");
        }
        redirectedOutput = new File(tmpFolder, "redirectedOutput");
        redirectedError = new File(tmpFolder, "redirectedError");
    }

    private LabelledRegularDD<ADD> sumOfPredicatesABC() {

        /* Get variables */
        ADD predA = ddManager.namedVar("predA");
        ADD predB = ddManager.namedVar("predB");
        ADD predC = ddManager.namedVar("predC");

        /* a + b */
        ADD sumAB = predA.plus(predB);
        predA.recursiveDeref();
        predB.recursiveDeref();

        /* a + b + c */
        ADD sumABC = sumAB.plus(predC);
        sumAB.recursiveDeref();
        predC.recursiveDeref();

        /* Give it a name */
        return new LabelledRegularDD<>(sumABC, "sumOfPredicatesABC");
    }

    private LabelledRegularDD<ADD> sumOfArbitraryPredicates() {

        /* Get variables */
        ADD predA = ddManager.namedVar("𒓕x>20?");
        ADD predB = ddManager.namedVar("ä<10-v");

        /* a + b */
        ADD sumAB = predA.plus(predB);
        predA.recursiveDeref();
        predB.recursiveDeref();

        /* Give it a name */
        return new LabelledRegularDD<>(sumAB, "sumOfArbitraryPredicates");
    }

    private XDD<String> helloWorld() {

        /* Create variables */
        XDD<String> hello = stringMonoidDDManager.namedVar("sayHello", "Hello ", "***** ");
        XDD<String> world = stringMonoidDDManager.namedVar("sayWorld", "World!", "*****!");

        /* Create HelloWorld-XDD */
        helloWorld = hello.join(world);
        hello.recursiveDeref();
        world.recursiveDeref();

        return helloWorld;
    }

    private LabelledRegularDD<ADD> xorAB() {

        /* Get variables */
        ADD x0 = ddManager.namedVar("SomeLongVariableName");
        ADD x1 = ddManager.namedVar("SomeName");

        /* Build exclusive disjunction */
        ADD xor = x0.xor(x1);
        x0.recursiveDeref();
        x1.recursiveDeref();

        /* Give it a name */
        return new LabelledRegularDD<>(xor, "sumOfPredicatesABC");
    }

    /* Additional assertions */

    @AfterClass
    public void tearDown() {

        /* Release test ADDs */
        sumOfPredicatesABC.dd().recursiveDeref();
        sumOfArbitraryPredicates.dd().recursiveDeref();
        helloWorld.recursiveDeref();
        xorAB.dd().recursiveDeref();

        /* Release memory */
        assertRefCountZeroAndQuit(ddManager);
        assertRefCountZeroAndQuit(stringMonoidDDManager);
    }

    protected void assertSuccessfulExecution(String... command) throws IOException, InterruptedException {
        Process p = new ProcessBuilder().command(command)
                                        .redirectOutput(redirectedOutput)
                                        .redirectError(redirectedError)
                                        .start();
        int expectedExitCode = 0;
        int actualExitCode = p.waitFor();
        assertEquals(actualExitCode, expectedExitCode);
    }

    protected void assertEqualsIgnorePlatformSpecificLineSeparator(String actual, String expected) {
        String actualWithSystemLineSeparator = actual.replaceAll(NL_REGEX, System.lineSeparator());
        String expectedWithSystemLineSeparator = expected.replaceAll(NL_REGEX, System.lineSeparator());
        assertEquals(actualWithSystemLineSeparator, expectedWithSystemLineSeparator);
    }

    protected String withoutConcreteIds(String code) {
        return code.replaceAll(EVAL_ID_REGEX, "eval********");
    }
}
