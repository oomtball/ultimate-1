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
package info.scce.addlib.dd.add;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.google.common.collect.Lists;
import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.dd.DDManagerException;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.ringlikedd.example.ArithmeticDDManager;
import info.scce.addlib.traverser.PreorderTraverser;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@SuppressWarnings("PMD.TooManyStaticImports")
public class ADDManagerTest extends DDManagerTest {

    private ADDManager ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNamedVar(ADDBackend backend) {
        ddManager = new ADDManager(backend);
        String validName = "abcABC09_";
        ADD f = ddManager.namedVar(validName);
        f.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testInterManagerOperations(ADDBackend backend) {
        ddManager = new ADDManager(backend);
        /* Get another ADDManager */
        ADDManager other_ddManager = new ADDManager(backend);

        /* Get operands */
        ADD var0 = ddManager.ithVar(0);
        ADD other_var0 = other_ddManager.ithVar(0);

        /* Try forbidden operation between different ADDManagers */
        try {
            var0.times(other_var0);
            fail("Expected exception was not thrown");
        } catch (DDManagerException e) {
            String message = e.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(DDManager.class.getSimpleName()));
            assertTrue(message.contains(ADD.class.getSimpleName()));
            assertTrue(message.contains("must share the same"));
        }
        var0.recursiveDeref();
        other_var0.recursiveDeref();

        /* Quit local ADDManager separately */
        assertRefCountZeroAndQuit(other_ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApply(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);

        /* Get some constants */
        ADD const1 = ddManager.readOne();
        ADD const10 = ddManager.constant(10);

        /* Build var0 + var1 */
        ADD var0_plus_var1 = var0.plus(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Build const1 + const10 */
        ADD const1_plus_const10 = const1.plus(const10);
        const1.recursiveDeref();
        const10.recursiveDeref();

        /* Build (var0 + var1 + Build const1 + const10) % 7 */
        BinaryOperator<Double> sumMod7 = (left, right) -> (left + right) % 7;
        ADD var0_plus_var1_plus_const1_plus_const10_mod7 = var0_plus_var1.apply(sumMod7, const1_plus_const10);
        var0_plus_var1.recursiveDeref();
        const1_plus_const10.recursiveDeref();

        /* Get terminal nodes */
        ADD t00 = var0_plus_var1_plus_const1_plus_const10_mod7.e().e();
        ADD t01 = var0_plus_var1_plus_const1_plus_const10_mod7.e().t();
        ADD t10 = var0_plus_var1_plus_const1_plus_const10_mod7.t().e();
        ADD t11 = var0_plus_var1_plus_const1_plus_const10_mod7.t().t();

        /* Assert terminal nodes */
        assertEquals(t00.v(), (11.0 + 0.0) % 7, 0.0);
        assertEquals(t01.v(), (11.0 + 1.0) % 7, 0.0);
        assertEquals(t10.v(), (11.0 + 1.0) % 7, 0.0);
        assertEquals(t11.v(), (11.0 + 2.0) % 7, 0.0);
        var0_plus_var1_plus_const1_plus_const10_mod7.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicApply(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some constants */
        ADD const1 = ddManager.readOne();
        ADD const10 = ddManager.constant(10);

        /* Build const1 + const10 + 5 */
        ADD const1_plus_const10 = const1.plus(const10);
        const1.recursiveDeref();
        const10.recursiveDeref();
        ADD const1_plus_const10_plus_5 = const1_plus_const10.monadicApply(x -> x + 5);

        const1_plus_const10.recursiveDeref();
        /* Assert value */
        double expectedValue = 16.0;
        double actualValue = const1_plus_const10_plus_5.v();
        assertEquals(actualValue, expectedValue, 0.0);
        const1_plus_const10_plus_5.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testEvalADD(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables and constants */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD zero = ddManager.readZero();
        ADD one = ddManager.readOne();
        ADD two = ddManager.constant(2);

        /* Build var0 + var1 */
        ADD var0_plus_var1 = var0.plus(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Get terminal nodes */
        ADD var0_plus_var1_ass00 = var0_plus_var1.eval(false, false);
        ADD var0_plus_var1_ass01 = var0_plus_var1.eval(false, true);
        ADD var0_plus_var1_ass10 = var0_plus_var1.eval(true, false);
        ADD var0_plus_var1_ass11 = var0_plus_var1.eval(true, true);

        /* Assert terminal nodes */
        assertEquals(var0_plus_var1_ass00, zero);
        assertEquals(var0_plus_var1_ass01, one);
        assertEquals(var0_plus_var1_ass10, one);
        assertEquals(var0_plus_var1_ass11, two);
        zero.recursiveDeref();
        one.recursiveDeref();
        two.recursiveDeref();
        var0_plus_var1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicApplyWithManyDifferentCallbacks(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);

        /* Build sum var0 + var1 */
        ADD var0_plus_var1 = var0.plus(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Add values 2, 3, ..., 12 one after the other */
        ADD var0_plus_var1_plus_2 = var0_plus_var1.monadicApply(t -> t + 2);
        var0_plus_var1.recursiveDeref();
        ADD var0_plus_var1_plus_5 = var0_plus_var1_plus_2.monadicApply(t -> t + 3);
        var0_plus_var1_plus_2.recursiveDeref();
        ADD var0_plus_var1_plus_9 = var0_plus_var1_plus_5.monadicApply(t -> t + 4);
        var0_plus_var1_plus_5.recursiveDeref();
        ADD var0_plus_var1_plus_14 = var0_plus_var1_plus_9.monadicApply(t -> t + 5);
        var0_plus_var1_plus_9.recursiveDeref();
        ADD var0_plus_var1_plus_20 = var0_plus_var1_plus_14.monadicApply(t -> t + 6);
        var0_plus_var1_plus_14.recursiveDeref();
        ADD var0_plus_var1_plus_27 = var0_plus_var1_plus_20.monadicApply(t -> t + 7);
        var0_plus_var1_plus_20.recursiveDeref();
        ADD var0_plus_var1_plus_35 = var0_plus_var1_plus_27.monadicApply(t -> t + 8);
        var0_plus_var1_plus_27.recursiveDeref();
        ADD var0_plus_var1_plus_44 = var0_plus_var1_plus_35.monadicApply(t -> t + 9);
        var0_plus_var1_plus_35.recursiveDeref();
        ADD var0_plus_var1_plus_54 = var0_plus_var1_plus_44.monadicApply(t -> t + 10);
        var0_plus_var1_plus_44.recursiveDeref();
        ADD var0_plus_var1_plus_65 = var0_plus_var1_plus_54.monadicApply(t -> t + 11);
        var0_plus_var1_plus_54.recursiveDeref();
        ADD var0_plus_var1_plus_77 = var0_plus_var1_plus_65.monadicApply(t -> t + 12);
        var0_plus_var1_plus_65.recursiveDeref();

        /* Assert terminal nodes */
        assertEquals(var0_plus_var1_plus_77.eval(false, false).v(), 77.0, 0.0);
        assertEquals(var0_plus_var1_plus_77.eval(false, true).v(), 78.0, 0.0);
        assertEquals(var0_plus_var1_plus_77.eval(true, false).v(), 78.0, 0.0);
        assertEquals(var0_plus_var1_plus_77.eval(true, true).v(), 79.0, 0.0);
        var0_plus_var1_plus_77.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApplyWithManyDifferentCallbacks(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        /* Multiplication */
        ADD expected_mult = var0.times(var1);
        ADD actual_mult = var0.apply((t, u) -> t * u, var1);
        assertEquals(actual_mult, expected_mult);
        actual_mult.recursiveDeref();
        expected_mult.recursiveDeref();

        /* Addition */
        ADD expected_plus = var0.plus(var1);
        ADD actual_plus = var0.apply(Double::sum, var1);
        assertEquals(actual_plus, expected_plus);
        actual_plus.recursiveDeref();
        expected_plus.recursiveDeref();

        /* Substraction */
        ADD expected_minus = var0.minus(var1);
        ADD actual_minus = var0.apply((t, u) -> t - u, var1);
        assertEquals(actual_minus, expected_minus);
        actual_minus.recursiveDeref();
        expected_minus.recursiveDeref();

        /* Minimum */
        ADD expected_minimum = var0.minimum(var1);
        ADD actual_minimum = var0.apply(Double::min, var1);
        assertEquals(actual_minimum, expected_minimum);
        actual_minimum.recursiveDeref();
        expected_minimum.recursiveDeref();

        /* Maximum */
        ADD expected_maximum = var0.maximum(var1);
        ADD actual_maximum = var0.apply(Double::max, var1);
        assertEquals(actual_maximum, expected_maximum);
        actual_maximum.recursiveDeref();
        expected_maximum.recursiveDeref();

        /* Disjunction */
        ADD expected_or = var0.or(var1);
        ADD actual_or = var0.apply((t, u) -> t.equals(0.0) && u.equals(0.0) ? 0.0 : 1.0, var1);
        assertEquals(actual_or, expected_or);
        actual_or.recursiveDeref();
        expected_or.recursiveDeref();

        /* Negated conjunction */
        ADD expected_nand = var0.nand(var1);
        ADD actual_nand = var0.apply((t, u) -> 1 - (t * u), var1);
        assertEquals(actual_nand, expected_nand);
        actual_nand.recursiveDeref();
        expected_nand.recursiveDeref();

        /* Negated disjunction */
        ADD expected_nor = var0.nor(var1);
        ADD actual_nor = var0.apply((t, u) -> t.equals(0.0) && u.equals(0.0) ? 1.0 : 0.0, var1);
        assertEquals(actual_nor, expected_nor);
        actual_nor.recursiveDeref();
        expected_nor.recursiveDeref();

        /* Exclusive disjunction */
        ADD expected_xor = var0.xor(var1);
        ADD actual_xor = var0.apply((t, u) -> t + u == 1.0 ? 1.0 : 0.0, var1);
        assertEquals(actual_xor, expected_xor);
        actual_xor.recursiveDeref();
        expected_xor.recursiveDeref();

        /* Exclusive negated disjunction */
        ADD expected_xnor = var0.xnor(var1);
        ADD actual_xnor = var0.apply((t, u) -> u.equals(t) ? 1.0 : 0.0, var1);
        assertEquals(actual_xnor, expected_xnor);
        actual_xnor.recursiveDeref();
        expected_xnor.recursiveDeref();

        /* Greater than indication */
        ADD expected_oneZeroMaximum = var0.oneZeroMaximum(var1);
        ADD actual_oneZeroMaximum = var0.apply((t, u) -> t > u ? 1.0 : 0.0, var1);
        assertEquals(actual_oneZeroMaximum, expected_oneZeroMaximum);
        actual_oneZeroMaximum.recursiveDeref();
        expected_oneZeroMaximum.recursiveDeref();

        /* And something else */
        ADD actual_modulo_2_plus_right = var0.apply((t, u) -> t % 2 + u, var1);
        assertEquals(0.0, 0, actual_modulo_2_plus_right.eval(false, false).v());
        assertEquals(1.0, 0, actual_modulo_2_plus_right.eval(false, true).v());
        assertEquals(0.0, 0, actual_modulo_2_plus_right.eval(true, false).v());
        assertEquals(1.0, 0, actual_modulo_2_plus_right.eval(true, true).v());
        actual_modulo_2_plus_right.recursiveDeref();
        var0.recursiveDeref();
        var1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testTimesSimilarToDocumentation(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get a constant */
        ADD f = ddManager.constant(5.0);

        /* Multiply constant with some variables */
        for (int i = 3; i >= 0; i--) {

            /* Get the variable */
            ADD var = ddManager.ithVar(i);

            /* Multiply the variable to the product */
            ADD tmp = var.times(f);
            f.recursiveDeref();
            var.recursiveDeref();
            f = tmp;
        }

        /* Assert all paths */
        for (int i = 0; i < 16; i++) {
            boolean[] assignment = {((i >> 3) & 1) == 1, ((i >> 2) & 1) == 1, ((i >> 1) & 1) == 1, (i & 1) == 1};
            double expectedValue = i == 15 ? 5.0 : 0.0;
            ADD terminal = f.eval(assignment);
            double actualValue = terminal.v();
            assertEquals(actualValue, expectedValue, 0.0);
        }

        /* Release memory */
        f.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testTimesAsInDocumentation(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get a constant */
        ADD f = ddManager.constant(5);

        /* Multiply constant with some variables */
        for (int i = 3; i >= 0; i--) {

            /* Get the variable */
            ADD var = ddManager.ithVar(i);

            /* Multiply the variable to the product */
            ADD tmp = var.times(f);
            f.recursiveDeref();
            var.recursiveDeref();
            f = tmp;
        }

        /* Release memory */
        f.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApplySimilarToDocumentation(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);

        /* Build sum8: var0 + var1 + 8 */
        ADD sum8 = var0.apply((a, b) -> a + b + 8, var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Assert values */
        ADD terminal8 = sum8.e().e();
        assertEquals(terminal8.v(), 8.0, 0.0);
        ADD terminal10 = sum8.t().t();
        assertEquals(terminal10.v(), 10.0, 0.0);

        /* Release memory */
        sum8.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApplyAsInDocumentation(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);

        /* Build sum8: var0 + var1 + 8 */
        ADD sum8 = var0.apply((a, b) -> a + b + 8, var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* ... */

        /* Release memory */
        sum8.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testReorderNone(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);

        /* Build exclusive disjunction */
        ADD xor = var0.xor(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Get fingerprint of the order prior to reduceHeap */
        List<ADD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(xor));

        /* Assert that DDReorderingType.NONE does not change the order */
        ddManager.reduceHeap(DDReorderingType.NONE, 0);
        List<ADD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(xor));
        assertEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        xor.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testReorderSame(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Load ADD that can be reordered */
        ADD subject = getReorderableADD();

        /* Get fingerprint of the order prior to reduceHeap */
        List<ADD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));

        /* Assert that DDReorderingType.ANNEALING changes the order */
        ddManager.reduceHeap(DDReorderingType.ANNEALING, 0);
        List<ADD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));
        assertNotEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        subject.recursiveDeref();
    }

    private ADD getReorderableADD() {

        /* Get Some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD var2 = ddManager.ithVar(2);
        ADD var3 = ddManager.ithVar(3);

        /* Build disjunction: var0 | var1 */
        ADD var0_or_var1 = var0.or(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Build conjunction: var2 & var3 */
        ADD var2_and_var3 = var2.times(var3);
        var2.recursiveDeref();
        var3.recursiveDeref();

        /* Build an ADD with a variable order dependant structure: (var0 | var1) & (var2 & var3) */
        ADD subject = var0_or_var1.times(var2_and_var3);
        var0_or_var1.recursiveDeref();
        var2_and_var3.recursiveDeref();
        return subject;
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testReorderRandom(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);

        /* Build exclusive disjunction */
        ADD xor = var0.xor(var1);
        var0.recursiveDeref();
        var1.recursiveDeref();

        /* Just check that nothing breaks */
        int A_FEW_TIMES = 16;
        for (int i = 0; i < A_FEW_TIMES; i++) {
            ddManager.reduceHeap(DDReorderingType.RANDOM, 0);
        }
        xor.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testReorderWithLargerExample(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Sum up the variables from 0 to n */
        int n = 128;
        ADD sum = ddManager.constant(0);
        for (int i = 1; i < n; i++) {
            ADD var = ddManager.ithVar(i);
            ADD tmp = sum.plus(var);
            var.recursiveDeref();
            sum.recursiveDeref();
            sum = tmp;
        }

        /* Just check that nothing breaks */
        int A_FEW_TIMES = 16;
        for (int i = 0; i < A_FEW_TIMES; i++) {
            ddManager.reduceHeap(DDReorderingType.RANDOM, 0);
        }
        sum.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testToXDD(ADDBackend backend) {
        ddManager = new ADDManager(backend);
        /* Get Some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var2 = ddManager.ithVar(2);
        ADD var3 = ddManager.ithVar(3);


        /* Build conjunction: var2 & var3 */
        ADD var2_times_var3 = var2.times(var3);
        var2.recursiveDeref();
        var3.recursiveDeref();

        /* Build an ADD: (var0 | var1) + var2 * var3 */
        ADD add = var0.plus(var2_times_var3);
        var0.recursiveDeref();
        var2_times_var3.recursiveDeref();

        /* Convert to XDD */
        ArithmeticDDManager ddManagerTarget = new ArithmeticDDManager(backend);
        XDD<Double> xdd = add.toXDD(ddManagerTarget);

        /* Assert equality */
        for (int i = 0; i < 16; i++) {
            boolean a = ((i >> 0) & 1) == 1;
            boolean b = ((i >> 1) & 1) == 1;
            boolean c = ((i >> 2) & 1) == 1;
            boolean d = ((i >> 3) & 1) == 1;
            assertEquals(xdd.eval(a, b, c, d).v(), add.eval(a, b, c, d).v(), 0.0);
        }

        /* Assert variable names were preserved */
        assertEquals(xdd.readName(), add.readName());
        assertEquals(xdd.t().readName(), add.t().readName());
        assertEquals(xdd.e().t().readName(), add.e().t().readName());

        /* Release memory */
        add.recursiveDeref();
        xdd.recursiveDeref();
        assertRefCountZeroAndQuit(ddManagerTarget);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testSecureDeref(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD A = ddManager.ithVar(0);
        A.recursiveDeref();
        try {
            A.recursiveDeref();
            fail("ref count should have been zero");
        } catch (DDManagerException e) {
            assertEquals("Cannot dereference unreferenced DD", e.getMessage());
        }
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testReorderEnforced(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get Some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD var2 = ddManager.ithVar(2);

        /* Build var1 + 2*var2 + -var3 */
        ADD var0_plus_2var1 = var0.apply((a, b) -> a + 2 * b, var1);
        ADD result = var0_plus_2var1.minus(var2);
        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var0_plus_2var1.recursiveDeref();

        /* Enforce variables order on ADD */
        int[] permutation = {2, 1, 0};
        ddManager.setVariableOrder(permutation);

        /* Assert that one path has the correct variables order, thus all paths as well */
        assertEquals(result.readName(), "x2");
        assertEquals(result.t().readName(), "x1");
        assertEquals(result.t().t().readName(), "x0");

        /* Assert that the variables order is correctly permuted. */
        assertEquals(ddManager.readPerm(0), 2);
        assertEquals(ddManager.readPerm(1), 1);
        assertEquals(ddManager.readPerm(2), 0);

        result.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testReorderEnforcedSame(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get Some variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD var2 = ddManager.ithVar(2);

        /* Build var1 + 2*var2 - var3 */
        ADD var0_plus_2var1 = var0.apply((a, b) -> a + 2 * b, var1);
        ADD result = var0_plus_2var1.minus(var2);
        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var0_plus_2var1.recursiveDeref();

        /* Get fingerprint of the order prior to reordering */
        List<ADD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(result));

        /* Enforce variables order on ADD */
        int[] permutation = {0, 1, 2};
        ddManager.setVariableOrder(permutation);

        /* Assert that the same variables order results in the same ADD. */
        List<ADD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(result));
        assertEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        result.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testAutomaticReordering(ADDBackend addBackend) {
        ddManager = new ADDManager(addBackend);

        /* Load ADD that can be reordered */
        ADD subject = getReorderableADD();

        /* Get fingerprint of the order prior to automatic reordering */
        List<ADD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));

        /* Enable automatic reordering */
        ddManager.setNextReordering(1);
        ddManager.enableAutomaticReordering(DDReorderingType.ANNEALING);

        /* Trigger automatic reordering */
        ADD var4 = ddManager.ithVar(4);
        var4.recursiveDeref();

        /* Assert that automatic reordering with DDReorderingType.ANNEALING changes the order */
        List<ADD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));
        assertNotEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        subject.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testDisableAutomaticReordering(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Load ADD that can be reordered */
        ADD subject = getReorderableADD();

        /* Get fingerprint of the order prior to automatic reordering */
        List<ADD> preorderBeforeReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));

        /* Enable and disable automatic reordering */
        ddManager.setNextReordering(1);
        ddManager.enableAutomaticReordering(DDReorderingType.ANNEALING);
        ddManager.disableAutomaticReordering();

        /* Try to trigger automatic reordering */
        ADD var4 = ddManager.ithVar(4);
        var4.recursiveDeref();

        /* Assert that automatic reordering is not triggered and the order does not change */
        List<ADD> preorderAfterReduceHeap = Lists.newArrayList(new PreorderTraverser<>(subject));
        assertEquals(preorderAfterReduceHeap, preorderBeforeReduceHeap);
        subject.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testCompose(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get constants and variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD one = ddManager.readOne();
        ADD zero = ddManager.readZero();

        /* Compose the ADD with the second variable */
        ADD composed = var0.compose(var1, 0);

        /* Assert that the variable changed */
        assertEquals(composed.readIndex(), 1);
        assertEquals(composed.t(), one);
        assertEquals(composed.e(), zero);

        /* Release memory */
        one.recursiveDeref();
        zero.recursiveDeref();
        var0.recursiveDeref();
        var1.recursiveDeref();
        composed.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testVectorCompose(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get constants and variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD var2 = ddManager.ithVar(2);
        ADD var3 = ddManager.ithVar(3);
        ADD zero = ddManager.readZero();
        ADD one = ddManager.readOne();
        ADD two = ddManager.constant(2.0);

        /* Build a composed ADD with var2 and var3 */
        ADD var0_plus_var1 = var0.plus(var1);
        ADD[] vector = {var3, var2, one, one};
        ADD var2_plus_var3 = var0_plus_var1.vectorCompose(vector);

        /* Assert that the variables changed and the structure is the same */
        assertEquals(var2_plus_var3.readIndex(), 2);
        assertEquals(var2_plus_var3.t().readIndex(), 3);
        assertEquals(var2_plus_var3.e().readIndex(), 3);
        assertEquals(var2_plus_var3.t().t(), two);
        assertEquals(var2_plus_var3.t().e(), one);
        assertEquals(var2_plus_var3.e().t(), one);
        assertEquals(var2_plus_var3.e().e(), zero);

        /* Release memory */
        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var3.recursiveDeref();
        zero.recursiveDeref();
        one.recursiveDeref();
        two.recursiveDeref();
        var0_plus_var1.recursiveDeref();
        var2_plus_var3.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testDagSize(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        /* Get constants and variables */
        ADD var0 = ddManager.ithVar(0);
        ADD var1 = ddManager.ithVar(1);
        ADD var2 = ddManager.ithVar(2);
        ADD var0_plus_var1 = var0.plus(var1);
        ADD var0_plus_var1_plus_var2 = var0_plus_var1.plus(var2);

        assertEquals(var0.dagSize(), 3);
        assertEquals(var0_plus_var1.dagSize(), 6);
        assertEquals(var0_plus_var1_plus_var2.dagSize(), 10);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var2.recursiveDeref();
        var0_plus_var1.recursiveDeref();
        var0_plus_var1_plus_var2.recursiveDeref();
    }

    @Test
    public void testIte() {
        ddManager = new ADDManager(BackendProvider.getSylvanADDBackend());

        ADD var0 = ddManager.ithVar(0);
        ADD one = ddManager.readOne();
        ADD zero = ddManager.readZero();

        ADD ite = var0.ite(zero, one);

        assertEquals(ite.readIndex(), 0);
        assertEquals(ite.t().v(), 0);
        assertEquals(ite.e().v(), 1);

        var0.recursiveDeref();
        one.recursiveDeref();
        zero.recursiveDeref();
        ite.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicApplyClearCache(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("temp0");
        ADD mod_var0 = var0.monadicApply(x -> x > 0.0 ? 1.0 : -1.0);
        for (int i = 1; i < 1500; i++) {
            double weight = i;
            ADD weighted_mod_var0 = mod_var0.monadicApply(x -> weight * x);
            assertEquals(weighted_mod_var0.t().v(), weight);
            assertEquals(weighted_mod_var0.e().v(), -weight);
            weighted_mod_var0.recursiveDeref();
        }

        var0.recursiveDeref();
        mod_var0.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicApplyWithAnotherADD(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("test");
        ADD five = ddManager.constant(5.0);
        ADD mod_var0 = var0.monadicApply(x -> five.v());

        assertEquals(mod_var0.v(), 5.0);

        var0.recursiveDeref();
        five.recursiveDeref();
        mod_var0.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApplyWithAnotherADD(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("test");
        ADD five = ddManager.constant(5.0);
        ADD mod_var0 = var0.apply((x, y) -> x + y + five.v(), five);

        assertFalse(mod_var0.isConstant());
        assertEquals(mod_var0.t().v(), 11.0);
        assertEquals(mod_var0.e().v(), 10.0);

        var0.recursiveDeref();
        five.recursiveDeref();
        mod_var0.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testThresholdADD(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("var0");
        ADD var1 = ddManager.namedVar("var1");
        ADD var0MinusVar1 = var0.minus(var1);
        ADD one = ddManager.constant(1.0);
        ADD threshold1 = var0MinusVar1.threshold(one);

        assertEquals(threshold1.t().e().v(), 1.0);
        assertEquals(threshold1.t().t().v(), 0.0);
        assertEquals(threshold1.e().v(), 0.0);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var0MinusVar1.recursiveDeref();
        one.recursiveDeref();
        threshold1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicApply2Leaves(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("var0");
        ADD var1 = ddManager.namedVar("var1");
        ADD var0MinusVar1 = var0.minus(var1);
        ADD three = ddManager.constant(3.0);

        List<ADD> derefADDs = new ArrayList<>();

        UnaryOperator<ADD> tripleValues = (add) -> {
            if (add.isConstant()) {
                ADD resultADD = add.times(three);
                derefADDs.add(resultADD);
                return resultADD;
            }
            return new ADD(ddManager.getBackend().invalid(), ddManager);
        };

        ADD tripledADD = var0MinusVar1.monadicApply2(tripleValues);

        assertEquals(tripledADD.t().t().v(), 0.0);
        assertEquals(tripledADD.t().e().v(), 3.0);
        assertEquals(tripledADD.e().t().v(), -3.0);
        assertEquals(tripledADD.e().e().v(), 0.0);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var0MinusVar1.recursiveDeref();
        three.recursiveDeref();
        tripledADD.recursiveDeref();
        for (ADD add : derefADDs) {
            add.recursiveDeref();
        }
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApply2Leaves(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("var0");
        ADD var1 = ddManager.namedVar("var1");
        ADD var0MinusVar1 = var0.minus(var1);

        List<ADD> derefADDs = new ArrayList<>();

        BinaryOperator<ADD> doubleValues = (add1, add2) -> {
            if (add1.isConstant() && add2.isConstant()) {
                ADD resultADD = add1.plus(add2);
                derefADDs.add(resultADD);
                return resultADD;
            }
            return new ADD(ddManager.getBackend().invalid(), ddManager);
        };

        ADD doubledADD = var0MinusVar1.apply2(doubleValues, var0MinusVar1);

        assertEquals(doubledADD.t().t().v(), 0.0);
        assertEquals(doubledADD.t().e().v(), 2.0);
        assertEquals(doubledADD.e().t().v(), -2.0);
        assertEquals(doubledADD.e().e().v(), 0.0);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var0MinusVar1.recursiveDeref();
        doubledADD.recursiveDeref();
        for (ADD add : derefADDs) {
            add.recursiveDeref();
        }
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMonadicApply2InnerNodes(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("var0");
        ADD var1 = ddManager.namedVar("var1");
        ADD var0MinusVar1 = var0.minus(var1);

        UnaryOperator<ADD> useLeftChildValue = (add) -> {
            if (add.dagSize() > 2 && add.t().isConstant()) {
                return add.t();
            }
            return new ADD(ddManager.getBackend().invalid(), ddManager);
        };

        ADD leftChildValueADD = var0MinusVar1.monadicApply2(useLeftChildValue);

        assertEquals(leftChildValueADD.t().v(), 0.0);
        assertEquals(leftChildValueADD.e().v(), -1.0);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var0MinusVar1.recursiveDeref();
        leftChildValueADD.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testApply2InnerNodes(ADDBackend backend) {
        ddManager = new ADDManager(backend);

        ADD var0 = ddManager.namedVar("var0");
        ADD var1 = ddManager.namedVar("var1");
        ADD var0MinusVar1 = var0.minus(var1);

        BinaryOperator<ADD> combinedSize = (add1, add2) -> ddManager.constant(add1.dagSize() + add2.dagSize());

        ADD combinedSizeADD = var0MinusVar1.apply2(combinedSize, var0MinusVar1);

        assertEquals(combinedSizeADD.v(), 12.0);

        var0.recursiveDeref();
        var1.recursiveDeref();
        var0MinusVar1.recursiveDeref();
        combinedSizeADD.recursiveDeref();
        combinedSizeADD.recursiveDeref();
    }
}
