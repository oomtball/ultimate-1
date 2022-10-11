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
package info.scce.addlib.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;
import info.scce.addlib.util.IOUtils;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class CnfparserTest extends DDManagerTest {

    private final BDDBackend bddBackend;
    private BDDManager ddManager;

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public CnfparserTest(BDDBackend bddBackend) {
        this.bddBackend = bddBackend;
    }

    @BeforeClass
    public void setUp() {
        ddManager = new BDDManager(bddBackend);
    }

    @AfterClass
    public void tearDown() {
        /* Release memory */
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test
    public void testParseCnf() throws IOException {
        File cnfFile = File.createTempFile("cnfFile", "");
        cnfFile.deleteOnExit();
        try {
            try (PrintWriter printWriter = IOUtils.getBufferedPrintWriterUTF8(cnfFile)) {
                printWriter.write(" -1 2 3 0\n" + "1 4 -5 0\n" + "-2 -1 4 0");
            }
            CnfParser cnfParser = new CnfParser();
            BDD bddParsed = cnfParser.parseCnf(ddManager, cnfFile);
            BDD var0 = ddManager.ithVar(1);
            BDD var1 = ddManager.ithVar(2);
            BDD var2 = ddManager.ithVar(3);
            BDD var3 = ddManager.ithVar(4);
            BDD var4 = ddManager.ithVar(5);
            BDD notvar0 = var0.not();
            BDD notvar1 = var1.not();
            BDD notvar4 = var4.not();
            BDD int1 = notvar0.or(var1);
            BDD int2 = int1.or(var2);
            BDD int3 = var0.or(var3);
            BDD int4 = int3.or(notvar4);
            BDD int5 = notvar1.or(notvar0);
            BDD int6 = int5.or(var3);
            BDD int7 = int2.and(int4);
            BDD bdd = int6.and(int7);

            assertEquals(bddParsed, bdd);

            for (int i = 0; i < 32; i++) {
                boolean a = String.format("%05d", Integer.parseInt(Integer.toBinaryString(i))).charAt(0) == '1';
                boolean b = String.format("%05d", Integer.parseInt(Integer.toBinaryString(i))).charAt(1) == '1';
                boolean c = String.format("%05d", Integer.parseInt(Integer.toBinaryString(i))).charAt(2) == '1';
                boolean d = String.format("%05d", Integer.parseInt(Integer.toBinaryString(i))).charAt(3) == '1';
                boolean e = String.format("%05d", Integer.parseInt(Integer.toBinaryString(i))).charAt(4) == '1';
                assertEquals(bddParsed.eval(a, b, c, d, e), bdd.eval(a, b, c, d, e));
            }
            var0.recursiveDeref();
            var1.recursiveDeref();
            var2.recursiveDeref();
            var3.recursiveDeref();
            var4.recursiveDeref();
            notvar0.recursiveDeref();
            notvar1.recursiveDeref();
            notvar4.recursiveDeref();
            int1.recursiveDeref();
            int2.recursiveDeref();
            int3.recursiveDeref();
            int4.recursiveDeref();
            int5.recursiveDeref();
            int6.recursiveDeref();
            int7.recursiveDeref();
            bdd.recursiveDeref();
            bddParsed.recursiveDeref();
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }
}
