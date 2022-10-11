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
package info.scce.addlib.dd.xdd.latticedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.xdd.latticedd.BooleanLatticeDDManager;

public class BoundedFuzzyLogicDDManager extends BooleanLatticeDDManager<Double> {

    public BoundedFuzzyLogicDDManager() {
        this(BackendProvider.getADDBackend());
    }

    public BoundedFuzzyLogicDDManager(ADDBackend backend) {
        super(backend);
    }

    @Override
    protected Double meet(Double left, Double right) {
        return ensureRange(left + right - 1);
    }

    @Override
    protected Double join(Double left, Double right) {
        return ensureRange(left + right);
    }

    @Override
    protected Double botElement() {
        return 0.0;
    }

    @Override
    protected Double topElement() {
        return 1.0;
    }

    @Override
    protected Double compl(Double x) {
        return ensureRange(1.0 - x);
    }

    @Override
    public Double parseElement(String str) {
        return ensureRange(Double.parseDouble(str));
    }

    private double ensureRange(double x) {
        if (x > 1.0) {
            return 1.0;
        } else {
            return Math.max(x, 0.0);
        }
    }
}
