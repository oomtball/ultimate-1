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

import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class BDDVisitorTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testSimpleBDD(BDDBackend backend) {
        BDDManager ddManager = new BDDManager(backend);

        BDD trueBDD = ddManager.parse("1");
        BDD one = ddManager.readOne();
        assertEquals(trueBDD, one);
        trueBDD.recursiveDeref();

        BDD falseBDD = ddManager.parse("0");
        BDD zero = ddManager.readLogicZero();
        assertEquals(falseBDD, zero);
        falseBDD.recursiveDeref();

        BDD zeroAndZero = ddManager.parse("0 & 0");
        assertEquals(zeroAndZero, zero);
        BDD zeroAndOne = ddManager.parse("0 & 1");
        assertEquals(zeroAndOne, zero);
        BDD oneAndZero = ddManager.parse("1 & 0");
        assertEquals(oneAndZero, zero);
        BDD oneAndOne = ddManager.parse("1 & 1");
        assertEquals(oneAndOne, one);
        zeroAndZero.recursiveDeref();
        zeroAndOne.recursiveDeref();
        oneAndZero.recursiveDeref();
        oneAndOne.recursiveDeref();

        BDD zeroOrZero = ddManager.parse("0 | 0");
        assertEquals(zeroOrZero, zero);
        BDD zeroOrOne = ddManager.parse("0 | 1");
        assertEquals(zeroOrOne, one);
        BDD oneOrZero = ddManager.parse("1 | 0");
        assertEquals(oneOrZero, one);
        BDD oneOrOne = ddManager.parse("1 | 1");
        assertEquals(oneOrOne, one);
        zeroOrZero.recursiveDeref();
        zeroOrOne.recursiveDeref();
        oneOrZero.recursiveDeref();
        oneOrOne.recursiveDeref();

        BDD notOne = ddManager.parse("! 1");
        assertEquals(notOne, zero);
        BDD notZero = ddManager.parse("! 0 and not false");
        assertEquals(notZero, one);
        notOne.recursiveDeref();
        notZero.recursiveDeref();

        zero.recursiveDeref();
        one.recursiveDeref();

        /* Release memory */
        assertEquals(ddManager.checkZeroRef(), 0);
        ddManager.quit();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testVariables(BDDBackend backend) {
        BDDManager ddManager = new BDDManager(backend);
        BDD one = ddManager.readOne();
        BDD zero = ddManager.readLogicZero();

        BDD parsedVar0 = ddManager.parse("a");
        BDD var0 = ddManager.ithVar(0);
        assertEquals(parsedVar0, var0);
        var0.recursiveDeref();

        BDD parsedVar1 = ddManager.parse("b");
        BDD var1 = ddManager.ithVar(1);
        assertEquals(parsedVar1, var1);
        var1.recursiveDeref();

        BDD parsedVar0OrparsedVar1 = parsedVar0.or(parsedVar1);
        BDD or00 = parsedVar0OrparsedVar1.eval(false, false);
        BDD or01 = parsedVar0OrparsedVar1.eval(false, true);
        BDD or10 = parsedVar0OrparsedVar1.eval(true, false);
        BDD or11 = parsedVar0OrparsedVar1.eval(true, true);

        assertEquals(or00, zero);
        assertEquals(or01, one);
        assertEquals(or10, one);
        assertEquals(or11, one);
        parsedVar0OrparsedVar1.recursiveDeref();

        BDD parsedVar0AndparsedVar1 = parsedVar0.and(parsedVar1);
        BDD and00 = parsedVar0AndparsedVar1.eval(false, false);
        BDD and01 = parsedVar0AndparsedVar1.eval(false, true);
        BDD and10 = parsedVar0AndparsedVar1.eval(true, false);
        BDD and11 = parsedVar0AndparsedVar1.eval(true, true);

        assertEquals(and00, zero);
        assertEquals(and01, zero);
        assertEquals(and10, zero);
        assertEquals(and11, one);
        parsedVar0AndparsedVar1.recursiveDeref();
        parsedVar0.recursiveDeref();
        parsedVar1.recursiveDeref();
        one.recursiveDeref();
        zero.recursiveDeref();

        /* Release memory */
        assertEquals(ddManager.checkZeroRef(), 0);
        ddManager.quit();
    }
}
