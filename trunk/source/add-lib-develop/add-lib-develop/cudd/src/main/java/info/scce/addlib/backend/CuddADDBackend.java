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
import info.scce.addlib.cudd.DD_AOP;
import info.scce.addlib.cudd.DD_MAOP;
import org.kohsuke.MetaInfServices;
import static info.scce.addlib.cudd.Cudd.*;

@MetaInfServices(ADDBackend.class)
public class CuddADDBackend extends AbstractCuddBackend implements ADDBackend {

    public CuddADDBackend() {
        super();
    }

    public CuddADDBackend(int numVars, int numVarsZ, long maxMemory) {
        super(numVars, numVarsZ, maxMemory);
    }

    public CuddADDBackend(int numVars, int numVarsZ, int numSlots, int cacheSize, long maxMemory) {
        super(numVars, numVarsZ, numSlots, cacheSize, maxMemory);
    }

    @Override
    public double readEpsilon(long ddManager) {
        return Cudd_ReadEpsilon(ddManager);
    }

    @Override
    public void setEpsilon(long ddManager, double epsilon) {
        Cudd_SetEpsilon(ddManager, epsilon);
    }

    @Override
    public void setBackground(long ddManager, long background) {
        Cudd_SetBackground(ddManager, background);
    }

    @Override
    public long readOne(long ddManager) {
        return Cudd_ReadOne(ddManager);
    }

    @Override
    public long readZero(long ddManager) {
        return Cudd_ReadZero(ddManager);
    }

    @Override
    public long readPlusInfinity(long ddManager) {
        return Cudd_ReadPlusInfinity(ddManager);
    }

    @Override
    public long readMinusInfinity(long ddManager) {
        return Cudd_ReadMinusInfinity(ddManager);
    }

    @Override
    public long constant(long ddManager, double value) {
        return Cudd_addConst(ddManager, value);
    }

    @Override
    public long ithVar(long ddManager, int idx) {
        return Cudd_addIthVar(ddManager, idx);
    }

    @Override
    public long xddIthVar(long ddManager, int idx) {
        return ithVar(ddManager, idx);
    }

    @Override
    public long newVar(long ddManager) {
        return Cudd_addNewVar(ddManager);
    }

    @Override
    public long newVarAtLevel(long ddManager, int level) {
        return Cudd_addNewVarAtLevel(ddManager, level);
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
    public double v(long dd) {
        return Cudd_V(dd);
    }

    @Override
    public long ite(long ddManager, long dd, long t, long e) {
        return Cudd_addIte(ddManager, dd, t, e);
    }

    @Override
    public long iteConstant(long ddManager, long dd, long t, long e) {
        return Cudd_addIteConstant(ddManager, dd, t, e);
    }

    @Override
    public long evalConst(long ddManager, long dd, long g) {
        return Cudd_addEvalConst(ddManager, dd, g);
    }

    @Override
    public long cmpl(long ddManager, long dd) {
        return Cudd_addCmpl(ddManager, dd);
    }

    @Override
    public int leq(long ddManager, long dd, long g) {
        return Cudd_addLeq(ddManager, dd, g);
    }

    @Override
    public long compose(long ddManager, long dd, long g, int var) {
        return Cudd_addCompose(ddManager, dd, g, var);
    }

    @Override
    public long vectorComposeADD(long ddManager, long dd, long... vector) {
        return Cudd_addVectorCompose(ddManager, dd, vector);
    }

    @Override
    public long plus(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addPlus, dd, g);
    }

    @Override
    public long times(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addTimes, dd, g);
    }

    @Override
    public long threshold(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addThreshold, dd, g);
    }

    @Override
    public long setNZ(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addSetNZ, dd, g);
    }

    @Override
    public long divide(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addDivide, dd, g);
    }

    @Override
    public long minus(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addMinus, dd, g);
    }

    @Override
    public long minimum(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addMinimum, dd, g);
    }

    @Override
    public long maximum(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addMaximum, dd, g);
    }

    @Override
    public long oneZeroMaximum(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addOneZeroMaximum, dd, g);
    }

    @Override
    public long diff(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addDiff, dd, g);
    }

    @Override
    public long agreement(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addAgreement, dd, g);
    }

    @Override
    public long or(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addOr, dd, g);
    }

    @Override
    public long nand(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addNand, dd, g);
    }

    @Override
    public long nor(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addNor, dd, g);
    }

    @Override
    public long xor(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addXor, dd, g);
    }

    @Override
    public long xnor(long ddManager, long dd, long g) {
        return Cudd_addApply(ddManager, DD_AOP.Cudd_addXnor, dd, g);
    }

    @Override
    public long log(long ddManager, long dd) {
        return Cudd_addMonadicApply(ddManager, DD_MAOP.Cudd_addLog, dd);
    }

    @Override
    public long apply(long ddManager, DD_AOP_Fn aop, long dd, long g) {
        return Cudd_addApply(ddManager, aop, dd, g);
    }

    @Override
    public long monadicApply(long ddManager, DD_MAOP_Fn maop, long ptr) {
        return Cudd_addMonadicApply(ddManager, maop, ptr);
    }

    @Override
    public long invalid() {
        return 0;
    }
}
