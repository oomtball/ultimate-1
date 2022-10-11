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
package info.scce.addlib.dd.xdd.ringlikedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.xdd.ringlikedd.FieldDDManager;

public class ArithmeticDDManager extends FieldDDManager<Double> {

    public ArithmeticDDManager() {
        this(BackendProvider.getADDBackend());
    }

    public ArithmeticDDManager(ADDBackend backend) {
        super(backend);
    }

    @Override
    protected Double zeroElement() {
        return 0.0;
    }

    @Override
    protected Double oneElement() {
        return 1.0;
    }

    @Override
    protected Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    protected Double addInverse(Double a) {
        return -a;
    }

    @Override
    protected Double mult(Double a, Double b) {
        return a * b;
    }

    @Override
    protected Double multInverse(Double a) {
        return 1.0 / a;
    }

    @Override
    protected Double inf(Double a, Double b) {
        return Math.min(a, b);
    }

    @Override
    protected Double sup(Double a, Double b) {
        return Math.max(a, b);
    }

    @Override
    public Double parseElement(String input) {
        return Double.parseDouble(input);
    }
}
