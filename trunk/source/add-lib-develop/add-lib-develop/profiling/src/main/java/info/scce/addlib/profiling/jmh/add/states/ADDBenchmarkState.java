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
package info.scce.addlib.profiling.jmh.add.states;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.profiling.jmh.utils.DDUtils;

public interface ADDBenchmarkState {

    default ADD createSingleADD(ADDManager addManager) {
        return DDUtils.createADDFromDoubles(addManager, getValues());
    }

    default ADD[] createComposeVector(ADDManager addManager) {
        int numVars = addManager.knownVarNames().size() + 1;
        ADD[] composeVector = new ADD[numVars];
        composeVector[0] = addManager.namedVar("var" + (numVars - 1));
        for (int i = 1; i < composeVector.length; i++) {
            composeVector[i] = addManager.namedVar("var" + i);
        }
        return composeVector;
    }

    default double[] getValues(long seed, int size) {
        Random r = new Random(seed);
        double rangeMax = 1 << 14;
        double rangeMin = rangeMax * -1;
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = generateRandomDouble(r, rangeMax, rangeMin);
        }
        return values;
    }

    default double generateRandomDouble(Random r, double rangeMax, double rangeMin) {
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    default Map<String, Runnable> getMethods() {
        Map<String, Runnable> methodMapping = new HashMap<>();
        methodMapping.put("V", this::createValues);
        methodMapping.put("ADD", this::createSingleADD);
        methodMapping.put("VC", this::createComposeVector);
        return methodMapping;
    }

    default void deref() {
        ADD add = getSingleADD();
        ADD[] composeVector = getComposeVector();

        if (composeVector != null) {
            for (ADD composeADD : composeVector) {
                composeADD.recursiveDeref();
            }
        }
        if (add != null) {
            add.recursiveDeref();
        }
    }

    // Getter methods
    ADD getSingleADD();

    ADD[] getComposeVector();

    double[] getValues();

    ADDManager getADDManager();

    // Initialization methods
    void createComposeVector();

    void createSingleADD();

    void createValues();
}


