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

import info.scce.addlib.backend.SylvanBDDBackend;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

/**
 * Sylvan is a multithreaded library for decision diagrams.
 * Not all types of decision diagrams and methods from CUDD are available in Sylvan.
 * Check the Javadoc or documentation for further information on the differences.
 */
public final class SylvanDDManagerExample {

    private SylvanDDManagerExample() {

    }

    public static void main(String[] args) {
        /* Sylvan backends accept the following parameters */

        /* numWorkers controls the number of used workers for the manager.
         * When set to 0, Sylvan automatically allocates them based on the system.
         */
        int numWorkers = 4;

        /* memoryCap limits the max memory of the manager in bytes */
        long memoryCap = 1024L * 1024L;

        /* tableRatio denotes the ratio of the nodes table to the operation cache, a value of 1 usually works well */
        int tableRatio = 1;

        /* initialRatio controls how many times the garbage collector can double the size of the table.
         * The initial size is 1/(2^initialRatio).
         * Large values might result in larger runtimes but less memory used.
         */
        int initialRatio = 5;

        /* The number of workers has to be set with the initialization of the backend and cannot be changed later */
        SylvanBDDBackend sylvanBDDBackend = new SylvanBDDBackend(numWorkers, memoryCap, tableRatio, initialRatio);

        /* Sylvan, contrary to CUDD, only uses one manager for the DDs */
        BDDManager bddManager1 = new BDDManager(sylvanBDDBackend);
        BDDManager bddManager2 = new BDDManager(sylvanBDDBackend);

        /* This has implications on memory management. When calling quit on a DDManager,
         * the associated memory is not released until quit has been called on ALL DDManagers!
         */
        BDD bdd1 = bddManager1.ithVar(0);
        BDD bdd2 = bddManager2.ithVar(0);
        bddManager1.quit();

        /* Crashes for CUDD but not for Sylvan, indicating that the memory is still used */
        bdd1.dagSize();

        /* If both CUDD and Sylvan are used or tested, DDs should be dereferenced as well when not using them anymore */
        bdd1.recursiveDeref();
        bdd2.recursiveDeref();
        bddManager2.quit();
    }
}
