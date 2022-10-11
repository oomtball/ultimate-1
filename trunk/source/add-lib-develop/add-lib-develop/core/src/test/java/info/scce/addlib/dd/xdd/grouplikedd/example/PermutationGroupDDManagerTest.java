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
package info.scce.addlib.dd.xdd.grouplikedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PermutationGroupDDManagerTest extends DDManagerTest {

    private static final int N = 5;

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testConstantDDs(ADDBackend addBackend) {
        PermutationGroupDDManager ddManager = new PermutationGroupDDManager(addBackend, N);

        /* Assert inverse */
        XDD<Permutation> f = ddManager.constant(new Permutation(2, 4, 3, 0, 1));
        XDD<Permutation> actualInverse = f.inverse();
        XDD<Permutation> expectedInverse = ddManager.constant(new Permutation(3, 4, 0, 2, 1));
        assertEquals(actualInverse, expectedInverse);
        assertEquals(actualInverse.v(), expectedInverse.v());
        actualInverse.recursiveDeref();
        expectedInverse.recursiveDeref();

        /* Assert composition */
        XDD<Permutation> g = ddManager.constant(new Permutation(3, 1, 2, 0, 4));
        XDD<Permutation> actualComposition = f.join(g);
        g.recursiveDeref();
        f.recursiveDeref();
        XDD<Permutation> expectedComposition = ddManager.constant(new Permutation(0, 4, 3, 2, 1));
        assertEquals(actualComposition, expectedComposition);
        assertEquals(actualComposition.v(), expectedComposition.v());
        expectedComposition.recursiveDeref();
        actualComposition.recursiveDeref();

        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testDDs(ADDBackend addBackend) {
        PermutationGroupDDManager ddManager = new PermutationGroupDDManager(addBackend, N);

        /* Get some DDs */
        XDD<Permutation> identity = ddManager.neutral();
        XDD<Permutation> f = ddManager.constant(new Permutation(2, 4, 3, 0, 1));
        XDD<Permutation> g = ddManager.constant(new Permutation(3, 1, 2, 0, 4));

        /* Assert composition */
        XDD<Permutation> f_join_g = f.join(g);
        XDD<Permutation> expected_f_join_g = ddManager.constant(new Permutation(0, 4, 3, 2, 1));
        assertEquals(f_join_g, expected_f_join_g);
        assertEquals(f_join_g.v(), expected_f_join_g.v());
        expected_f_join_g.recursiveDeref();

        /* Get small conditional DDs */
        XDD<Permutation> f_if_var0 = ddManager.ithVar(0, f, identity);
        XDD<Permutation> g_if_var1 = ddManager.ithVar(1, g, identity);

        /* Compose small DDs */
        XDD<Permutation> composition = f_if_var0.join(g_if_var1);
        f_if_var0.recursiveDeref();
        g_if_var1.recursiveDeref();
        assertFalse(composition.isConstant());

        /* Get first layer */
        XDD<Permutation> composition_then = composition.t();
        assertFalse(composition_then.isConstant());
        XDD<Permutation> composition_else = composition.e();
        composition.recursiveDeref();
        assertFalse(composition_else.isConstant());

        /* Get second layer */
        XDD<Permutation> composition_then_then = composition_then.t();
        assertTrue(composition_then_then.isConstant());
        XDD<Permutation> composition_then_else = composition_then.e();
        assertTrue(composition_then_else.isConstant());
        XDD<Permutation> composition_else_then = composition_else.t();
        assertTrue(composition_else_then.isConstant());
        XDD<Permutation> composition_else_else = composition_else.e();
        assertTrue(composition_else_else.isConstant());

        /* Assert composition */
        XDD<Permutation> expectedComposition_then_then = f_join_g;
        XDD<Permutation> expectedComposition_then_else = f;
        XDD<Permutation> expectedComposition_else_then = g;
        XDD<Permutation> expectedComposition_else_else = identity;
        f_join_g.recursiveDeref();
        f.recursiveDeref();
        g.recursiveDeref();
        identity.recursiveDeref();
        assertEquals(composition_then_then, expectedComposition_then_then);
        assertEquals(composition_then_else, expectedComposition_then_else);
        assertEquals(composition_else_then, expectedComposition_else_then);
        assertEquals(composition_else_else, expectedComposition_else_else);

        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        PermutationGroupDDManager ddManager = new PermutationGroupDDManager(addBackend, N);

        int[] p = {0, 1, 2, 3, 4};
        int n = 99;
        for (int i = 0; i < n; i++) {
            swapRandomIdx(p);

            /* Assert parseElement is inverse of toString for permutation p */
            XDD<Permutation> xddPermutation = ddManager.constant(new Permutation(p));
            String str = xddPermutation.toString();
            XDD<Permutation> xddPermutationReproduced =
                    ddManager.parse("\"" + str + "\"");
            assertEquals(xddPermutationReproduced, xddPermutation);

            /* Release memory */
            xddPermutation.recursiveDeref();
            xddPermutationReproduced.recursiveDeref();
        }
        assertRefCountZeroAndQuit(ddManager);
    }

    private void swapRandomIdx(int[] p) {
        int i0 = (int) (Math.random() * p.length);
        int i1 = (int) (Math.random() * p.length);
        int tmp = p[i0];
        p[i0] = p[i1];
        p[i1] = tmp;
    }
}
