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
package info.scce.addlib.dd.bdd;

import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.dd.DDManagerException;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanLogicDDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class BDDManagerTest extends DDManagerTest {

    private BDDManager ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testInterManagerOperations(BDDBackend backend) {
        ddManager = new BDDManager(backend);
        /* Get another BDDManager */
        BDDManager other_ddManager = new BDDManager(backend);

        /* Get operands */
        BDD var0 = ddManager.ithVar(0);
        BDD other_var0 = other_ddManager.ithVar(0);

        /* Try forbidden operation between different BDDManagers */
        try {
            var0.or(other_var0);
            fail("Expected exception was not thrown");
        } catch (DDManagerException e) {
            String message = e.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(DDManager.class.getSimpleName()));
            assertTrue(message.contains(BDD.class.getSimpleName()));
            assertTrue(message.contains("must share the same"));
        }
        var0.recursiveDeref();
        other_var0.recursiveDeref();

        /* Quit local BDDManager separately */
        assertRefCountZeroAndQuit(other_ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testEval(BDDBackend backend) {
        ddManager = new BDDManager(backend);
        /* Get variables */
        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);

        /* Get some constants */
        BDD one = ddManager.readOne();
        BDD zero = ddManager.readLogicZero();

        /* Build disjunction: var0 | var1 */
        BDD var0_or_var1 = var0.or(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Get terminal nodes */
        BDD t00 = var0_or_var1.e().e();
        BDD t01 = var0_or_var1.e().t();
        BDD t1 = var0_or_var1.t();

        /* Assert terminal nodes */
        assertEquals(t00, zero);
        assertEquals(t01, one);
        assertEquals(t1, one);
        var0_or_var1.recursiveDeref();
        one.recursiveDeref();
        zero.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testOrSimilarToDocumentation(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get the variables */
        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);

        /* Build the disjunction */
        BDD disjunction = var0.or(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Evaluate disjunction for assignment var0 := 1, var1 := 0 */
        BDD terminal = disjunction.eval(true, false);

        /* See if the terminal is what we expect it to be */
        BDD one = ddManager.readOne();
        assertEquals(terminal, one);

        /* Release memory */
        disjunction.recursiveDeref();
        one.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testOrAsInDocumentation(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get the variables */
        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);

        /* Build the disjunction */
        BDD disjunction = var0.or(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Evaluate disjunction for assignment var0 := 1, var1 := 0 */
        BDD terminal = disjunction.eval(true, false);

        /* See if the terminal is what we expect it to be */
        BDD one = ddManager.readOne();
        assertEquals(terminal, one);

        /* Release memory */
        disjunction.recursiveDeref();
        one.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testToXDD(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get constants and variables */
        BDD one = ddManager.readOne();
        BDD var0 = ddManager.namedVar("a");
        BDD var1 = ddManager.namedVar("b");

        /* Build the implication */
        BDD not_var0 = var0.not();
        var0.recursiveDeref();
        BDD var0_impl_var1 = not_var0.or(var1);
        not_var0.recursiveDeref();
        var1.recursiveDeref();

        /* Convert to XDD */
        BDD expected = var0_impl_var1;
        XDD<Boolean> actual = var0_impl_var1.toXDD(new BooleanLogicDDManager(BackendProvider.getCuddADDBackend()));

        /* Assert equality */
        assertEquals(actual.eval(true, true).v().booleanValue(), expected.eval(true, true).equals(one));
        assertEquals(actual.eval(true, false).v().booleanValue(), expected.eval(true, false).equals(one));
        assertEquals(actual.eval(false, true).v().booleanValue(), expected.eval(false, true).equals(one));
        assertEquals(actual.eval(false, false).v().booleanValue(), expected.eval(false, false).equals(one));

        /* Release memory */
        one.recursiveDeref();
        expected.recursiveDeref();
        actual.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testToXDD2(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get constants and variables */
        BDD one = ddManager.readOne();
        BDD var0 = ddManager.namedVar("a");
        BDD var1 = ddManager.namedVar("b");

        /* not a and b */
        BDD not_var0 = var0.not();
        var0.recursiveDeref();
        BDD var0_impl_var1 = not_var0.and(var1);
        not_var0.recursiveDeref();
        var1.recursiveDeref();

        /* Convert to XDD */
        BDD expected = var0_impl_var1;
        XDD<Boolean> actual = var0_impl_var1.toXDD(new BooleanLogicDDManager(BackendProvider.getCuddADDBackend()));


        /* Assert equality */
        assertEquals(actual.eval(true, true).v().booleanValue(), expected.eval(true, true).equals(one));
        assertEquals(actual.eval(true, false).v().booleanValue(), expected.eval(true, false).equals(one));
        assertEquals(actual.eval(false, true).v().booleanValue(), expected.eval(false, true).equals(one));
        assertEquals(actual.eval(false, false).v().booleanValue(), expected.eval(false, false).equals(one));

        /* Release memory */
        one.recursiveDeref();
        expected.recursiveDeref();
        actual.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testToXDDExhaustive(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);
        BDD var2 = ddManager.ithVar(2);
        BDD var3 = ddManager.ithVar(3);
        BDD var4 = ddManager.ithVar(4);
        BDD not_var0 = var0.not();
        BDD not_var1 = var1.not();
        BDD not_var4 = var4.not();
        BDD not_var0_or_var1 = not_var0.or(var1);
        BDD not_var0_or_var1_or_var2 = not_var0_or_var1.or(var2);
        BDD var0_or_var3 = var0.or(var3);
        BDD not_var3 = var3.not();
        BDD not_var3_or_not_var4 = not_var3.or(not_var4);
        BDD not_var1_or_not_var6 = not_var1.or(not_var0);
        BDD not_var1_or_not_var6_or_var3 = not_var1_or_not_var6.or(var3);
        BDD not_var0_or_var1_or_var2_and_not_var3_or_not_var4 = not_var0_or_var1_or_var2.and(not_var3_or_not_var4);
        BDD bdd = not_var1_or_not_var6_or_var3.and(not_var0_or_var1_or_var2_and_not_var3_or_not_var4);

        BooleanLogicDDManager booleanLogicDDManager = new BooleanLogicDDManager(BackendProvider.getCuddADDBackend());
        XDD<Boolean> xdd = bdd.toXDD(booleanLogicDDManager);
        BDD bddOne = ddManager.readOne();
        XDD<Boolean> xddOne = booleanLogicDDManager.one();
        for (int i = 0; i < 32; i++) {
            String binaryString = String.format("%05d", Integer.parseInt(Integer.toBinaryString(i)));
            boolean a = binaryString.charAt(0) == '1';
            boolean b = binaryString.charAt(1) == '1';
            boolean c = binaryString.charAt(2) == '1';
            boolean d = binaryString.charAt(3) == '1';
            boolean e = binaryString.charAt(4) == '1';
            boolean bddLeaf = bdd.eval(a, b, c, d, e).equals(bddOne);
            boolean xddLeaf = xdd.eval(a, b, c, d, e).equals(xddOne);
            assertEquals(xddLeaf, bddLeaf);
        }

        bddOne.recursiveDeref();
        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var3.recursiveDeref();
        var4.recursiveDeref();
        not_var0.recursiveDeref();
        not_var1.recursiveDeref();
        not_var4.recursiveDeref();
        not_var0_or_var1.recursiveDeref();
        not_var0_or_var1_or_var2.recursiveDeref();
        var0_or_var3.recursiveDeref();
        not_var3.recursiveDeref();
        not_var3_or_not_var4.recursiveDeref();
        not_var1_or_not_var6.recursiveDeref();
        not_var1_or_not_var6_or_var3.recursiveDeref();
        not_var0_or_var1_or_var2_and_not_var3_or_not_var4.recursiveDeref();
        bdd.recursiveDeref();
        xdd.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testSecureDeref(BDDBackend backend) {
        ddManager = new BDDManager(backend);
        assertEquals(ddManager.checkZeroRef(), 0);
        BDD A = ddManager.ithVar(0);
        A.recursiveDeref();
        try {
            A.recursiveDeref();
            fail("ref count should have been zero");
        } catch (DDManagerException e) {
            assertEquals(e.getMessage(), "Cannot dereference unreferenced DD");
        }
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testCompose(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get constants and variables */
        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);
        BDD one = ddManager.readOne();
        BDD zero = ddManager.readLogicZero();

        /* Build BDDs via composition */
        BDD not_var0 = var0.not();
        BDD not_var1 = var1.not();

        BDD composed_var1 = not_var0.compose(var1, 0);
        BDD composed_not_var1 = not_var0.compose(not_var1, 0);

        /* Assert that composition with negated vars returns the correct result */
        assertEquals(composed_var1.readIndex(), 1);
        assertEquals(composed_var1.eval(true, true), zero);
        assertEquals(composed_var1.eval(true, false), one);

        assertEquals(composed_not_var1.readIndex(), 1);
        assertEquals(composed_not_var1.eval(true, true), one);
        assertEquals(composed_not_var1.eval(true, false), zero);

        /* Release memory */
        var0.recursiveDeref();
        var1.recursiveDeref();
        one.recursiveDeref();
        zero.recursiveDeref();
        not_var0.recursiveDeref();
        not_var1.recursiveDeref();
        composed_var1.recursiveDeref();
        composed_not_var1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testVectorCompose(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get constants and variables */
        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);
        BDD var2 = ddManager.ithVar(2);
        BDD var3 = ddManager.ithVar(3);
        BDD one = ddManager.readOne();
        BDD zero = ddManager.readLogicZero();

        /* Build a composed BDD with var2 and var3 */
        BDD var0_and_var1 = var0.and(var1);
        BDD[] vector = {var3, var2, one, one};
        BDD var2_and_var3 = var0_and_var1.vectorCompose(vector);

        /* Assert that the variables changed and the structure is the same */
        assertEquals(var2_and_var3.readIndex(), 2);
        assertEquals(var2_and_var3.t().readIndex(), 3);
        assertEquals(var2_and_var3.e(), zero);
        assertEquals(var2_and_var3.t().t(), one);
        assertEquals(var2_and_var3.t().e(), zero);

        /* Release memory */
        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var3.recursiveDeref();
        one.recursiveDeref();
        zero.recursiveDeref();
        var0_and_var1.recursiveDeref();
        var2_and_var3.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testDagSize(BDDBackend backend) {
        ddManager = new BDDManager(backend);

        /* Get constants and variables */
        BDD var0 = ddManager.ithVar(0);
        BDD var1 = ddManager.ithVar(1);
        BDD var0_and_var1 = var0.and(var1);

        assertEquals(var0.dagSize(), 2);
        assertEquals(var0_and_var1.dagSize(), 3);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var0_and_var1.recursiveDeref();
    }
}