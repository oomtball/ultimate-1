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

package info.scce.addlib.examples.manipulation;

import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

/**
 * Example for reordering a decision diagram with various reordering strategies.
 * Currently, only CUDD offers the option to reorder decision diagrams.
 */
public final class ReorderingExample {

    private ReorderingExample() {

    }

    public static void main(String[] args) {

        /* Create the BDD */
        BDDManager bddManager = new BDDManager(BackendProvider.getBDDBackend("cudd"));
        BDD bdd = getReorderableBDD(bddManager);

        /* Before reordering:
         *
         *       result
         *          \
         *           a
         *          /  \
         *         :    \
         *         |    |
         *         :    |
         * x1x3 - b     b - x1A2
         *      : |    /|
         *     /  |   : |
         *     :  d  :  c
         *     |  : \: /|
         *     \  : / \ |
         *        0     1
         *
         */

        /* Size of the BDD with the default order */
        int expectedDD1Size = 6;
        assert expectedDD1Size == bdd.dagSize();

        /* The easiest way to reorder a decision diagram is with the given types
         * in info.scce.addlib.cudd.Cudd_ReorderingType, e.g. EXACT.
         */
        bddManager.reduceHeap(DDReorderingType.EXACT, 0);

        /* After reordering:
         *
         *       result
         *          \
         *           a
         *          /  \
         *         :    \
         *         d    c
         *         :\  :
         *         | \/
         *         : :\
         *         |/  b
         *         :  : \
         *         | /  |
         *         0    1
         *
         */

        /* Size of the ADD with the default order */
        int expectedDD2Size = 5;
        assert expectedDD2Size == bdd.dagSize();

        /* The example above illustrates how reordering with one of the reordering strategies can be done.
         * The order can also be adjusted manually with setVariableOrder.
         * The permutation is done on the original order.
         */
        int[] permutation = {2, 3, 0, 1};
        bddManager.setVariableOrder(permutation);

        int expectedDD3Size = 7;
        assert expectedDD3Size == bdd.dagSize();

        /* The library also offers the option to automatically reorder decision diagrams.
         * The method enableAutomaticReordering enables it, disableAutomaticReordering disables it.
         * Automatic reordering should be used with care when working with very large decision diagrams
         * due to the required time to reorder.
         * The reordering happens when the number of nodes reaches a certain threshold determined by
         * setNextReordering.
         */
        bddManager.setNextReordering(0);
        bddManager.enableAutomaticReordering(DDReorderingType.EXACT);

        /* Trigger for automatic reordering */
        BDD x0 = bddManager.namedVar("a");
        BDD trigger = bdd.or(x0);
        x0.recursiveDeref();
        trigger.recursiveDeref();

        int expectedDD4Size = 5;
        assert expectedDD4Size == bdd.dagSize();

        bdd.recursiveDeref();
        bddManager.quit();
    }

    private static BDD getReorderableBDD(BDDManager bddManager) {

        /* Build XDD */
        BDD x0 = bddManager.namedVar("a");
        BDD x1 = bddManager.namedVar("b");
        BDD x2 = bddManager.namedVar("c");
        BDD x3 = bddManager.namedVar("d");
        BDD x0Not = x0.not();
        BDD x1A2 = x1.and(x2);
        BDD conj1 = x0.and(x1A2);
        BDD x1A3 = x1.and(x3);
        BDD conj2 = x0Not.and(x1A3);
        BDD result = conj1.or(conj2);

        /* Release space for reordering to take place */
        x0.recursiveDeref();
        x1.recursiveDeref();
        x2.recursiveDeref();
        x3.recursiveDeref();
        x0Not.recursiveDeref();
        x1A2.recursiveDeref();
        conj1.recursiveDeref();
        x1A3.recursiveDeref();
        conj2.recursiveDeref();

        return result;
    }
}
