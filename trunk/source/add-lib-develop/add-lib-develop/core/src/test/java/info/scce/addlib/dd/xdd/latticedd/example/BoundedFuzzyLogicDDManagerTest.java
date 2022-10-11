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

public class BoundedFuzzyLogicDDManagerTest extends DDManagerTest {

    public static final double EPS = 0.00001;

    private BoundedFuzzyLogicDDManager ddManager;
    private XDD<Double> a;
    private XDD<Double> b;
    private XDD<Double> c;

    @AfterMethod
    public void tearDown() {
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNot(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Double> not_a = a.not();
        assertEquals(not_a.v().doubleValue(), 0.2, EPS);
        not_a.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new BoundedFuzzyLogicDDManager(addBackend);
        a = ddManager.constant(0.8);
        b = ddManager.constant(0.4);
        c = ddManager.constant(0.2);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAnd(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Double> a_and_b = a.and(b);
        assertEquals(a_and_b.v(), 0.2, EPS);
        a_and_b.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testOr(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Double> b_or_c = b.or(c);
        assertEquals(b_or_c.v(), 0.6, EPS);
        b_or_c.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testBot(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Double> zero = ddManager.bot();
        assertEquals(zero.v(), 0, EPS);
        zero.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testTop(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Double> one = ddManager.top();
        assertEquals(one.v(), 1, EPS);
        one.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testRangeCorrection(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<Double> two = ddManager.constant(2.0);
        XDD<Double> three = ddManager.constant(3.0);
        XDD<Double> two_or_three = two.or(three);
        assertEquals(two_or_three.v(), 1.0, EPS);
        two.recursiveDeref();
        three.recursiveDeref();
        two_or_three.recursiveDeref();
    }
}