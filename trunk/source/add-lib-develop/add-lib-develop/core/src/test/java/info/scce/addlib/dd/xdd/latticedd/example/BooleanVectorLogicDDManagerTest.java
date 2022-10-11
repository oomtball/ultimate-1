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

public class BooleanVectorLogicDDManagerTest extends DDManagerTest {

    private static final int N = 2;

    private BooleanVectorLogicDDManager ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testCustomOperation(ADDBackend addBackend) {
        ddManager = new BooleanVectorLogicDDManager(addBackend, N);

        /* Get BooleanVector values */
        BooleanVector b00 = new BooleanVector(false, false);
        BooleanVector b01 = new BooleanVector(false, true);
        BooleanVector b10 = new BooleanVector(true, false);
        BooleanVector b11 = new BooleanVector(true, true);

        /* Get constant DDs */
        XDD<BooleanVector> const00 = ddManager.constant(b00);
        XDD<BooleanVector> const01 = ddManager.constant(b01);
        XDD<BooleanVector> const10 = ddManager.constant(b10);
        XDD<BooleanVector> const11 = ddManager.constant(b11);

        /* Get some simple DDs */
        XDD<BooleanVector> const10_if_var0_else_const00 = ddManager.ithVar(0, const10, const00);
        XDD<BooleanVector> const01_if_var1_else_const00 = ddManager.ithVar(1, const01, const00);

        /* Assert identity */
        XDD<BooleanVector> alsoIdentity = const10_if_var0_else_const00.or(const01_if_var1_else_const00);
        XDD<BooleanVector> identity =
                const10_if_var0_else_const00.apply(BooleanVector::or, const01_if_var1_else_const00);

        assertEquals(alsoIdentity, identity);
        assertEquals(identity.e().e(), const00);
        assertEquals(identity.e().t(), const01);
        assertEquals(identity.t().e(), const10);
        assertEquals(identity.t().t(), const11);
        assertEquals(identity.e().e().v(), b00);
        assertEquals(identity.e().t().v(), b01);
        assertEquals(identity.t().e().v(), b10);
        assertEquals(identity.t().t().v(), b11);

        /* Release memory */
        const10_if_var0_else_const00.recursiveDeref();
        const01_if_var1_else_const00.recursiveDeref();
        alsoIdentity.recursiveDeref();
        identity.recursiveDeref();
        const00.recursiveDeref();
        const01.recursiveDeref();
        const10.recursiveDeref();
        const11.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testComposition(ADDBackend addBackend) {
        ddManager = new BooleanVectorLogicDDManager(addBackend, N);

        /* Get constant DDs */
        XDD<BooleanVector> const00 = ddManager.constant(new BooleanVector(false, false));
        XDD<BooleanVector> const01 = ddManager.constant(new BooleanVector(false, true));
        XDD<BooleanVector> const10 = ddManager.constant(new BooleanVector(true, false));

        /* Get identity DD */
        XDD<BooleanVector> idVar0 = ddManager.ithVar(0, const10, const00);
        XDD<BooleanVector> idVar1 = ddManager.ithVar(1, const01, const00);
        XDD<BooleanVector> id = idVar0.or(idVar1);
        const00.recursiveDeref();
        const01.recursiveDeref();
        const10.recursiveDeref();

        /* Assert compositions with id */
        XDD<BooleanVector> idVar1_comp_id = id.monadicApply(bn -> idVar1.eval(bn.data()).v());
        XDD<BooleanVector> idVar0_comp_id = id.monadicApply(bn -> idVar0.eval(bn.data()).v());
        XDD<BooleanVector> id_comp_idVar1 = idVar1.monadicApply(bn -> id.eval(bn.data()).v());
        XDD<BooleanVector> id_comp_idVar0 = idVar0.monadicApply(bn -> id.eval(bn.data()).v());
        assertEquals(idVar1_comp_id, idVar1);
        assertEquals(idVar0_comp_id, idVar0);
        assertEquals(id_comp_idVar1, idVar1);
        assertEquals(id_comp_idVar0, idVar0);

        /* Release memory */
        idVar0.recursiveDeref();
        idVar1.recursiveDeref();
        id.recursiveDeref();
        idVar1_comp_id.recursiveDeref();
        idVar0_comp_id.recursiveDeref();
        id_comp_idVar1.recursiveDeref();
        id_comp_idVar0.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        ddManager = new BooleanVectorLogicDDManager(addBackend, N);

        /* Repeat test a few times with varying values */
        for (int i = 0; i < 4; i++) {
            boolean b0 = i % 2 == 0;
            boolean b1 = (i / 2) % 2 == 0;

            /* Assert parseElement is iverse of toString for values b0, b1 */
            XDD<BooleanVector> xddBooleanVector = ddManager.constant(new BooleanVector(b0, b1));
            String str = xddBooleanVector.toString();
            XDD<BooleanVector> xddBooleanVectorReproduced = ddManager.constant(ddManager.parseElement(str));
            assertEquals(xddBooleanVectorReproduced, xddBooleanVector);

            /* Release memory */
            xddBooleanVector.recursiveDeref();
            xddBooleanVectorReproduced.recursiveDeref();
        }
    }
}
