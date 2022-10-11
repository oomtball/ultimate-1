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

import info.scce.addlib.dd.DDReorderingType;
import org.kohsuke.MetaInfServices;
import static info.scce.addlib.cudd.Cudd.*;

@MetaInfServices(ZDDBackend.class)
public class CuddZDDBackend extends AbstractCuddBackend implements ZDDBackend {

    public CuddZDDBackend() {
        super();
    }

    public CuddZDDBackend(int numVars, int numVarsZ, long maxMemory) {
        super(numVars, numVarsZ, maxMemory);
    }

    public CuddZDDBackend(int numVars, int numVarsZ, int numSlots, int cacheSize, long maxMemory) {
        super(numVars, numVarsZ, numSlots, cacheSize, maxMemory);
    }

    @Override
    public long readOne(long ddManager) {
        return Cudd_ReadOne(ddManager);
    }

    @Override
    public long readZddOne(long ddManager, int idx) {
        return Cudd_ReadZddOne(ddManager, idx);
    }

    @Override
    public long readZero(long ddManager) {
        return Cudd_ReadZero(ddManager);
    }

    @Override
    public int readPerm(long ddManager, int idx) {
        return Cudd_ReadPermZdd(ddManager, idx);
    }

    @Override
    public long ithVar(long ddManager, int var) {
        return Cudd_zddIthVar(ddManager, var);
    }

    @Override
    public void ref(long ddManager, long ddPtr) {
        Cudd_Ref(ddPtr);
    }

    @Override
    public void deref(long ddManager, long ddPtr) {
        Cudd_RecursiveDerefZdd(ddManager, ddPtr);
    }

    @Override
    public long t(long ddPtr) {
        return Cudd_T(ddPtr);
    }

    @Override
    public long e(long ddPtr) {
        return Cudd_E(ddPtr);
    }

    @Override
    public long eval(long ddManager, long ddPtr, int... input) {
        return Cudd_Eval(ddManager, ddPtr, input);
    }

    @Override
    public long union(long ddManager, long ddPtr, long g) {
        return Cudd_zddUnion(ddManager, ddPtr, g);
    }

    @Override
    public long intersect(long ddManager, long ddPtr, long g) {
        return Cudd_zddIntersect(ddManager, ddPtr, g);
    }

    @Override
    public long diff(long ddManager, long ddPtr, long g) {
        return Cudd_zddDiff(ddManager, ddPtr, g);
    }

    @Override
    public long change(long ddManager, long ddPtr, int var) {
        return Cudd_zddChange(ddManager, ddPtr, var);
    }

    @Override
    public long subset0(long ddManager, long ddPtr, int var) {
        return Cudd_zddSubset0(ddManager, ddPtr, var);
    }

    @Override
    public long subset1(long ddManager, long ddPtr, int var) {
        return Cudd_zddSubset1(ddManager, ddPtr, var);
    }

    @Override
    public int reorder(long ddManager, DDReorderingType heuristic, int minsize) {
        return Cudd_zddReduceHeap(ddManager, heuristic.cuddReorderingType(), minsize);
    }

    @Override
    public boolean setVariableOrder(long ddManager, int[] permutation) {
        return Cudd_zddShuffleHeap(ddManager, permutation) != 0;
    }

    @Override
    public void enableAutomaticReordering(long ddManager, DDReorderingType heuristic) {
        Cudd_AutodynEnableZdd(ddManager, heuristic.cuddReorderingType());
    }

    @Override
    public void disableAutomaticReordering(long ddManager) {
        Cudd_AutodynDisableZdd(ddManager);
    }

}
