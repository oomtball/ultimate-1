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

import java.awt.Color;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ColorDDManagerTest extends DDManagerTest {

    private static final Color SOME_RED = new Color(230, 100, 0);
    private static final Color SOME_GREEN = new Color(30, 170, 125);
    private static final Color SOME_LIGHT_GREEN = new Color(150, 240, 60);
    private static final Color SOME_GRAY = new Color(222, 222, 222);

    private ColorDDManager ddManager;

    @AfterClass
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testZeroElement(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert zero element */
        assertEquals(ddManager.zeroElement(), Color.BLACK);

        /* Assert zero element DD */
        XDD<Color> zero = ddManager.zero();
        assertEquals(zero.v(), Color.BLACK);
        zero.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new ColorDDManager(addBackend);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testOneElement(ADDBackend addBackend) {
        setUp(addBackend);

        /* Assert one element */
        assertEquals(ddManager.oneElement(), Color.WHITE);

        /* Assert one element DD */
        XDD<Color> one = ddManager.one();
        assertEquals(one.v(), Color.WHITE);
        one.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMult(ADDBackend addBackend) {
        setUp(addBackend);

        /* Expected products */
        Color expected_ab = new Color(27, 66, 0);
        Color expected_ac = new Color(135, 94, 0);
        Color expected_bc = new Color(17, 160, 29);

        /* Actual products */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);
        XDD<Color> c = ddManager.constant(SOME_LIGHT_GREEN);
        XDD<Color> actual_ab = a.mult(b);
        XDD<Color> actual_ac = a.mult(c);
        XDD<Color> actual_bc = b.mult(c);
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();

        /* Assert products */
        assertEquals(actual_ab.v(), expected_ab);
        assertEquals(actual_ac.v(), expected_ac);
        assertEquals(actual_bc.v(), expected_bc);
        actual_ab.recursiveDeref();
        actual_ac.recursiveDeref();
        actual_bc.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAdd(ADDBackend addBackend) {
        setUp(addBackend);

        /* Expected products */
        Color expected_ab = new Color(255, 255, 125);
        Color expected_ac = new Color(255, 255, 60);
        Color expected_bc = new Color(180, 255, 185);

        /* Actual products */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);
        XDD<Color> c = ddManager.constant(SOME_LIGHT_GREEN);
        XDD<Color> actual_ab = a.add(b);
        XDD<Color> actual_ac = a.add(c);
        XDD<Color> actual_bc = b.add(c);
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();

        /* Assert products */
        assertEquals(actual_ab.v(), expected_ab);
        assertEquals(actual_ac.v(), expected_ac);
        assertEquals(actual_bc.v(), expected_bc);
        actual_ab.recursiveDeref();
        actual_ac.recursiveDeref();
        actual_bc.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAddCommutativity(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get some constant DDs */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);
        XDD<Color> c = ddManager.constant(SOME_LIGHT_GREEN);

        /* Assert commutativity */
        XDD<Color> ab = a.add(b);
        XDD<Color> ba = b.add(a);
        assertEquals(ba, ab);
        XDD<Color> bc = b.add(c);
        XDD<Color> cb = c.add(b);
        assertEquals(cb, bc);

        /* Dereference DDs */
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
        ab.recursiveDeref();
        ba.recursiveDeref();
        bc.recursiveDeref();
        cb.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMultCommutativity(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get some constant DDs */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);
        XDD<Color> c = ddManager.constant(SOME_LIGHT_GREEN);

        /* Assert commutativity */
        XDD<Color> ab = a.mult(b);
        XDD<Color> ba = b.mult(a);
        assertEquals(ba, ab);
        XDD<Color> bc = b.mult(c);
        XDD<Color> cb = c.mult(b);
        assertEquals(cb, bc);

        /* Dereference DDs */
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
        ab.recursiveDeref();
        ba.recursiveDeref();
        bc.recursiveDeref();
        cb.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMultAssociativity(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get some constant DDs */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);
        XDD<Color> c = ddManager.constant(SOME_LIGHT_GREEN);

        /* Assert associativity */
        XDD<Color> ab = a.mult(b);
        XDD<Color> ab_c = ab.mult(c);
        XDD<Color> bc = b.mult(c);
        XDD<Color> a_bc = a.mult(bc);
        assertEquals(a_bc, ab_c);


        /* Dereference DDs */
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
        ab.recursiveDeref();
        ab_c.recursiveDeref();
        bc.recursiveDeref();
        a_bc.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testPlusAssociativity(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get some constant DDs */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);
        XDD<Color> c = ddManager.constant(SOME_LIGHT_GREEN);

        /* Assert associativity */
        XDD<Color> ab = a.add(b);
        XDD<Color> ab_c = ab.add(c);
        XDD<Color> bc = b.add(c);
        XDD<Color> a_bc = a.add(bc);
        assertEquals(a_bc, ab_c);


        /* Dereference DDs */
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
        ab.recursiveDeref();
        ab_c.recursiveDeref();
        bc.recursiveDeref();
        a_bc.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testGrayscaleOperator(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test operator on some gray */
        Color expectedGrayscale = SOME_GRAY;
        Color actualGrayscale = ColorDDManager.grayscale(SOME_GRAY);
        assertEquals(actualGrayscale, expectedGrayscale);

        /* Test operator on some colours and expect it to be some form of gray */
        assertGray(ColorDDManager.grayscale(SOME_RED));
        assertGray(ColorDDManager.grayscale(SOME_GREEN));
        assertGray(ColorDDManager.grayscale(SOME_LIGHT_GREEN));
        assertGray(ColorDDManager.grayscale(SOME_GRAY));
    }

    private void assertGray(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        assertTrue(r == g && g == b);
        assertTrue(0 < r && r < 255);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAvg(ADDBackend addBackend) {
        setUp(addBackend);

        Color expectedYellow = new Color(130, 135, 62);
        Color actualYellow = ColorDDManager.avg(SOME_RED, SOME_GREEN);
        assertEquals(actualYellow, expectedYellow);
        Color expectedLightYellow = new Color(190, 170, 30);
        Color actualLightYellow = ColorDDManager.avg(SOME_RED, SOME_LIGHT_GREEN);
        assertEquals(actualLightYellow, expectedLightYellow);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAvgAsOperator(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get constant DDs */
        XDD<Color> a = ddManager.constant(SOME_RED);
        XDD<Color> b = ddManager.constant(SOME_GREEN);

        /* Assert avg operator */
        Color expectedYellow = new Color(130, 135, 62);
        XDD<Color> c = a.apply(ColorDDManager::avg, b);
        Color actualYellow = c.v();
        assertEquals(actualYellow, expectedYellow);

        /* Dereference DDs */
        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
    }
}