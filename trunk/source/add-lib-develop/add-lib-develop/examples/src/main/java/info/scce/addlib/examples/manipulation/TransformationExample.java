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

package info.scce.addlib.examples.manipulation;

import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.XDDManager;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanLogicDDManager;
import info.scce.addlib.dd.xdd.ringlikedd.example.ArithmeticDDManager;

/**
 * Examples for transforming between different data types for decision diagrams.
 * The following transformations are directly possible with built-in functions:
 * ADD and BDD -> XDD
 * XDDs can be transformed to various data types with a transformation function.
 * Transformations for the same data type are shown in ApplyExample.
 */
public final class TransformationExample {

    private TransformationExample() {

    }

    public static void main(String[] args) {

        /* Change cudd to sylvan for the Sylvan backend */
        String backendType = "cudd";

        /* Tested managers */
        BDDManager bddManager = new BDDManager(BackendProvider.getBDDBackend(backendType));
        ADDManager addManager = new ADDManager(BackendProvider.getADDBackend(backendType));

        /* BDD example */
        BDD bdd = bddManager.namedVar("a");

        /* ADD example */
        ADD add = addManager.namedVar("a");

        /* ADDs and BDDs are transformed to XDDs with the toXDD() method. A BooleanLogicDDManager for BDD
         * and an ArithmeticDDManager for ADD is required to store and manage the newly created DD
         * and release the memory once the DD is no longer necessary.
         */
        BooleanLogicDDManager booleanLogicDDManager =
                new BooleanLogicDDManager(BackendProvider.getADDBackend(backendType));
        XDD<Boolean> xddFromBDD = bdd.toXDD(booleanLogicDDManager);

        ArithmeticDDManager arithmeticDDManager = new ArithmeticDDManager(BackendProvider.getADDBackend(backendType));
        XDD<Double> xddFromADD = add.toXDD(arithmeticDDManager);

        /* XDDs hold arbitrary data types and transformation between types is possible with monadicTransform().
         * The method requires an XDDManager for the resulting XDD and a function which maps terminal nodes (leaves)
         * from the input to the output data type.
         */
        XDDManager<String> stringXDDManager = new XDDManager<>(BackendProvider.getADDBackend(backendType));
        XDD<String> stringXDDFromADD = xddFromADD.monadicTransform(stringXDDManager, Object::toString);

        /* Dereference all created DDs to release the allocated memory */
        bdd.recursiveDeref();
        add.recursiveDeref();

        xddFromBDD.recursiveDeref();
        xddFromADD.recursiveDeref();
        stringXDDFromADD.recursiveDeref();

        addManager.quit();
        bddManager.quit();
        booleanLogicDDManager.quit();
        arithmeticDDManager.quit();
    }
}
