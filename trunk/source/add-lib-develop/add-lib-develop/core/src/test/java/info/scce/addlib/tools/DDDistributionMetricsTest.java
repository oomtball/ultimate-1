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
package info.scce.addlib.tools;

import java.util.Map;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class DDDistributionMetricsTest {

    protected BDDManager bddManager;
    protected ADDManager addManager;
    protected BDD bdd;
    protected ADD add;

    public void setUpBDD(BDDBackend bddBackend) {
        bddManager = new BDDManager(bddBackend);
        /*
         *
         *           a
         *          /  \
         *         :    \
         *         |     |
         *         b     b
         *         |    / \
         *         |   /   \
         *         |  :    |
         *         |  c    c
         *         |  :\  / \
         *         |  | \:   |
         *         |__/__d   |
         *         : /\ /|   |
         *         |/ : \|   |
         *         : /   |   |
         *         0     1   |
         *         |_________|
         */

        BDD bdd1 = bddManager.namedVar("a");
        BDD bdd2 = bddManager.namedVar("b");
        BDD bdd3 = bddManager.namedVar("c");
        BDD bdd4 = bddManager.namedVar("d");
        BDD notBDD1 = bdd1.not();
        BDD notBDD2 = bdd2.not();
        BDD notBDD3 = bdd3.not();
        BDD bddN12 = notBDD1.and(bdd2);
        BDD bddN124 = bddN12.and(bdd4);
        BDD bdd1N2 = bdd1.and(notBDD2);
        BDD bdd1N23 = bdd1N2.and(bdd3);
        BDD bdd1N234 = bdd1N23.and(bdd4);
        BDD bdd12 = bdd1.and(bdd2);
        BDD bdd12N3 = bdd12.and(notBDD3);
        BDD bdd12N34 = bdd12N3.and(bdd4);
        BDD disj1 = bddN124.or(bdd1N234);
        bdd = disj1.or(bdd12N34);

        bdd1.recursiveDeref();
        bdd2.recursiveDeref();
        bdd3.recursiveDeref();
        bdd4.recursiveDeref();
        notBDD1.recursiveDeref();
        notBDD2.recursiveDeref();
        notBDD3.recursiveDeref();
        bddN12.recursiveDeref();
        bddN124.recursiveDeref();
        bdd1N2.recursiveDeref();
        bdd1N23.recursiveDeref();
        bdd1N234.recursiveDeref();
        bdd12.recursiveDeref();
        bdd12N3.recursiveDeref();
        bdd12N34.recursiveDeref();
        disj1.recursiveDeref();
    }

    public void setupADD(ADDBackend addBackend) {
        addManager = new ADDManager(addBackend);
        /*
         *
         *           a
         *          /  \
         *         :    \
         *         |     |
         *         b     b
         *        / \   / \
         *       :   \ :   \
         *       |    \|    |
         *       c     c    c
         *      / \   / \  / \
         *     :   \ :   |:   |
         *     |    |    |    |
         *     0    1    2    3
         */

        ADD a = addManager.namedVar("a");
        ADD b = addManager.namedVar("b");
        ADD c = addManager.namedVar("c");

        ADD aPb = a.plus(b);
        add = aPb.plus(c);

        a.recursiveDeref();
        b.recursiveDeref();
        c.recursiveDeref();
        aPb.recursiveDeref();
    }

    public void tearDownBDD() {
        bdd.recursiveDeref();
        bddManager.quit();
    }

    public void tearDownADD() {
        add.recursiveDeref();
        addManager.quit();
    }

    protected <T> void assertMappedElementIsEqual(Map<Integer, Integer> distribution, int key, int actual) {
        Integer value = distribution.get(key);
        assertNotNull(value);
        assertEquals(value, Integer.valueOf(actual));
    }
}