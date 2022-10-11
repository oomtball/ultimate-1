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
package info.scce.addlib.cudd;

import java.util.HashSet;
import java.util.Set;

import info.scce.addlib.apply.DD_AOP_Fn;
import info.scce.addlib.apply.DD_MAOP_Fn;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static info.scce.addlib.cudd.Cudd.CUDD_CACHE_SLOTS;
import static info.scce.addlib.cudd.Cudd.CUDD_UNIQUE_SLOTS;
import static info.scce.addlib.cudd.Cudd.Cudd_CheckZeroRef;
import static info.scce.addlib.cudd.Cudd.Cudd_E;
import static info.scce.addlib.cudd.Cudd.Cudd_Eval;
import static info.scce.addlib.cudd.Cudd.Cudd_Init;
import static info.scce.addlib.cudd.Cudd.Cudd_IsComplement;
import static info.scce.addlib.cudd.Cudd.Cudd_IsConstant;
import static info.scce.addlib.cudd.Cudd.Cudd_NodeReadIndex;
import static info.scce.addlib.cudd.Cudd.Cudd_Not;
import static info.scce.addlib.cudd.Cudd.Cudd_Quit;
import static info.scce.addlib.cudd.Cudd.Cudd_ReadLogicZero;
import static info.scce.addlib.cudd.Cudd.Cudd_ReadOne;
import static info.scce.addlib.cudd.Cudd.Cudd_ReadZddOne;
import static info.scce.addlib.cudd.Cudd.Cudd_ReadZero;
import static info.scce.addlib.cudd.Cudd.Cudd_RecursiveDeref;
import static info.scce.addlib.cudd.Cudd.Cudd_RecursiveDerefZdd;
import static info.scce.addlib.cudd.Cudd.Cudd_Ref;
import static info.scce.addlib.cudd.Cudd.Cudd_T;
import static info.scce.addlib.cudd.Cudd.Cudd_V;
import static info.scce.addlib.cudd.Cudd.Cudd_addApply;
import static info.scce.addlib.cudd.Cudd.Cudd_addConst;
import static info.scce.addlib.cudd.Cudd.Cudd_addIthVar;
import static info.scce.addlib.cudd.Cudd.Cudd_addMonadicApply;
import static info.scce.addlib.cudd.Cudd.Cudd_bddAnd;
import static info.scce.addlib.cudd.Cudd.Cudd_bddCompose;
import static info.scce.addlib.cudd.Cudd.Cudd_bddIthVar;
import static info.scce.addlib.cudd.Cudd.Cudd_bddOr;
import static info.scce.addlib.cudd.Cudd.Cudd_bddVectorCompose;
import static info.scce.addlib.cudd.Cudd.Cudd_bddXnor;
import static info.scce.addlib.cudd.Cudd.Cudd_zddChange;
import static info.scce.addlib.cudd.Cudd.Cudd_zddDiff;
import static info.scce.addlib.cudd.Cudd.Cudd_zddIthVar;
import static info.scce.addlib.cudd.Cudd.Cudd_zddUnion;
import static info.scce.addlib.cudd.DD_AOP.Cudd_addPlus;
import static info.scce.addlib.cudd.DD_AOP.Cudd_addTimes;
import static info.scce.addlib.util.Conversions.NULL;
import static info.scce.addlib.util.Conversions.asBoolean;
import static info.scce.addlib.util.Conversions.asInts;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@SuppressWarnings({"PMD.TooManyStaticImports",
                   "PMD.AvoidCatchingGenericException",
                   "PMD.AvoidThrowingRawExceptionTypes"})
public class CuddTest {

    private long ddManager = NULL;

    @BeforeClass
    public void setUp() {

        /* Initialize with default values */
        ddManager = Cudd_Init(0, 0, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, 0);
    }

    @AfterClass
    public void tearDown() {

        /* Check that no unwanted references remain */
        int expectedRefCount = 0;
        int actualRefCount = Cudd_CheckZeroRef(ddManager);
        assertEquals(actualRefCount, expectedRefCount);

        /* Release memory */
        Cudd_Quit(ddManager);
        ddManager = NULL;
    }

    @Test
    public void testIsComplement() {

        /* Assert new variable not complemented */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        assertFalse(asBoolean(Cudd_IsComplement(var0)));

        /* Assert not to complement decision diagram */
        long not_var0 = Cudd_Not(var0);
        assertTrue(asBoolean(Cudd_IsComplement(not_var0)));
    }

    @Test
    public void testNot() {
        long ptr = 0;
        long actualCompl = Cudd_Not(ptr);
        long expectedCompl = 1;
        assertEquals(actualCompl, expectedCompl);
    }

    @Test
    public void testBddOr() {
        /* Get some variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build disjunction: var0 | var1 */
        long var0_or_var1 = Cudd_bddOr(ddManager, var0, var1);
        Cudd_Ref(var0_or_var1);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Get some constants */
        long zero = Cudd_ReadLogicZero(ddManager);
        Cudd_Ref(zero);
        long one = Cudd_ReadOne(ddManager);
        Cudd_Ref(one);

        /* Get disjunction terminal nodes */
        long var0_or_var1_ass00 = Cudd_Eval(ddManager, var0_or_var1, asInts(false, false));
        long var0_or_var1_ass01 = Cudd_Eval(ddManager, var0_or_var1, asInts(false, true));
        long var0_or_var1_ass10 = Cudd_Eval(ddManager, var0_or_var1, asInts(true, false));
        long var0_or_var1_ass11 = Cudd_Eval(ddManager, var0_or_var1, asInts(true, true));

        /* Assert disjunction terminal nodes */
        assertEquals(var0_or_var1_ass00, zero);
        assertEquals(var0_or_var1_ass01, one);
        assertEquals(var0_or_var1_ass10, one);
        assertEquals(var0_or_var1_ass11, one);
        Cudd_RecursiveDeref(ddManager, zero);
        Cudd_RecursiveDeref(ddManager, one);
        Cudd_RecursiveDeref(ddManager, var0_or_var1);
    }

    @Test
    public void testAddMonadicApplyThroughCallback() {

        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build sum: var0 + var1 */
        long var0_plus_var1 = Cudd_addApply(ddManager, Cudd_addPlus, var0, var1);
        Cudd_Ref(var0_plus_var1);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Increment by 5 with callback to Java */
        DD_MAOP_Fn maopInc5 = new DD_MAOP_Fn() {

            @Override
            public long apply(long ddManager, long f) {
                if (asBoolean(Cudd_IsConstant(f))) {
                    return Cudd_addConst(ddManager, Cudd_V(f) + 5);
                }
                return NULL;
            }
        };
        long var0_plus_var1_inc5 = Cudd_addMonadicApply(ddManager, maopInc5, var0_plus_var1);
        Cudd_Ref(var0_plus_var1_inc5);
        Cudd_RecursiveDeref(ddManager, var0_plus_var1);

        /* Get sum + 5 terminals nodes */
        long var0_plus_var1_inc5_ass00 = Cudd_Eval(ddManager, var0_plus_var1_inc5, asInts(false, false));
        long var0_plus_var1_inc5_ass01 = Cudd_Eval(ddManager, var0_plus_var1_inc5, asInts(false, true));
        long var0_plus_var1_inc5_ass10 = Cudd_Eval(ddManager, var0_plus_var1_inc5, asInts(true, false));
        long var0_plus_var1_inc5_ass11 = Cudd_Eval(ddManager, var0_plus_var1_inc5, asInts(true, true));

        /* Assert sum terminal nodes */
        assertEquals(Cudd_V(var0_plus_var1_inc5_ass00), 5.0, 0);
        assertEquals(Cudd_V(var0_plus_var1_inc5_ass01), 6.0, 0);
        assertEquals(Cudd_V(var0_plus_var1_inc5_ass10), 6.0, 0);
        assertEquals(Cudd_V(var0_plus_var1_inc5_ass11), 7.0, 0);
        Cudd_RecursiveDeref(ddManager, var0_plus_var1_inc5);
    }

    @Test
    public void testAddMonadicApplyThroughCallback2() {
        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Get some constants */
        long const5 = Cudd_addConst(ddManager, 5);
        Cudd_Ref(const5);

        /* Build sum: var0 + var1 */
        long var0_plus_var1 = Cudd_addApply(ddManager, Cudd_addPlus, var0, var1);
        Cudd_Ref(var0_plus_var1);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);
        long var0_plus_var1_plus_const5 = Cudd_addApply(ddManager, Cudd_addPlus, var0_plus_var1, const5);
        Cudd_Ref(var0_plus_var1_plus_const5);
        Cudd_RecursiveDeref(ddManager, var0_plus_var1);
        Cudd_RecursiveDeref(ddManager, const5);

        /* Build inverse of the sum */
        final Set<Long> actualInvokationsOnTerminals = new HashSet<>();
        DD_MAOP_Fn maopInv = new DD_MAOP_Fn() {

            @Override
            public long apply(long ddManager, long f) {
                if (asBoolean(Cudd_IsConstant(f))) {
                    actualInvokationsOnTerminals.add(f);
                    double inverse = 1.0 / Cudd_V(f);
                    return Cudd_addConst(ddManager, inverse);
                }
                return NULL;
            }
        };
        long inverse_of_var0_plus_var1_plus_const5 =
                Cudd_addMonadicApply(ddManager, maopInv, var0_plus_var1_plus_const5);
        Cudd_Ref(inverse_of_var0_plus_var1_plus_const5);

        /* Assert invocations on sum terminal nodes */
        Set<Long> expectedInvokationsOnTerminals = new HashSet<>();
        expectedInvokationsOnTerminals.add(Cudd_Eval(ddManager, var0_plus_var1_plus_const5, asInts(false, false)));
        expectedInvokationsOnTerminals.add(Cudd_Eval(ddManager, var0_plus_var1_plus_const5, asInts(false, true)));
        expectedInvokationsOnTerminals.add(Cudd_Eval(ddManager, var0_plus_var1_plus_const5, asInts(true, true)));
        assertEquals(actualInvokationsOnTerminals, expectedInvokationsOnTerminals);
        Cudd_RecursiveDeref(ddManager, var0_plus_var1_plus_const5);

        /* Get inverse of sum terminals nodes */
        long inverse_of_var0_plus_var1_plus_const5_ass00 =
                Cudd_Eval(ddManager, inverse_of_var0_plus_var1_plus_const5, asInts(false, false));
        long inverse_of_var0_plus_var1_plus_const5_ass01 =
                Cudd_Eval(ddManager, inverse_of_var0_plus_var1_plus_const5, asInts(false, true));
        long inverse_of_var0_plus_var1_plus_const5_ass10 =
                Cudd_Eval(ddManager, inverse_of_var0_plus_var1_plus_const5, asInts(true, false));
        long inverse_of_var0_plus_var1_plus_const5_ass11 =
                Cudd_Eval(ddManager, inverse_of_var0_plus_var1_plus_const5, asInts(true, true));

        /* Assert inverse of sum terminal nodes */
        assertEquals(Cudd_V(inverse_of_var0_plus_var1_plus_const5_ass00), 1.0 / 5.0, 0.0);
        assertEquals(Cudd_V(inverse_of_var0_plus_var1_plus_const5_ass01), 1.0 / 6.0, 0.0);
        assertEquals(Cudd_V(inverse_of_var0_plus_var1_plus_const5_ass10), 1.0 / 6.0, 0.0);
        assertEquals(Cudd_V(inverse_of_var0_plus_var1_plus_const5_ass11), 1.0 / 7.0, 0.0);
        Cudd_RecursiveDeref(ddManager, inverse_of_var0_plus_var1_plus_const5);
    }

    @Test
    public void testAddApplyThroughCallback() {
        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build sum + 5: var0 + var1 + 5 */
        final Set<Long> actualInvokationFingerprints = new HashSet<>();
        DD_AOP_Fn aopPlusPlus5 = new DD_AOP_Fn() {

            @Override
            public long apply(long ddManager, long f, long g) {
                if (asBoolean(Cudd_IsConstant(f)) && asBoolean(Cudd_IsConstant(g))) {
                    long invokationFingerprint = f + g;
                    actualInvokationFingerprints.add(invokationFingerprint);
                    double sumPlus5 = Cudd_V(f) + Cudd_V(g) + 5;
                    return Cudd_addConst(ddManager, sumPlus5);
                }
                return NULL;
            }
        };
        long var0_plus_var1_plus_5 = Cudd_addApply(ddManager, aopPlusPlus5, var0, var1);
        Cudd_Ref(var0_plus_var1_plus_5);

        /* Assert invocations on var0 respectively var1 terminals */
        Set<Long> expectedInvokationFingerprints = new HashSet<>();
        expectedInvokationFingerprints.add(Cudd_E(var0) + Cudd_E(var1));
        expectedInvokationFingerprints.add(Cudd_E(var0) + Cudd_T(var1));
        expectedInvokationFingerprints.add(Cudd_T(var0) + Cudd_E(var1));
        expectedInvokationFingerprints.add(Cudd_T(var0) + Cudd_T(var1));
        assertEquals(actualInvokationFingerprints, expectedInvokationFingerprints);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Get sum + 5 terminals nodes */
        long var0_plus_var1_plus_5_ass00 = Cudd_Eval(ddManager, var0_plus_var1_plus_5, asInts(false, false));
        long var0_plus_var1_plus_5_ass01 = Cudd_Eval(ddManager, var0_plus_var1_plus_5, asInts(false, true));
        long var0_plus_var1_plus_5_ass10 = Cudd_Eval(ddManager, var0_plus_var1_plus_5, asInts(true, false));
        long var0_plus_var1_plus_5_ass11 = Cudd_Eval(ddManager, var0_plus_var1_plus_5, asInts(true, true));

        /* Assert inverse of sum terminal nodes */
        assertEquals(Cudd_V(var0_plus_var1_plus_5_ass00), 5.0, 0.0);
        assertEquals(Cudd_V(var0_plus_var1_plus_5_ass01), 6.0, 0.0);
        assertEquals(Cudd_V(var0_plus_var1_plus_5_ass10), 6.0, 0.0);
        assertEquals(Cudd_V(var0_plus_var1_plus_5_ass11), 7.0, 0.0);
        Cudd_RecursiveDeref(ddManager, var0_plus_var1_plus_5);
    }

    @Test
    public void testAddApply() {

        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Get some constants */
        long const5 = Cudd_addConst(ddManager, 5.67);
        Cudd_Ref(const5);
        long const1 = Cudd_ReadOne(ddManager);
        Cudd_Ref(const1);
        long const0 = Cudd_ReadZero(ddManager);
        Cudd_Ref(const0);

        /* Build var1 * const5 */
        long var1_times_const5 = Cudd_addApply(ddManager, Cudd_addTimes, var1, const5);
        Cudd_Ref(var1_times_const5);
        Cudd_RecursiveDeref(ddManager, var1);
        Cudd_RecursiveDeref(ddManager, const5);

        /* Build var1 * const5 + var0 */
        long var1_times_const5_plus_var0 = Cudd_addApply(ddManager, Cudd_addPlus, var1_times_const5, var0);
        Cudd_Ref(var1_times_const5_plus_var0);
        Cudd_RecursiveDeref(ddManager, var1_times_const5);
        Cudd_RecursiveDeref(ddManager, var0);

        /* Build var1 * const5 + var0 + const1 */
        long var1_times_const5_plus_var0_plus_const1 =
                Cudd_addApply(ddManager, Cudd_addPlus, var1_times_const5_plus_var0, const1);
        Cudd_Ref(var1_times_const5_plus_var0_plus_const1);
        Cudd_RecursiveDeref(ddManager, var1_times_const5_plus_var0);
        Cudd_RecursiveDeref(ddManager, const1);

        /* Get terminals nodes */
        long var1_times_const5_plus_var0_plus_const1_ass00 =
                Cudd_Eval(ddManager, var1_times_const5_plus_var0_plus_const1, asInts(false, false));
        long var1_times_const5_plus_var0_plus_const1_ass01 =
                Cudd_Eval(ddManager, var1_times_const5_plus_var0_plus_const1, asInts(false, true));
        long var1_times_const5_plus_var0_plus_const1_ass10 =
                Cudd_Eval(ddManager, var1_times_const5_plus_var0_plus_const1, asInts(true, false));
        long var1_times_const5_plus_var0_plus_const1_ass11 =
                Cudd_Eval(ddManager, var1_times_const5_plus_var0_plus_const1, asInts(true, true));

        /* Assert terminal nodes */
        assertEquals(Cudd_V(var1_times_const5_plus_var0_plus_const1_ass00), 1.0, 0.0);
        assertEquals(Cudd_V(var1_times_const5_plus_var0_plus_const1_ass01), 6.67, 0.0);
        assertEquals(Cudd_V(var1_times_const5_plus_var0_plus_const1_ass10), 2.0, 0.0);
        assertEquals(Cudd_V(var1_times_const5_plus_var0_plus_const1_ass11), 7.67, 0.0);

        /* Build ... * const0 */
        long whatever_times_const0 =
                Cudd_addApply(ddManager, Cudd_addTimes, const0, var1_times_const5_plus_var0_plus_const1);
        Cudd_Ref(whatever_times_const0);
        Cudd_RecursiveDeref(ddManager, var1_times_const5_plus_var0_plus_const1);

        /* Assert zero */
        assertEquals(const0, whatever_times_const0);
        Cudd_RecursiveDeref(ddManager, const0);
        Cudd_RecursiveDeref(ddManager, whatever_times_const0);
    }

    @Test
    public void testBddCompose() {

        /* Get some variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Get some constants */
        long one = Cudd_ReadOne(ddManager);
        Cudd_Ref(one);

        /* Build disjunction: var0 | var1 */
        long var0_or_var1 = Cudd_bddOr(ddManager, var0, var1);
        Cudd_Ref(var0_or_var1);

        /* Build composition with disjunction */
        long composition_one_or_var1 = Cudd_bddCompose(ddManager, var0_or_var1, one, 0);
        Cudd_Ref(composition_one_or_var1);
        Cudd_RecursiveDeref(ddManager, var0_or_var1);

        /* Assert composition with disjunction */
        assertEquals(composition_one_or_var1, one);
        Cudd_RecursiveDeref(ddManager, composition_one_or_var1);

        /* Build conjunction: var0 & var1 */
        long var0_and_var1 = Cudd_bddAnd(ddManager, var0, var1);
        Cudd_Ref(var0_and_var1);

        /* Build composition with conjunction */
        long composition_var0_and_one = Cudd_bddCompose(ddManager, var0_and_var1, one, 1);
        Cudd_Ref(composition_var0_and_one);
        Cudd_RecursiveDeref(ddManager, var0_and_var1);
        Cudd_RecursiveDeref(ddManager, one);

        /* Assert composition with conjunction */
        assertEquals(composition_var0_and_one, var0);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);
        Cudd_RecursiveDeref(ddManager, composition_var0_and_one);
    }

    @Test
    public void testBddVectorCompose() {

        /* Get some variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build var0 == var1 */
        long var0_xnor_var1 = Cudd_bddXnor(ddManager, var0, var1);
        Cudd_Ref(var0_xnor_var1);

        /* Build conjunction: var0 & var1 */
        long var0_and_var1 = Cudd_bddAnd(ddManager, var0, var1);
        Cudd_Ref(var0_and_var1);

        /* Build disjunction: var0 | var1 */
        long var0_or_var1 = Cudd_bddOr(ddManager, var0, var1);
        Cudd_Ref(var0_or_var1);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Assert vector composition */
        long[] vector = {var0_and_var1, var0_or_var1};
        long actualComposition = Cudd_bddVectorCompose(ddManager, var0_xnor_var1, vector);
        Cudd_Ref(actualComposition);
        long expectedComposition = var0_xnor_var1;
        assertEquals(actualComposition, expectedComposition);
        Cudd_RecursiveDeref(ddManager, actualComposition);
        Cudd_RecursiveDeref(ddManager, var0_and_var1);
        Cudd_RecursiveDeref(ddManager, var0_or_var1);
        Cudd_RecursiveDeref(ddManager, var0_xnor_var1);
    }

    @Test
    public void testBddVectorCompose2() {

        /* Get some variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build conjunction: var0 & var1 */
        long var0_and_var1 = Cudd_bddAnd(ddManager, var0, var1);
        Cudd_Ref(var0_and_var1);

        /* Build disjunction: var0 | var1 */
        long var0_or_var1 = Cudd_bddOr(ddManager, var0, var1);
        Cudd_Ref(var0_or_var1);

        /* Assert composition */
        long expectedComposition = var0_and_var1;
        long[] vector = {var0_and_var1, var0_or_var1};
        long actualComposition = Cudd_bddVectorCompose(ddManager, var0, vector);
        Cudd_Ref(actualComposition);
        assertEquals(actualComposition, expectedComposition);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);
        Cudd_RecursiveDeref(ddManager, var0_and_var1);
        Cudd_RecursiveDeref(ddManager, var0_or_var1);
        Cudd_RecursiveDeref(ddManager, actualComposition);
    }

    @Test
    public void testEval() {

        /* Get some variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Get some constants */
        long zero = Cudd_ReadLogicZero(ddManager);
        Cudd_Ref(zero);
        long one = Cudd_ReadOne(ddManager);
        Cudd_Ref(one);

        /* Build conjunction: var0 & var1 */
        long var0_and_var1 = Cudd_bddAnd(ddManager, var0, var1);
        Cudd_Ref(var0_and_var1);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Assert evaluation */
        assertEquals(Cudd_Eval(ddManager, var0_and_var1, asInts(false, false)), zero);
        assertEquals(Cudd_Eval(ddManager, var0_and_var1, asInts(false, true)), zero);
        assertEquals(Cudd_Eval(ddManager, var0_and_var1, asInts(true, false)), zero);
        assertEquals(Cudd_Eval(ddManager, var0_and_var1, asInts(true, true)), one);
        Cudd_RecursiveDeref(ddManager, zero);
        Cudd_RecursiveDeref(ddManager, one);
        Cudd_RecursiveDeref(ddManager, var0_and_var1);
    }

    @Test
    public void testAddIthVar() {

        /* Get some variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);

        /* Get some constants */
        long zero = Cudd_ReadLogicZero(ddManager);
        long one = Cudd_ReadOne(ddManager);
        Cudd_Ref(zero);
        Cudd_Ref(one);

        /* Assert variable */
        assertEquals(Cudd_T(var0), one);
        assertEquals(Cudd_E(var0), zero);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, one);
        Cudd_RecursiveDeref(ddManager, zero);
    }

    @Test
    public void testBddOrSimilarToDocumentation() {

        /* Get the variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build the disjunction */
        long disjunction = Cudd_bddOr(ddManager, var0, var1);
        Cudd_Ref(disjunction);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Evaluate disjunction for assignment var0 := 1, var1 := 0 */
        int[] assignment = {1, 0};
        long terminal = Cudd_Eval(ddManager, disjunction, assignment);

        /* See if the terminal is what we expect it to be */
        long one = Cudd_ReadOne(ddManager);
        Cudd_Ref(one);
        assertEquals(one, terminal);

        /* Release memory */
        Cudd_RecursiveDeref(ddManager, one);
        Cudd_RecursiveDeref(ddManager, disjunction);
    }

    @Test
    public void testBddOrAsInDocumentation() {

        /* Initialize DDManager with default values */
        long ddManager = Cudd_Init(0, 0, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, 0);

        /* Get the variables */
        long var0 = Cudd_bddIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_bddIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build the disjunction */
        long disjunction = Cudd_bddOr(ddManager, var0, var1);
        Cudd_Ref(disjunction);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Evaluate disjunction for assignment var0 := 1, var1 := 0 */
        int[] assignment = {1, 0};
        long terminal = Cudd_Eval(ddManager, disjunction, assignment);

        /* See if the terminal is what we expect it to be */
        long one = Cudd_ReadOne(ddManager);
        Cudd_Ref(one);
        assertEquals(one, terminal);

        /* Release memory */
        Cudd_RecursiveDeref(ddManager, one);
        Cudd_RecursiveDeref(ddManager, disjunction);
        Cudd_Quit(ddManager);
    }

    @Test
    public void testAddTimesSimilarToDocumentation() {

        /* Get a constant */
        long f = Cudd_addConst(ddManager, 5);
        Cudd_Ref(f);

        /* Multiply constant with some variables */
        for (int i = 3; i >= 0; i--) {

            /* Get the variable */
            long var = Cudd_addIthVar(ddManager, i);
            Cudd_Ref(var);

            /* Multiply the variable to the product */
            long tmp = Cudd_addApply(ddManager, Cudd_addTimes, var, f);
            Cudd_Ref(tmp);
            Cudd_RecursiveDeref(ddManager, f);
            Cudd_RecursiveDeref(ddManager, var);
            f = tmp;
        }

        /* Release memory */
        Cudd_RecursiveDeref(ddManager, f);
    }

    @Test
    public void testAddTimesAsInDocumentation() {

        /* Initialize DDManager with default values */
        long ddManager = Cudd_Init(0, 0, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, 0);

        /* Get a constant */
        long f = Cudd_addConst(ddManager, 5);
        Cudd_Ref(f);

        /* Multiply constant with some variables */
        for (int i = 3; i >= 0; i--) {

            /* Get the variable */
            long var = Cudd_addIthVar(ddManager, i);
            Cudd_Ref(var);

            /* Multiply the variable to the product */
            long tmp = Cudd_addApply(ddManager, Cudd_addTimes, var, f);
            Cudd_Ref(tmp);
            Cudd_RecursiveDeref(ddManager, f);
            Cudd_RecursiveDeref(ddManager, var);
            f = tmp;
        }

        /* Release memory */
        Cudd_RecursiveDeref(ddManager, f);
        Cudd_Quit(ddManager);
    }

    @Test
    public void testAddApplySimilarToDocumentation() {

        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build sum8: var0 + var1 + 8 */
        long sum8 = Cudd_addApply(ddManager, new DD_AOP_Fn() {

            @Override
            public long apply(long ddManager, long f, long g) {
                if (asBoolean(Cudd_IsConstant(f)) && asBoolean(Cudd_IsConstant(g))) {
                    double value = Cudd_V(f) + Cudd_V(g) + 8;
                    return Cudd_addConst(ddManager, value);
                }
                return NULL;
            }
        }, var0, var1);
        Cudd_Ref(sum8);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Assert values */
        long terminal8 = Cudd_E(Cudd_E(sum8));
        assertEquals(Cudd_V(terminal8), 8.0, 0.0);
        long terminal10 = Cudd_T(Cudd_T(sum8));
        assertEquals(Cudd_V(terminal10), 10.0, 0.0);

        /* Release memory */
        Cudd_RecursiveDeref(ddManager, sum8);
    }

    @Test
    public void testAddApplyAsInDocumentation() {

        /* Initialize DDManager with default values */
        long ddManager = Cudd_Init(0, 0, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, 0);

        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Build sum8: var0 + var1 + 8 */
        long sum8 = Cudd_addApply(ddManager, new DD_AOP_Fn() {

            @Override
            public long apply(long ddManager, long f, long g) {
                if (asBoolean(Cudd_IsConstant(f)) && asBoolean(Cudd_IsConstant(g))) {
                    double value = Cudd_V(f) + Cudd_V(g) + 8;
                    return Cudd_addConst(ddManager, value);
                }
                return NULL;
            }
        }, var0, var1);
        Cudd_Ref(sum8);
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);

        /* Release memory */
        Cudd_RecursiveDeref(ddManager, sum8);
        Cudd_Quit(ddManager);
    }

    @Test
    public void testNodeReadIndex() {

        /* Get some node */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);

        /* Assert node index */
        int expected = 0;
        int actual = Cudd_NodeReadIndex(var0);
        assertEquals(actual, expected);
        Cudd_RecursiveDeref(ddManager, var0);
    }

    @Test
    public void testApplyRtException() {

        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);
        long var1 = Cudd_addIthVar(ddManager, 1);
        Cudd_Ref(var1);

        /* Catch runtime exception without crashing the VM */
        try {
            Cudd_addApply(ddManager, new DD_AOP_Fn() {

                @Override
                public long apply(long ddManager, long f, long g) {
                    throw new RuntimeException("some runtime exception");
                }
            }, var0, var1);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "some runtime exception");
        }
        Cudd_RecursiveDeref(ddManager, var0);
        Cudd_RecursiveDeref(ddManager, var1);
    }

    @Test
    public void testMonadicApplyRtException() {

        /* Get some variables */
        long var0 = Cudd_addIthVar(ddManager, 0);
        Cudd_Ref(var0);

        /* Catch runtime exception without crashing the VM */
        try {
            Cudd_addMonadicApply(ddManager, new DD_MAOP_Fn() {

                @Override
                public long apply(long ddManager, long f) {
                    throw new RuntimeException("some runtime exception");
                }
            }, var0);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "some runtime exception");
        }
        Cudd_RecursiveDeref(ddManager, var0);
    }

    @Test
    public void testZddSimple() {
        /* Variables are named as in Figure 8: "Supression of irrelevant variables"
         * and the same operations are executed and tested
         * https://en.wikipedia.org/wiki/Zero-suppressed_decision_diagram */

        long zddManager = Cudd_Init(0, 0, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, 0);
        long A = Cudd_ReadZero(zddManager);
        Cudd_Ref(A);
        long B = Cudd_ReadZddOne(zddManager, 0);
        Cudd_Ref(B);

        long C = Cudd_zddChange(zddManager, B, 0);
        Cudd_Ref(C);
        long C_T = Cudd_T(C);
        Cudd_Ref(C_T);
        assertEquals(C_T, B);
        Cudd_RecursiveDerefZdd(zddManager, C_T);
        long C_E = Cudd_E(C);
        Cudd_Ref(C_E);
        assertEquals(C_E, A);
        Cudd_RecursiveDerefZdd(zddManager, C_E);

        long D = Cudd_zddChange(zddManager, B, 1);
        Cudd_Ref(D);
        long D_T = Cudd_T(D);
        Cudd_Ref(D_T);
        assertEquals(D_T, B);
        Cudd_RecursiveDerefZdd(zddManager, D_T);
        long D_E = Cudd_E(D);
        Cudd_Ref(D_E);
        assertEquals(D_E, A);
        Cudd_RecursiveDerefZdd(zddManager, D_E);

        /* x_1 is root instead of x_2(as in wikipedia figure)
         * because nodes are ordered in increasing order by default */
        long E = Cudd_zddUnion(zddManager, C, D);
        Cudd_Ref(E);
        long E_T = Cudd_T(E);
        Cudd_Ref(E_T);
        assertEquals(E_T, B);
        Cudd_RecursiveDerefZdd(zddManager, E_T);
        long E_E = Cudd_E(E);
        Cudd_Ref(E_E);
        long E_ET = Cudd_T(E_E);
        Cudd_Ref(E_ET);
        assertEquals(E_ET, B);
        Cudd_RecursiveDerefZdd(zddManager, E_ET);
        long E_EE = Cudd_E(E_E);
        Cudd_Ref(E_EE);
        assertEquals(E_EE, A);
        Cudd_RecursiveDerefZdd(zddManager, E_EE);
        Cudd_RecursiveDerefZdd(zddManager, E_E);

        long F = Cudd_zddUnion(zddManager, B, E);
        Cudd_Ref(F);
        long F_T = Cudd_T(F);
        Cudd_Ref(F_T);
        assertEquals(F_T, B);
        Cudd_RecursiveDerefZdd(zddManager, F_T);
        long F_E = Cudd_E(F);
        Cudd_Ref(F_E);
        long F_ET = Cudd_T(F_E);
        Cudd_Ref(F_ET);
        assertEquals(F_ET, B);
        Cudd_RecursiveDerefZdd(zddManager, F_ET);
        long F_EE = Cudd_E(F_E);
        Cudd_Ref(F_EE);
        assertEquals(F_EE, B);
        Cudd_RecursiveDerefZdd(zddManager, F_EE);
        Cudd_RecursiveDerefZdd(zddManager, F_E);

        long G = Cudd_zddDiff(zddManager, F, C);
        Cudd_Ref(G);
        long G_T = Cudd_T(G);
        Cudd_Ref(G_T);
        assertEquals(G_T, B);
        Cudd_RecursiveDerefZdd(zddManager, G_T);
        long G_E = Cudd_T(G);
        Cudd_Ref(G_E);
        assertEquals(G_E, B);
        Cudd_RecursiveDerefZdd(zddManager, G_E);

        Cudd_RecursiveDerefZdd(zddManager, A);
        Cudd_RecursiveDerefZdd(zddManager, B);
        Cudd_RecursiveDerefZdd(zddManager, C);
        Cudd_RecursiveDerefZdd(zddManager, D);
        Cudd_RecursiveDerefZdd(zddManager, E);
        Cudd_RecursiveDerefZdd(zddManager, F);
        Cudd_RecursiveDerefZdd(zddManager, G);

        assertEquals(Cudd_CheckZeroRef(zddManager), 0);
        Cudd_Quit(zddManager);
    }

    @Test
    public void testZDDEval() {

        /* Get a variable */
        long zdd = Cudd_zddIthVar(ddManager, 0);
        Cudd_Ref(zdd);

        /* Evaluate the ZDD */
        long evalResultTrue = Cudd_Eval(ddManager, zdd, new int[] {1});
        assertEquals(Cudd_V(evalResultTrue), 1.0);

        long evalResultFalse = Cudd_Eval(ddManager, zdd, new int[] {0});
        assertEquals(Cudd_V(evalResultFalse), 0.0);

        Cudd_RecursiveDerefZdd(ddManager, zdd);
    }
}


