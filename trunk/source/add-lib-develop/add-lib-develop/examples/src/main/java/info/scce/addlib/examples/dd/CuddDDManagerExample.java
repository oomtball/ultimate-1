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

import info.scce.addlib.backend.CuddBDDBackend;
import info.scce.addlib.cudd.Cudd;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

/**
 * CUDD is a single-threaded library for decision diagrams.
 * CUDD is older than Sylvan and one of the standard libraries for DDs
 * and offers more methods to manipulate DDs and deal with reordering.
 * Check the Javadoc or documentation for further information on the differences.
 */
public final class CuddDDManagerExample {

    private CuddDDManagerExample() {

    }

    public static void main(String[] args) {
        /* CUDD backends accept the following parameters */

        /* Initial number of variables for BDDs, ADDs and XDDs, usually set to 0 */
        int numVars = 0;

        /* Initial number of ZDD variables, usually set to 0 */
        int numVarsZ = 0;

        /* Initial size of the unique tables used for storing DDs, usually set to 256.
         * Refer to the documentation for further information regarding unique tables.
         * The value is also referenced in Cudd.UNIQUE_SLOTS.
         */
        int numSlots = Cudd.CUDD_UNIQUE_SLOTS;

        /* Size of the cache for DD operations.
         * Should not be too small to be useful or too large to prevent overhead.
         * A value of 262144 usually works well.
         * The value is also referenced in Cudd.CUDD_CACHE_SLOTS.
         */
        int cacheSize = Cudd.CUDD_CACHE_SLOTS;

        /* maxMemory limits the max memory of the manager in bytes.
         * If set to 0, CUDD sets a proper value based on the system.
         */
        long maxMemory = 1024L * 1024L;

        /* Create the backend and manager */
        CuddBDDBackend cuddBDDBackend = new CuddBDDBackend(numVars, numVarsZ, numSlots, cacheSize, maxMemory);
        BDDManager bddManager = new BDDManager(cuddBDDBackend);

        /* Dereference all created DDs to release the allocated memory */
        BDD bdd = bddManager.ithVar(0);
        bdd.recursiveDeref();
        bddManager.quit();
    }
}
