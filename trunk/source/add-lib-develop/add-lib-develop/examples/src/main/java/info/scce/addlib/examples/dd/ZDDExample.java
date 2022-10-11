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

import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.backend.ZDDBackend;
import info.scce.addlib.dd.zdd.ZDD;
import info.scce.addlib.dd.zdd.ZDDManager;

/**
 * Example for using zero-suppressed decision diagrams with the ADD-Lib.
 * ZDDs are only implemented for CUDD at the moment.
 */
public final class ZDDExample {

    private ZDDExample() {

    }

    public static void main(String[] args) {

        /* Use the CUDD backend */
        String backendType = "cudd";
        ZDDBackend zddBackend = BackendProvider.getZDDBackend(backendType);
        ZDDManager zddManager = new ZDDManager(zddBackend);

        /* Create the variables for the ZDD */
        ZDD x1 = zddManager.namedVar("a1");
        ZDD x2 = zddManager.namedVar("a2");

        /* Create a ZDD based on the formula {{x1} ∪ {x2}} ∩ {x2} */
        ZDD x1UnionX2 = x1.union(x2);
        ZDD result = x1UnionX2.intersect(x2);

        /* Evaluate an assignment of variables with x1=0, x2=1 */
        boolean eval1 = result.eval(false, true).v();
        assert eval1;

        /* Evaluate an assignment of variables with x1=1, x2=0 */
        boolean eval2 = result.eval(true, false).v();
        assert !eval2;

        /* Dereference all created DDs to release the allocated memory */
        x1.recursiveDeref();
        x2.recursiveDeref();
        x1UnionX2.recursiveDeref();
        result.recursiveDeref();

        zddManager.quit();
    }
}
