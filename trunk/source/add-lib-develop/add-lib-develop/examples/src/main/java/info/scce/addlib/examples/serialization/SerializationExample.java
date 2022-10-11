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

package info.scce.addlib.examples.serialization;

import java.util.Objects;

import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.XDDManager;
import info.scce.addlib.dd.xdd.ringlikedd.example.ArithmeticDDManager;
import info.scce.addlib.serializer.DDProperty;
import info.scce.addlib.serializer.XDDSerializer;

/**
 * To preserve larger decision diagrams for later use, XDDSerializer from info.scce.addlib.serializer can be used.
 * Currently, serialization is only implemented for XDDs, although transformation to XDDs makes it possible to serialize
 * BDDs and ADDs as well.
 */
public final class SerializationExample {

    private SerializationExample() {

    }

    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String[] args) {

        /* Change cudd to sylvan for the Sylvan backend */
        String backendType = "cudd";

        /* Tested managers */
        XDDManager<Double> xddManager = new ArithmeticDDManager(BackendProvider.getADDBackend(backendType));

        /* XDD example */
        XDD<Double> xdd = xddManager.namedVar("a");

        /* Serialize the DD to a string. The class also offers the option to serialize to standard output or file. */
        XDDSerializer<Double> xddSerializer = new XDDSerializer<>();
        String serializedDD = xddSerializer.serialize(xdd);

        System.out.println(serializedDD);

        /* Deserialize the string to a DD from the string. The method requires a DDManager to import the DD into
         * and an argument for the DD property which is used for the creation of the variables.
         * Further information regarding DDProperty can be found in the Javadoc or in the documentation.
         */
        XDD<Double> xddDeserialized = xddSerializer.deserialize(xddManager, serializedDD, DDProperty.VARNAME);
        assert Objects.equals(xdd.readName(), xddDeserialized.readName());

        /* Dereference all created DDs to release the allocated memory */
        xdd.recursiveDeref();
        xddManager.quit();
    }
}
