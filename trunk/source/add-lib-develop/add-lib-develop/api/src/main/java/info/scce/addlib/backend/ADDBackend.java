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
package info.scce.addlib.backend;

import info.scce.addlib.apply.DD_AOP_Fn;
import info.scce.addlib.apply.DD_MAOP_Fn;

public interface ADDBackend extends Backend {

    /* ADDManager methods */
    double readEpsilon(long ddManager);

    void setEpsilon(long ddManager, double epsilon);

    void setBackground(long ddManager, long background);

    long readOne(long ddManager);

    long readZero(long ddManager);

    long readPlusInfinity(long ddManager);

    long readMinusInfinity(long ddManager);

    long constant(long ddManager, double value);

    long ithVar(long ddManager, int idx);

    long xddIthVar(long ddManager, int idx);

    long newVar(long ddManager);

    long newVarAtLevel(long ddManager, int level);

    /* ADD methods */
    @Override
    long t(long dd);

    @Override
    long e(long dd);

    @Override
    long eval(long ddManager, long dd, int... input);

    double v(long dd);

    long ite(long ddManager, long dd, long t, long e);

    long iteConstant(long ddManager, long dd, long t, long e);

    long evalConst(long ddManager, long dd, long g);

    long cmpl(long ddManager, long dd);

    int leq(long ddManager, long dd, long g);

    long compose(long ddManager, long dd, long g, int var);

    long vectorComposeADD(long ddManager, long dd, long... vector);

    long plus(long ddManager, long dd, long g);

    long times(long ddManager, long dd, long g);

    long threshold(long ddManager, long dd, long g);

    long setNZ(long ddManager, long dd, long g);

    long divide(long ddManager, long dd, long g);

    long minus(long ddManager, long dd, long g);

    long minimum(long ddManager, long dd, long g);

    long maximum(long ddManager, long dd, long g);

    long oneZeroMaximum(long ddManager, long dd, long g);

    long diff(long ddManager, long dd, long g);

    long agreement(long ddManager, long dd, long g);

    long or(long ddManager, long dd, long g);

    long nand(long ddManager, long dd, long g);

    long nor(long ddManager, long dd, long g);

    long xor(long ddManager, long dd, long g);

    long xnor(long ddManager, long dd, long g);

    long log(long ddManager, long dd);

    /* Apply methods */
    long apply(long ddManager, DD_AOP_Fn aop, long dd, long g);

    long monadicApply(long ddManager, DD_MAOP_Fn maop, long ptr);

    long invalid();
}
