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

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.ringlikedd.example.ComplexNumber;
import info.scce.addlib.dd.xdd.ringlikedd.example.ComplexNumberDDManager;
import info.scce.addlib.viewer.DotViewer;

/**
 * Example for using custom algebraic decision diagrams with the ADD-Lib.
 * The classes in the info.scce.addlib.dd.xdd package of the core module
 * can be extended to create custom decision diagrams.
 * For the sake of compatibility only the CUDD backend is shown,
 * although the backend can be exchanged with the Sylvan one when Linux or Mac OS is used.
 */
public final class XDDExample {

    private XDDExample() {

    }

    public static void main(String[] args) {

        /* Change cudd to sylvan for the Sylvan backend */
        String backendType = "cudd";

        /* Manager for ADDs and XDDs use the same backends */
        ADDBackend xddBackend = BackendProvider.getADDBackend(backendType);
        ComplexNumberDDManager xddManager = new ComplexNumberDDManager(xddBackend);

        /* Create the variables for the BDD */
        XDD<ComplexNumber> x1 = xddManager.namedVar("a1");
        XDD<ComplexNumber> x2 = xddManager.namedVar("a2");

        /* Create a XDD based on the formula (2.0 + 2.5i) * a1 + (5.0 - 1.5i) * a2 */
        ComplexNumber complex1 = new ComplexNumber(2.0, 2.5);
        ComplexNumber complex2 = new ComplexNumber(5.0, -1.5);

        XDD<ComplexNumber> complex1XDD = xddManager.constant(complex1);
        XDD<ComplexNumber> complex2XDD = xddManager.constant(complex2);

        XDD<ComplexNumber> val1 = x1.mult(complex1XDD);
        XDD<ComplexNumber> val2 = x2.mult(complex2XDD);
        XDD<ComplexNumber> result = val1.add(val2);

        /* Size of the created BDD */
        int expectedXDDSize = 7;
        assert expectedXDDSize == result.dagSize();

        /* Evaluate an assignment of variables with x1=0, x2=1 */
        ComplexNumber eval1 = result.eval(false, true).v();
        ComplexNumber expectedEvalResult1 = new ComplexNumber(5, -1.5);
        assert Objects.equals(expectedEvalResult1, eval1);

        /* Evaluate an assignment of variables with x1=1, x2=1 */
        ComplexNumber eval2 = result.eval(true, true).v();
        ComplexNumber expectedEvalResult2 = new ComplexNumber(7, 1);
        assert Objects.equals(expectedEvalResult2, eval2);

        /* Show the created decision diagram with the DotViewer */
        DotViewer<XDD<ComplexNumber>> dotViewer = new DotViewer<>();
        dotViewer.view(result, "");

        /* Dereference all created DDs to release the allocated memory */
        x1.recursiveDeref();
        x2.recursiveDeref();

        complex1XDD.recursiveDeref();
        complex2XDD.recursiveDeref();
        val1.recursiveDeref();
        val2.recursiveDeref();
        result.recursiveDeref();

        xddManager.quit();
    }
}
