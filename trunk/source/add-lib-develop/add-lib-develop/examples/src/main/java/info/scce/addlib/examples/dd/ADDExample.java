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

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.ringlikedd.example.ArithmeticDDManager;
import info.scce.addlib.viewer.DotViewer;

/**
 * Example for using algebraic decision diagrams with the ADD-Lib.
 * For the sake of compatibility only the CUDD backend is shown,
 * although the backend can be exchanged with the Sylvan one when Linux or Mac OS is used.
 */
public final class ADDExample {

    private ADDExample() {

    }

    public static void main(String[] args) {

        /* Change cudd to sylvan for the sylvan backend */
        String backendType = "cudd";
        ADDBackend addBackend = BackendProvider.getADDBackend(backendType);
        ADDManager addManager = new ADDManager(addBackend);

        /* Create the variables for the ADD */
        ADD x1 = addManager.namedVar("a1");
        ADD x2 = addManager.namedVar("a2");

        /* Create an ADD based on the formula 2 * a1 - 5 * a2 */
        ADD two = addManager.constant(2.0);
        ADD five = addManager.constant(5.0);
        ADD twoX1 = x1.times(two);
        ADD fiveX2 = x2.times(five);
        ADD result = twoX1.minus(fiveX2);

        /* Size of the created ADD */
        int expectedADDSize = 7;
        assert expectedADDSize == result.dagSize();

        /* Evaluate an assignment of variables with x1=0, x2=1 */
        double eval1 = result.eval(false, true).v();
        double expectedEvalResult1 = -5;
        assert expectedEvalResult1 == eval1;

        /* Evaluate an assignment of variables with x1=1, x2=1 */
        double eval2 = result.eval(true, true).v();
        double expectedEvalResult2 = -3;
        assert expectedEvalResult2 == eval2;

        /* Show the created decision diagram with the DotViewer */
        DotViewer<XDD<Double>> dotViewer = new DotViewer<>();
        ArithmeticDDManager arithmeticDDManager = new ArithmeticDDManager();
        XDD<Double> convertedADD = result.toXDD(arithmeticDDManager);
        dotViewer.view(convertedADD, "");

        /* Dereference all created DDs to release the allocated memory */
        x1.recursiveDeref();
        x2.recursiveDeref();

        two.recursiveDeref();
        five.recursiveDeref();
        twoX1.recursiveDeref();
        fiveX2.recursiveDeref();
        result.recursiveDeref();
        convertedADD.recursiveDeref();

        addManager.quit();
        arithmeticDDManager.quit();
    }
}
