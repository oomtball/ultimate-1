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
package info.scce.addlib.dd.zdd;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import info.scce.addlib.backend.ZDDBackend;
import info.scce.addlib.dd.DDManagerException;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.traverser.PreorderTraverser;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class ZDDManagerTest extends DDManagerTest {

    private final ZDDBackend zddBackend;
    private ZDDManager ddManager;

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultZDDBackends")
    public ZDDManagerTest(ZDDBackend zddBackend) {
        this.zddBackend = zddBackend;
    }

    @BeforeMethod
    public void setUp() {
        ddManager = new ZDDManager(zddBackend);
    }

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test
    public void testSimple() {
        /* Get constants */
        ZDD zero = ddManager.readZero();
        ZDD one = ddManager.readZddOne(0);

        /* Test change */
        ZDD c = one.change(0);
        ZDD c_t = c.t();
        assertEquals(c_t, one);
        ZDD c_e = c.e();
        assertEquals(c_e, zero);

        /* Test change */
        ZDD d = one.change(1);
        ZDD d_t = d.t();
        assertEquals(d_t, one);
        ZDD d_e = d.e();
        assertEquals(d_e, zero);

        /* Test union */
        ZDD e = c.union(d);
        ZDD e_t = e.t();
        assertEquals(e_t, one);
        ZDD e_e = e.e();
        ZDD e_e_t = e_e.t();
        assertEquals(e_e_t, one);
        ZDD e_e_e = e_e.e();
        assertEquals(e_e_e, zero);

        /* Test union */
        ZDD f = one.union(e);
        ZDD f_t = f.t();
        assertEquals(f_t, one);
        ZDD f_e = f.e();
        ZDD f_e_t = f_e.t();
        assertEquals(f_e_t, one);
        ZDD f_e_e = f_e.e();
        assertEquals(f_e_e, one);

        /* Test diff */
        ZDD g = f.diff(c);
        ZDD g_t = g.t();
        assertEquals(g_t, one);
        ZDD g_e = g.e();
        assertEquals(g_e, one);

        /* Release memory */
        zero.recursiveDeref();
        one.recursiveDeref();
        c.recursiveDeref();
        d.recursiveDeref();
        e.recursiveDeref();
        f.recursiveDeref();
        g.recursiveDeref();
    }

    @Test
    public void testZDDEval() {
        ZDD zdd = ddManager.ithVar(0);
        boolean evalZDD = zdd.eval(true).v();
        assertTrue(evalZDD);
        zdd.recursiveDeref();
    }

    @Test
    public void testValidZDDReordering() {

        /* Load all the supported ZDD reordering heuristics */
        DDReorderingType[] supportedDDReorderingTypesArr = {DDReorderingType.SAME,
                                                            DDReorderingType.NONE,
                                                            DDReorderingType.RANDOM,
                                                            DDReorderingType.RANDOM_PIVOT,
                                                            DDReorderingType.SIFT,
                                                            DDReorderingType.SIFT_CONVERGE,
                                                            DDReorderingType.SYMM_SIFT,
                                                            DDReorderingType.SYMM_SIFT_CONV,
                                                            DDReorderingType.GROUP_SIFT,
                                                            DDReorderingType.GROUP_SIFT_CONV,
                                                            DDReorderingType.LINEAR,
                                                            DDReorderingType.LINEAR_CONVERGE};
        List<DDReorderingType> supportedDDReorderingTypes = Arrays.asList(supportedDDReorderingTypesArr);

        /* Load ZDD that can be reordered */
        ZDD subject = getReorderableZDD();

        /* Assert that all of the unimplemented reordering heuristics throw an exception */
        for (DDReorderingType ddReorderingType : DDReorderingType.values()) {
            try {
                ddManager.reduceHeap(ddReorderingType, 0);
                assertTrue(supportedDDReorderingTypes.contains(ddReorderingType));
            } catch (DDManagerException ddManagerException) {
                assertEquals(ddManagerException.getMessage(), "Unsupported reordering heuristic for ZDD");
                assertFalse(supportedDDReorderingTypes.contains(ddReorderingType));
            }
        }

        subject.recursiveDeref();
    }

    private ZDD getReorderableZDD() {

        /* Get some variables */
        ZDD var0 = ddManager.namedVar("var0");
        ZDD var1 = ddManager.namedVar("var1");
        ZDD var2 = ddManager.namedVar("var2");
        ZDD var3 = ddManager.namedVar("var3");

        /* Build ZDD */
        ZDD zdd1 = var0.union(var1);
        ZDD zdd2 = zdd1.diff(var2);
        ZDD result = zdd2.union(var3);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var3.recursiveDeref();
        zdd1.recursiveDeref();
        zdd2.recursiveDeref();

        return result;
    }

    @Test
    public void testReorderNone() {

        /* Load ZDD that can be reordered */
        ZDD subject = getReorderableZDD();

        /* Get fingerprint of the order prior to reduceHeap */
        List<ZDD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));

        /* Assert that DDReorderingType.NONE does not change the order */
        boolean returnCode = ddManager.reduceHeap(DDReorderingType.NONE, 0);
        List<ZDD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));
        assertEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        assertTrue(returnCode);
        subject.recursiveDeref();
    }

    @Test
    public void testReorderSift() {

        /* Load ZDD that can be reordered */
        ZDD subject = getReorderableZDD();

        /* Assert that the ZDD size changes after reordering */
        long sizeBefore = subject.dagSize();
        ddManager.reduceHeap(DDReorderingType.SIFT_CONVERGE, 0);
        long sizeAfter = subject.dagSize();
        assertNotEquals(sizeAfter, sizeBefore);
        subject.recursiveDeref();
    }

    @Test
    public void testReorderWithEnforcedOrder() {

        /* Load ZDD that can be reordered */
        ZDD subject = getReorderableZDD();

        /* Reorder ZDD */
        int[] permutation = {1, 3, 2, 0};
        ddManager.setVariableOrder(permutation);

        /* Assert that at least one path has the correct variables order, thus all paths as well */
        assertEquals(subject.readName(), "var1");
        assertEquals(subject.e().readName(), "var3");
        assertEquals(subject.e().t().readName(), "var2");
        assertEquals(subject.e().t().t().readName(), "var0");

        /* Assert that the correct order is enforced */
        assertEquals(ddManager.readPerm(1), 0);
        assertEquals(ddManager.readPerm(3), 1);
        assertEquals(ddManager.readPerm(2), 2);
        assertEquals(ddManager.readPerm(0), 3);

        subject.recursiveDeref();
    }

    @Test
    public void testAutomaticReordering() {

        /* Load ZDD that can be reordered */
        ZDD subject = getReorderableZDD();
        long sizeBefore = subject.dagSize();

        /* Enable automatic reordering */
        ddManager.setNextReordering(1);
        ddManager.enableAutomaticReordering(DDReorderingType.SIFT_CONVERGE);

        /* Trigger automatic reordering */
        ZDD var4 = ddManager.ithVar(4);
        var4.recursiveDeref();

        /* Assert that automatic reordering with DDReorderingType.SIFT_CONVERGE changes the order */
        long sizeAfter = subject.dagSize();
        assertNotEquals(sizeAfter, sizeBefore);
        subject.recursiveDeref();
    }

    @Test
    public void testDisableAutomaticReordering() {

        /* Load ZDD that can be reordered */
        ZDD subject = getReorderableZDD();

        /* Get fingerprint of the order prior to automatic reordering */
        List<ZDD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));

        /* Enable automatic reordering */
        ddManager.setNextReordering(1);
        ddManager.enableAutomaticReordering(DDReorderingType.SIFT_CONVERGE);
        ddManager.disableAutomaticReordering();

        /* Trigger automatic reordering */
        ZDD var4 = ddManager.ithVar(4);
        var4.recursiveDeref();

        /* Assert that automatic reordering with DDReorderingType.SIFT_CONVERGE changes the order */
        List<ZDD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));
        assertEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        subject.recursiveDeref();
    }
}
