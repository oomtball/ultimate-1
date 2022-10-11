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
package info.scce.addlib.profiling.jmh.bdd.states;

import java.util.HashMap;
import java.util.Map;

import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

public interface BDDBenchmarkState {

    default BDD createSingleBDD(BDDManager bddManager, int size) {
        NQueens nQueens = new NQueens(bddManager, size);
        return nQueens.createNQueensBDD();
    }

    default BDD[] createComposeVector(BDDManager bddManager) {
        int numVars = bddManager.knownVarNames().size() + 1;
        BDD[] composeVector = new BDD[numVars];
        composeVector[0] = bddManager.namedVar("var" + (numVars - 1));
        for (int i = 1; i < composeVector.length; i++) {
            composeVector[i] = bddManager.namedVar("var" + i);
        }
        return composeVector;
    }

    default Map<String, Runnable> getMethods() {
        HashMap<String, Runnable> methodMapping = new HashMap<>();
        methodMapping.put("BDD", this::createSingleBDD);
        methodMapping.put("VC", this::createComposeVector);
        return methodMapping;
    }

    default void deref() {
        BDD bdd = getSingleBDD();
        BDD[] composeVector = getComposeVector();

        if (bdd != null) {
            bdd.recursiveDeref();
        }
        if (composeVector != null) {
            for (BDD composeBDD : composeVector) {
                composeBDD.recursiveDeref();
            }
        }
    }

    // Getter methods
    BDD getSingleBDD();

    BDD[] getComposeVector();

    BDDManager getBDDManager();

    int getSize();

    // Initialization methods
    void createComposeVector();

    void createSingleBDD();
}
