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
package info.scce.addlib.dd.xdd.latticedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BooleanLogicDDManagerTest extends DDManagerTest {

    private BooleanLogicDDManager ddManager;
    private XDD<Boolean> one;
    private XDD<Boolean> zero;

    @AfterMethod
    public void tearDown() {
        one.recursiveDeref();
        zero.recursiveDeref();
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAnd(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Boolean> one_and_one = one.and(one);
        XDD<Boolean> one_and_zero = one.and(zero);
        XDD<Boolean> zero_and_one = zero.and(one);
        XDD<Boolean> zero_and_zero = zero.and(zero);
        assertTrue(one_and_one.v());
        assertFalse(one_and_zero.v());
        assertFalse(zero_and_one.v());
        assertFalse(zero_and_zero.v());
        one_and_one.recursiveDeref();
        one_and_zero.recursiveDeref();
        zero_and_one.recursiveDeref();
        zero_and_zero.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new BooleanLogicDDManager(addBackend);
        one = ddManager.one();
        zero = ddManager.zero();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testOr(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Boolean> one_or_one = one.or(one);
        XDD<Boolean> one_or_zero = one.or(zero);
        XDD<Boolean> zero_or_one = zero.or(one);
        XDD<Boolean> zero_or_zero = zero.or(zero);
        assertTrue(one_or_one.v());
        assertTrue(one_or_zero.v());
        assertTrue(zero_or_one.v());
        assertFalse(zero_or_zero.v());
        one_or_one.recursiveDeref();
        one_or_zero.recursiveDeref();
        zero_or_one.recursiveDeref();
        zero_or_zero.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNot(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Boolean> not_one = one.not();
        XDD<Boolean> not_zero = zero.not();
        assertFalse(not_one.v());
        assertTrue(not_zero.v());
        not_one.recursiveDeref();
        not_zero.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        setUp(addBackend);

        /* Repeat test for both Boolean values */
        for (int i = 0; i < 2; i++) {
            boolean b = i % 2 == 0;

            /* Assert parseElement is inverse of toString for the value of b */
            XDD<Boolean> xddBoolean = ddManager.constant(b);
            String str = xddBoolean.toString();
            XDD<Boolean> xddBooleanReproduced = ddManager.constant(ddManager.parseElement(str));
            assertEquals(xddBooleanReproduced, xddBoolean);

            /* Release memory */
            xddBoolean.recursiveDeref();
            xddBooleanReproduced.recursiveDeref();
        }
    }
}
