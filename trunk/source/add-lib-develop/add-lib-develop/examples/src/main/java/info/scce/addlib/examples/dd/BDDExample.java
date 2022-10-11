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
package info.scce.addlib.examples.dd;

import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanLogicDDManager;
import info.scce.addlib.viewer.DotViewer;

/**
 * Example for using boolean decision diagrams with the ADD-Lib.
 * For the sake of compatibility only the CUDD backend is shown,
 * although the backend can be exchanged with the Sylvan one when Linux or Mac OS is used.
 */
public final class BDDExample {

    private BDDExample() {

    }

    public static void main(String[] args) {

        /* Change cudd to sylvan for the Sylvan backend */
        String backendType = "sylvan";
        BDDBackend bddBackend = BackendProvider.getBDDBackend(backendType);
        BDDManager bddManager = new BDDManager(bddBackend);

        /* Create the variables for the BDD */
        BDD x1 = bddManager.namedVar("a1");
        BDD x2 = bddManager.namedVar("a2");
        BDD x3 = bddManager.namedVar("a3");

        /* Create a BDD based on the formula (!a1 & a2) xor (!a1 & a3) */
        BDD notX1 = x1.not();
        BDD notX1AndX2 = notX1.and(x2);
        BDD notX1AndX3 = notX1.and(x3);
        BDD result = notX1AndX2.xor(notX1AndX3);

        /* Print the size of the created BDD */
        int expectedBDDSize = 4;
        assert expectedBDDSize == result.dagSize();

        /* Evaluate an assignment of variables with x1=0, x2=1, x3=1 */
        boolean eval1 = result.eval(false, true, true).v();
        assert !eval1;

        /* Evaluate an assignment of variables with x1=0, x2=1, x3=0 */
        boolean eval2 = result.eval(false, true, false).v();
        assert eval2;

        /* Show the created decision diagram with the DotViewer */
        DotViewer<XDD<Boolean>> dotViewer = new DotViewer<>();
        BooleanLogicDDManager booleanLogicDDManager = new BooleanLogicDDManager();
        XDD<Boolean> convertedBDD = result.toXDD(booleanLogicDDManager);
        dotViewer.view(convertedBDD, "");

        /* Dereference all created DDs to release the allocated memory */
        x1.recursiveDeref();
        x2.recursiveDeref();
        x3.recursiveDeref();

        notX1AndX2.recursiveDeref();
        notX1.recursiveDeref();
        notX1AndX3.recursiveDeref();
        result.recursiveDeref();
        convertedBDD.recursiveDeref();

        bddManager.quit();
        booleanLogicDDManager.quit();
    }
}
