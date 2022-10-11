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

import java.util.Map;

import info.scce.addlib.backend.CuddADDBackend;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.profiling.jmh.BenchmarkState;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class CUDDADDBenchmarkState extends BenchmarkState<ADDManager> implements ADDBenchmarkState {

    private ADD add;
    private ADD[] composeVector;
    private double[] randomDoubles;

    @Override
    public ADD getSingleADD() {
        return add;
    }

    @Override
    public ADD[] getComposeVector() {
        return composeVector;
    }

    @Override
    public double[] getValues() {
        return randomDoubles;
    }

    @Override
    public ADDManager getADDManager() {
        return getDDManager();
    }

    @Override
    public void createSingleADD() {
        add = createSingleADD(getDDManager());
    }

    @Override
    public void createComposeVector() {
        composeVector = createComposeVector(getDDManager());
    }

    @Override
    public void createValues() {
        randomDoubles = getValues(seed, size);
    }

    @Override
    public void setup() {
        ADDManager addManager = new ADDManager(new CuddADDBackend());
        setDDManager(addManager);

        Map<String, Runnable> methodsMapping = getMethods();
        String[] initMethods = initData.split(",");
        for (String method : initMethods) {
            if (method.length() > 0) {
                Runnable methodRunner = methodsMapping.get(method);
                assert methodRunner != null;
                methodRunner.run();
            }
        }
    }

    @Override
    public void tearDown() {
        deref();
        super.tearDown();
    }
}
