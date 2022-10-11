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
import static info.scce.addlib.cudd.Cudd.*;
@MetaInfServices(BDDBackend.class)
public class CuddBDDBackend extends AbstractCuddBackend implements BDDBackend {

    public CuddBDDBackend() {
        super();
    }

    public CuddBDDBackend(int numVars, int numVarsZ, long maxMemory) {
        super(numVars, numVarsZ, maxMemory);
    }

    public CuddBDDBackend(int numVars, int numVarsZ, int numSlots, int cacheSize, long maxMemory) {
        super(numVars, numVarsZ, numSlots, cacheSize, maxMemory);
    }

    @Override
    public long readOne(long ddManager) {
        return Cudd_ReadOne(ddManager);
    }

    @Override
    public long readLogicZero(long ddManager) {
        return Cudd_ReadLogicZero(ddManager);
    }

    @Override
    public long ithVar(long ddManager, int var) {
        return Cudd_bddIthVar(ddManager, var);
    }

    @Override
    public long newVar(long ddManager) {
        return Cudd_bddNewVar(ddManager);
    }

    @Override
    public long newVarAtLevel(long ddManager, int level) {
        return Cudd_bddNewVarAtLevel(ddManager, level);
    }

    @Override
    public long t(long dd) {
        return Cudd_T(dd);
    }

    @Override
    public long e(long dd) {
        return Cudd_E(dd);
    }

    @Override
    public long eval(long ddManager, long dd, int... input) {
        return Cudd_Eval(ddManager, dd, input);
    }

    @Override
    public long not(long dd) {
        return Cudd_Not(dd);
    }

    @Override
    public long ite(long ddManager, long dd, long t, long e) {
        return Cudd_bddIte(ddManager, dd, t, e);
    }

    @Override
    public long iteLimit(long ddManager, long dd, long t, long e, int limit) {
        return Cudd_bddIteLimit(ddManager, dd, t, e, limit);
    }

    @Override
    public long iteConstant(long ddManager, long dd, long t, long e) {
        return Cudd_bddIteConstant(ddManager, dd, t, e);
    }

    @Override
    public long intersect(long ddManager, long dd, long g) {
        return Cudd_bddIntersect(ddManager, dd, g);
    }

    @Override
    public long and(long ddManager, long dd, long g) {
        return Cudd_bddAnd(ddManager, dd, g);
    }

    @Override
    public long andLimit(long ddManager, long dd, long g, int limit) {
        return Cudd_bddAndLimit(ddManager, dd, g, limit);
    }

    @Override
    public long or(long ddManager, long dd, long g) {
        return Cudd_bddOr(ddManager, dd, g);
    }

    @Override
    public long orLimit(long ddManager, long dd, long g, int limit) {
        return Cudd_bddOrLimit(ddManager, dd, g, limit);
    }

    @Override
    public long nand(long ddManager, long dd, long g) {
        return Cudd_bddNand(ddManager, dd, g);
    }

    @Override
    public long nor(long ddManager, long dd, long g) {
        return Cudd_bddNor(ddManager, dd, g);
    }

    @Override
    public long xor(long ddManager, long dd, long g) {
        return Cudd_bddXor(ddManager, dd, g);
    }

    @Override
    public long xnor(long ddManager, long dd, long g) {
        return Cudd_bddXnor(ddManager, dd, g);
    }

    @Override
    public long xnorLimit(long ddManager, long dd, long g, int limit) {
        return Cudd_bddXnorLimit(ddManager, dd, g, limit);
    }

    @Override
    public int leq(long ddManager, long dd, long g) {
        return Cudd_bddLeq(ddManager, dd, g);
    }

    @Override
    public long compose(long ddManager, long dd, long g, int var) {
        return Cudd_bddCompose(ddManager, dd, g, var);
    }

    @Override
    public long vectorCompose(long ddManager, long dd, long... vector) {
        return Cudd_bddVectorCompose(ddManager, dd, vector);
    }

    @Override
    public int isComplement(long dd) {
        return Cudd_IsComplement(dd);
    }
}
