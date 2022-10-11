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

import java.util.Objects;

import com.google.common.collect.BiMap;
import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

/**
 * The DDManager is one of the main components of creating decision diagrams, manipulating them
 * and release their memory after they are longer necessary.
 * This example illustrates the basic methods and some caveats to look out for.
 */
public final class DDManagerExample {

    private DDManagerExample() {

    }

    public static void main(String[] args) {

        /* Change cudd to sylvan for the Sylvan backend */
        String backendType = "cudd";

        /* Each DDManager requires a backend to work with. Ideally, the backend should be given
         * to the DDManager explicitly to not cause confusion as to which backend is used.
         * The BackendProvider class offers a simple way to pass the demanded backend.
         */
        BDDBackend bddBackend = BackendProvider.getBDDBackend(backendType);

        /* Alternatively, the Backend can be created with one of the constructors, allowing for more parameters */
        // CuddBDDBackend cuddBDDBackend = new CuddBDDBackend(0, 0, 8192);

        BDDManager bddManager = new BDDManager(bddBackend);

        /* New variables can be created with either namedVar or ithVar */
        BDD var1 = bddManager.namedVar("var1");
        BDD var2 = bddManager.namedVar("var2");

        BDD bdd1 = var1.and(var2);
        BDD bdd2 = var2.and(var1);

        /* The order within a DDManager is fixed. If different variable orders are desired,
         * a second DDManager has to be created. Reordering as shown in ReorderingExample also affects
         * all decision diagrams in the DDManager.
         */
        assert Objects.equals(bdd1.readName(), bdd2.readName());

        /* DDManagers map an index and a variable name to each other to allow for the use of both options.
         * The mappings can be accessed with knownVarNames().
         */
        BiMap<String, Integer> knownVarNames = bddManager.knownVarNames();
        assert Objects.equals(knownVarNames.get("var1"), 0);
        assert Objects.equals(knownVarNames.get("var2"), 1);

        /* After the DDs are no longer required, they should be dereferenced so that CUDD/Sylvan can release the memory.
         * It is not possible to access a DD after dereferencing, otherwise it results in a segmentation fault!
         * Due to CUDD and Sylvan working differently regarding DDManagers, it is recommended to not only call quit()
         * on the DDManager but dereference all DDs as well.
         * Further information regarding releasing memory can be found in the documentation.
         */
        var1.recursiveDeref();
        var2.recursiveDeref();
        bdd1.recursiveDeref();
        bdd2.recursiveDeref();

        bddManager.quit();
    }
}
