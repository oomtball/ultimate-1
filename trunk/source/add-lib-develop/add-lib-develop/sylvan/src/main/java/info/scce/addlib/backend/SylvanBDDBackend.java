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

import org.kohsuke.MetaInfServices;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_eval;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_false;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_gethigh;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_getlow;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_ithvar;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_leq;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_true;
import static info.scce.addlib.sylvan.Sylvan.sylvan_and;
import static info.scce.addlib.sylvan.Sylvan.sylvan_compose;
import static info.scce.addlib.sylvan.Sylvan.sylvan_ite;
import static info.scce.addlib.sylvan.Sylvan.sylvan_nand;
import static info.scce.addlib.sylvan.Sylvan.sylvan_nor;
import static info.scce.addlib.sylvan.Sylvan.sylvan_not;
import static info.scce.addlib.sylvan.Sylvan.sylvan_or;
import static info.scce.addlib.sylvan.Sylvan.sylvan_vector_compose;
import static info.scce.addlib.sylvan.Sylvan.sylvan_xor;
import static info.scce.addlib.sylvan.Sylvan.unsupportedMethod;

@SuppressWarnings("PMD.TooManyStaticImports")
@MetaInfServices(BDDBackend.class)
public class SylvanBDDBackend extends AbstractSylvanBackend implements BDDBackend {

    public SylvanBDDBackend() {
        super();
    }

    public SylvanBDDBackend(int numWorkers, long memorycap, int tableRatio, int initialRatio) {
        super(numWorkers, memorycap, tableRatio, initialRatio);
    }

    @Override
    public long readOne(long ddManager) {
        return mtbdd_true();
    }

    @Override
    public long readLogicZero(long ddManager) {
        return mtbdd_false();
    }

    @Override
    public long ithVar(long ddManager, int var) {
        return mtbdd_ithvar(var);
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
    public long not(long dd) {
        return sylvan_not(dd);
    }

    @Override
    public long ite(long ddManager, long dd, long t, long e) {
        return sylvan_ite(dd, t, e);
    }

    @Override
    public long iteLimit(long ddManager, long dd, long t, long e, int limit) {
        throw new UnsupportedOperationException(unsupportedMethod("iteLimit"));
    }

    @Override
    public long iteConstant(long ddManager, long dd, long t, long e) {
        throw new UnsupportedOperationException(unsupportedMethod("iteConstant"));
    }

    @Override
    public long intersect(long ddManager, long dd, long g) {
        throw new UnsupportedOperationException(unsupportedMethod("intersect"));
    }

    @Override
    public long and(long ddManager, long dd, long g) {
        return sylvan_and(dd, g);
    }

    @Override
    public long andLimit(long ddManager, long dd, long g, int limit) {
        throw new UnsupportedOperationException("andLimit");
    }

    @Override
    public long or(long ddManager, long dd, long g) {
        return sylvan_or(dd, g);
    }

    @Override
    public long orLimit(long ddManager, long dd, long g, int limit) {
        throw new UnsupportedOperationException(unsupportedMethod("orLimit"));
    }

    @Override
    public long nand(long ddManager, long dd, long g) {
        return sylvan_nand(dd, g);
    }

    @Override
    public long nor(long ddManager, long dd, long g) {
        return sylvan_nor(dd, g);
    }

    @Override
    public long xor(long ddManager, long dd, long g) {
        return sylvan_xor(dd, g);
    }

    @Override
    public long xnor(long ddManager, long dd, long g) {
        throw new UnsupportedOperationException(unsupportedMethod("xnor"));
    }

    @Override
    public long xnorLimit(long ddManager, long dd, long g, int limit) {
        throw new UnsupportedOperationException(unsupportedMethod("xnorLimit"));
    }

    @Override
    public int leq(long ddManager, long dd, long g) {
        return mtbdd_leq(dd, g);
    }

    @Override
    public long compose(long ddManager, long dd, long g, int var) {
        return sylvan_compose(dd, g, var);
    }

    @Override
    public long vectorCompose(long ddManager, long dd, long... vector) {
        return sylvan_vector_compose(dd, vector);
    }

    @Override
    public int isComplement(long dd) {
        return 0;
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
