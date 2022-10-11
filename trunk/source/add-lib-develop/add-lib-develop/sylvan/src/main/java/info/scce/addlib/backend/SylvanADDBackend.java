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
import org.kohsuke.MetaInfServices;
import static info.scce.addlib.sylvan.Sylvan.*;

@SuppressWarnings("PMD.TooManyStaticImports")
@MetaInfServices(ADDBackend.class)
public class SylvanADDBackend extends AbstractSylvanBackend implements ADDBackend {

    public SylvanADDBackend() {
        super();
    }

    public SylvanADDBackend(int numWorkers, long memorycap, int tableRatio, int initialRatio) {
        super(numWorkers, memorycap, tableRatio, initialRatio);
    }

    @Override
    public double readEpsilon(long ddManager) {
        throw new UnsupportedOperationException("readEpsilon");
    }

    @Override
    public void setEpsilon(long ddManager, double epsilon) {
        throw new UnsupportedOperationException("setEpsilon");
    }

    @Override
    public void setBackground(long ddManager, long background) {
        throw new UnsupportedOperationException("setBackground");
    }

    @Override
    public long readOne(long ddManager) {
        return mtbdd_double(1.0);
    }

    @Override
    public long readZero(long ddManager) {
        return mtbdd_double(0.0);
    }

    @Override
    public long readPlusInfinity(long ddManager) {
        throw new UnsupportedOperationException(unsupportedMethod("readPlusInfinity"));
    }

    @Override
    public long readMinusInfinity(long ddManager) {
        throw new UnsupportedOperationException(unsupportedMethod("readMinusInfinity"));
    }

    @Override
    public long constant(long ddManager, double value) {
        return mtbdd_double(value);
    }

    @Override
    public long ithVar(long ddManager, int idx) {
        return mtbdd_makenode(idx, readZero(ddManager), readOne(ddManager));
    }

    @Override
    public long xddIthVar(long ddManager, int idx) {
        return mtbdd_ithvar(idx);
    }

    @Override
    public long newVar(long ddManager) {
        throw new UnsupportedOperationException(unsupportedMethod("newVar"));
    }

    @Override
    public long newVarAtLevel(long ddManager, int level) {
        throw new UnsupportedOperationException(unsupportedMethod("newVarAtLevel"));
    }

    @Override
    public double v(long dd) {
        return mtbdd_getdouble(dd);
    }

    @Override
    public long ite(long ddManager, long dd, long t, long e) {
        return mtbdd_ite(dd, t, e);
    }

    @Override
    public long iteConstant(long ddManager, long dd, long t, long e) {
        throw new UnsupportedOperationException(unsupportedMethod("iteConstant"));
    }

    @Override
    public long evalConst(long ddManager, long dd, long g) {
        throw new UnsupportedOperationException(unsupportedMethod("evalConst"));
    }

    @Override
    public long cmpl(long ddManager, long dd) {
        return mtbdd_cmpl(dd);
    }

    @Override
    public int leq(long ddManager, long dd, long g) {
        return mtbdd_leq(dd, g);
    }

    @Override
    public long compose(long ddManager, long dd, long g, int var) {
        return mtbdd_compose(dd, g, var);
    }

    @Override
    public long vectorComposeADD(long ddManager, long dd, long... vector) {
        return mtbdd_vector_compose(dd, vector);
    }

    @Override
    public long plus(long ddManager, long dd, long g) {
        return mtbdd_plus(dd, g);
    }

    @Override
    public long times(long ddManager, long dd, long g) {
        return mtbdd_times(dd, g);
    }

    @Override
    public long threshold(long ddManager, long dd, long g) {
        return mtbdd_threshold_double_dleaf(dd, g);
    }

    @Override
    public long setNZ(long ddManager, long dd, long g) {
        throw new UnsupportedOperationException(unsupportedMethod("setNZ"));
    }

    @Override
    public long divide(long ddManager, long dd, long g) {
        return mtbdd_divide(dd, g);
    }

    @Override
    public long minus(long ddManager, long dd, long g) {
        return mtbdd_minus(dd, g);
    }

    @Override
    public long minimum(long ddManager, long dd, long g) {
        return mtbdd_min(dd, g);
    }

    @Override
    public long maximum(long ddManager, long dd, long g) {
        return mtbdd_max(dd, g);
    }

    @Override
    public long oneZeroMaximum(long ddManager, long dd, long g) {
        return mtbdd_one_zero_maximum(dd, g);
    }

    @Override
    public long diff(long ddManager, long dd, long g) {
        throw new UnsupportedOperationException(unsupportedMethod("diff"));
    }

    @Override
    public long agreement(long ddManager, long dd, long g) {
        throw new UnsupportedOperationException(unsupportedMethod("agreement"));
    }

    @Override
    public long or(long ddManager, long dd, long g) {
        return mtbdd_or(dd, g);
    }

    @Override
    public long nand(long ddManager, long dd, long g) {
        return mtbdd_nand(dd, g);
    }

    @Override
    public long nor(long ddManager, long dd, long g) {
        return mtbdd_nor(dd, g);
    }

    @Override
    public long xor(long ddManager, long dd, long g) {
        return mtbdd_xor(dd, g);
    }

    @Override
    public long xnor(long ddManager, long dd, long g) {
        return mtbdd_xnor(dd, g);
    }

    @Override
    public long log(long ddManager, long dd) {
        return mtbdd_log(dd);
    }

    @Override
    public long apply(long ddManager, DD_AOP_Fn aop, long dd, long g) {
        return mtbdd_addApply(aop, dd, g);
    }

    @Override
    public long monadicApply(long ddManager, DD_MAOP_Fn maop, long ptr) {
        return mtbdd_addMonadicApply(maop, ptr);
    }

    @Override
    public long invalid() {
        return mtbdd_invalid();
    }

    @Override
    public long t(long dd) {
        return mtbdd_gethigh(dd);
    }

    @Override
    public long e(long dd) {
        return mtbdd_getlow(dd);
    }

    @Override
    public long eval(long ddManager, long dd, int... input) {
        return mtbdd_eval(dd, input);
    }
}
