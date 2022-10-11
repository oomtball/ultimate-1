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

package info.scce.addlib.examples.visualization;

import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.codegenerator.AsciiArtGenerator;
import info.scce.addlib.codegenerator.AsciiArtTreeGenerator;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.XDDManager;
import info.scce.addlib.dd.xdd.ringlikedd.example.ArithmeticDDManager;
import info.scce.addlib.viewer.DotViewer;

/**
 * Illustrates how a DD can be visualized. The following options are available:
 * <ul>
 * <li>DotViewer from info.scce.addlib.viewer</li>
 * <li>AsciiArtGenerator from info.scce.addlib.codegenerator.AsciiArtGenerator</li>
 * <li>AsciiArtTreeGenerator from info.scce.addlib.codegenerator.AsciiArtGenerator</li>
 * </ul>
 */
public final class ViewerExample {

    private ViewerExample() {

    }

    public static void main(String[] args) {

        /* Change cudd to sylvan for the Sylvan backend */
        String backendType = "cudd";

        /* Tested managers */
        XDDManager<Double> xddManager = new ArithmeticDDManager(BackendProvider.getADDBackend(backendType));

        /* XDD example */
        XDD<Double> xdd = xddManager.namedVar("a");

        /* DotViewer requires an XDD to work. BDDExample and ADDExample, as well as TransformationExample,
         * show how BDDs and ADDs can be transformed to XDD when it is necessary to visualize these types of DDs.
         */
        DotViewer<XDD<Double>> dotViewer = new DotViewer<>();
        dotViewer.view(xdd, "label");

        /* With waitUntilAllClosed() the execution of the program can be interrupted
         * until all opened windows are closed.
         */
        // dotViewer.waitUntilAllClosed();

        /* When console or file output is preferred, the AsciiArtGenerator or AsciiArtTreeGenerator can be used.
         * The classes offer many options for the output to be processed, e.g. via standard output.
         */
        AsciiArtGenerator<XDD<Double>> asciiArtGenerator = new AsciiArtGenerator<>();
        asciiArtGenerator.generateToStdOut(xdd, "label");

        AsciiArtTreeGenerator<XDD<Double>> asciiArtTreeGenerator = new AsciiArtTreeGenerator<>();
        asciiArtTreeGenerator.generateToStdOut(xdd, "label");

        /* Dereference all created DDs to release the allocated memory */
        xdd.recursiveDeref();
        xddManager.quit();
    }
}
