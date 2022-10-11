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
package info.scce.addlib.dd.xdd.ringlikedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.grouplikedd.example.StringMonoidDDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ArithmeticDDManagerTest extends DDManagerTest {

    public static final double EPS = 0.00001;

    private ArithmeticDDManager ddManager;

    private XDD<Double> five;
    private XDD<Double> four;
    private XDD<Double> sevenPointFive;
    private XDD<Double> minusTwelve;
    private XDD<Double> threePointTwo;

    @AfterMethod
    public void tearDown() {

        /* Release memory of constants */
        five.recursiveDeref();
        four.recursiveDeref();
        sevenPointFive.recursiveDeref();
        minusTwelve.recursiveDeref();
        threePointTwo.recursiveDeref();

        /* Tear down DDManager */
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAdd(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert addition */
        XDD<Double> five_plus_threePointTwo = five.add(threePointTwo);
        XDD<Double> threePointTwo_plus_five = threePointTwo.add(five);
        XDD<Double> threePointTwo_plus_five_plus_minusTwelve = threePointTwo_plus_five.add(minusTwelve);
        XDD<Double> sevenPointFive_plus_sevenPointFive = sevenPointFive.add(sevenPointFive);
        assertEquals(five_plus_threePointTwo.v(), 8.2, EPS);
        assertEquals(threePointTwo_plus_five.v(), five_plus_threePointTwo.v(), EPS);
        assertEquals(threePointTwo_plus_five_plus_minusTwelve.v(), -3.8, EPS);
        assertEquals(sevenPointFive_plus_sevenPointFive.v(), 15.0, EPS);

        /* Release memory */
        five_plus_threePointTwo.recursiveDeref();
        threePointTwo_plus_five.recursiveDeref();
        threePointTwo_plus_five_plus_minusTwelve.recursiveDeref();
        sevenPointFive_plus_sevenPointFive.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {

        /* Set up DDManager */
        ddManager = new ArithmeticDDManager(addBackend);

        /* Set up come constants */
        five = ddManager.constant(5.0);
        four = ddManager.constant(4.0);
        sevenPointFive = ddManager.constant(7.5);
        minusTwelve = ddManager.constant(-12.0);
        threePointTwo = ddManager.constant(3.2);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAddInverse(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert additive inversion */
        XDD<Double> minus_five = five.addInverse();
        XDD<Double> minus_threePointTwo = threePointTwo.addInverse();
        XDD<Double> minus_minus_threePointTwo = minus_threePointTwo.addInverse();
        XDD<Double> minus_sevenPointFive = sevenPointFive.addInverse();
        assertEquals(minus_five.v(), -5.0, EPS);
        assertEquals(minus_threePointTwo.v(), -3.2, EPS);
        assertEquals(minus_minus_threePointTwo.v(), threePointTwo.v(), EPS);
        assertEquals(minus_sevenPointFive.v(), -7.5, EPS);

        /* Release memory */
        minus_five.recursiveDeref();
        minus_threePointTwo.recursiveDeref();
        minus_minus_threePointTwo.recursiveDeref();
        minus_sevenPointFive.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMult(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert multiplication */
        XDD<Double> five_times_threePointTwo = five.mult(threePointTwo);
        XDD<Double> threePointTwo_times_five = threePointTwo.mult(five);
        XDD<Double> threePointTwo_times_five_times_minusTwelve = threePointTwo_times_five.mult(minusTwelve);
        XDD<Double> minusTwelve_times_Twelve = minusTwelve.mult(minusTwelve);
        assertEquals(five_times_threePointTwo.v(), 16.0, EPS);
        assertEquals(threePointTwo_times_five.v(), five_times_threePointTwo.v(), EPS);
        assertEquals(threePointTwo_times_five_times_minusTwelve.v(), -192, EPS);
        assertEquals(minusTwelve_times_Twelve.v(), 144.0, EPS);

        /* Release memory */
        five_times_threePointTwo.recursiveDeref();
        threePointTwo_times_five.recursiveDeref();
        threePointTwo_times_five_times_minusTwelve.recursiveDeref();
        minusTwelve_times_Twelve.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void multInverse(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert multiplicative inversion */
        XDD<Double> inv_five = five.multInverse();
        XDD<Double> inv_threePointTwo = threePointTwo.multInverse();
        XDD<Double> inv_inv_threePointTwo = inv_threePointTwo.multInverse();
        XDD<Double> inv_sevenPointFive = sevenPointFive.multInverse();
        assertEquals(inv_five.v(), 0.2, EPS);
        assertEquals(inv_threePointTwo.v(), 0.3125, EPS);
        assertEquals(inv_inv_threePointTwo.v(), threePointTwo.v(), EPS);
        assertEquals(inv_sevenPointFive.v(), 0.1333333333, EPS);

        /* Release memory */
        inv_five.recursiveDeref();
        inv_threePointTwo.recursiveDeref();
        inv_inv_threePointTwo.recursiveDeref();
        inv_sevenPointFive.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void inf(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert infimum */
        XDD<Double> five_inf_threePointTwo = five.inf(threePointTwo);
        XDD<Double> threePointTwo_inf_five = threePointTwo.inf(five);
        XDD<Double> threePointTwo_inf_five_minusTwelve = threePointTwo_inf_five.inf(minusTwelve);
        XDD<Double> sevenPointFive_inf_sevenPointFive = sevenPointFive.inf(sevenPointFive);
        assertEquals(five_inf_threePointTwo.v(), 3.2, EPS);
        assertEquals(threePointTwo_inf_five.v(), five_inf_threePointTwo.v(), EPS);
        assertEquals(threePointTwo_inf_five_minusTwelve.v(), -12.0, EPS);
        assertEquals(sevenPointFive_inf_sevenPointFive.v(), 7.5, EPS);

        /* Release memory */
        five_inf_threePointTwo.recursiveDeref();
        threePointTwo_inf_five.recursiveDeref();
        threePointTwo_inf_five_minusTwelve.recursiveDeref();
        sevenPointFive_inf_sevenPointFive.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void sup(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert supremum */
        XDD<Double> five_sup_threePointTwo = five.sup(threePointTwo);
        XDD<Double> threePointTwo_sup_five = threePointTwo.sup(five);
        XDD<Double> threePointTwo_sup_five_sup_minusTwelve = threePointTwo_sup_five.sup(minusTwelve);
        XDD<Double> sevenPointFive_sup_sevenPointFive = sevenPointFive.sup(sevenPointFive);
        assertEquals(five_sup_threePointTwo.v(), 5.0, EPS);
        assertEquals(threePointTwo_sup_five.v(), five_sup_threePointTwo.v(), EPS);
        assertEquals(threePointTwo_sup_five_sup_minusTwelve.v(), 5.0, EPS);
        assertEquals(sevenPointFive_sup_sevenPointFive.v(), 7.5, EPS);

        /* Release memory */
        five_sup_threePointTwo.recursiveDeref();
        threePointTwo_sup_five.recursiveDeref();
        threePointTwo_sup_five_sup_minusTwelve.recursiveDeref();
        sevenPointFive_sup_sevenPointFive.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get some constants */
        XDD<Double> a = ddManager.constant(5.0);
        XDD<Double> b = ddManager.constant(7.3);

        /* Reconstruct and assert equality */
        XDD<Double> reconstructed_a = ddManager.constant(ddManager.parseElement(a.toString()));
        XDD<Double> reconstructed_b = ddManager.constant(ddManager.parseElement(b.toString()));
        assertEquals(reconstructed_a, a);
        assertEquals(reconstructed_b, b);

        /* Release memory */
        a.recursiveDeref();
        b.recursiveDeref();
        reconstructed_a.recursiveDeref();
        reconstructed_b.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicTransform(ADDBackend addBackend) {
        setUp(addBackend);

        /* Build test DD */
        XDD<Double> var0 = ddManager.namedVar("hey");
        XDD<Double> var1 = ddManager.namedVar("ho");
        XDD<Double> sum = var0.add(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Transform test DD */
        StringMonoidDDManager ddManager2 = new StringMonoidDDManager(addBackend);
        XDD<String> sumStr = sum.monadicTransform(ddManager2, x -> x > 0 ? "> zero" : "<= zero");

        /* Assert terminals */
        assertEquals(sumStr.t().v(), "> zero");
        assertEquals(sumStr.e().t().v(), "> zero");
        assertEquals(sumStr.e().e().v(), "<= zero");

        /* Assert variable names and indices */
        assertEquals(sumStr.readIndex(), 0);
        assertEquals(sumStr.readName(), "hey");
        assertEquals(sumStr.e().readIndex(), 1);
        assertEquals(sumStr.e().readName(), "ho");
        sum.recursiveDeref();
        sumStr.recursiveDeref();

        /* Release memory */
        assertRefCountZeroAndQuit(ddManager2);
    }
}
