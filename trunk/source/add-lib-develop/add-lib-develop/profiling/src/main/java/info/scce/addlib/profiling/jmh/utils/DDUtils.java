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
package info.scce.addlib.profiling.jmh.utils;

import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

public final class DDUtils {

    private DDUtils() {}

    public static ADD createADDFromDoubles(ADDManager addManager, double[] values) {
        ADD result = addManager.constant(0.0);
        for (int i = 0; i < values.length; i++) {
            ADD ithVar = addManager.namedVar("var" + i);
            ADD constDD = addManager.constant(values[i]);
            ADD constDD2 = addManager.constant(-1 * values[i]);
            ADD test = ithVar.ite(constDD, constDD2);
            ithVar.recursiveDeref();
            constDD.recursiveDeref();
            constDD2.recursiveDeref();

            ADD resultTemp = result.plus(test);
            result.recursiveDeref();
            test.recursiveDeref();

            result = resultTemp;
        }
        return result;
    }

    public static BDD createBDDFromBooleans(BDDManager bddManager, boolean[] values) {
        BDD result = bddManager.readLogicZero();
        for (int i = 0; i < values.length; i++) {
            BDD ithVar = bddManager.namedVar("var" + i);
            if (values[i]) {
                BDD notVar = ithVar.not();
                ithVar.recursiveDeref();
                ithVar = notVar;
            }
            result = result.or(ithVar);
        }
        return result;
    }
}
