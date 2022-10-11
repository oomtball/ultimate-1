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
import static info.scce.addlib.cudd.Cudd.*;

@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class AbstractCuddBackend implements Backend {

    private int numVars;
    private int numVarsZ;
    private int numSlots;
    private int cacheSize;
    private long maxMemory;

    public AbstractCuddBackend(int numVars, int numVarsZ, int numSlots, int cacheSize, long maxMemory) {
        this.numVars = numVars;
        this.numVarsZ = numVarsZ;
        this.numSlots = numSlots;
        this.cacheSize = cacheSize;
        this.maxMemory = maxMemory;
    }

    public AbstractCuddBackend(int numVars, int numVarsZ, long maxMemory) {
        this(numVars, numVarsZ, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, maxMemory);
    }

    public AbstractCuddBackend() {
        this(0, 0, CUDD_UNIQUE_SLOTS, CUDD_CACHE_SLOTS, 0);
    }

    @Override
    public String getId() {
        return "cudd";
    }

    @Override
    public boolean isAvailable() {
        return AVAILABLE;
    }

    @Override
    public long init() {
        return Cudd_Init(this.numVars, this.numVarsZ, this.numSlots, this.cacheSize, this.maxMemory);
    }

    @Override
    public void quit(long ddManager) {
        Cudd_Quit(ddManager);
    }

    @Override
    public int reorder(long ddMangerPtr, DDReorderingType heuristic, int minsize) {
        return Cudd_ReduceHeap(ddMangerPtr, heuristic.cuddReorderingType(), minsize);
    }

    @Override
    public long getNumRefs(long ddManager) {
        return Cudd_CheckZeroRef(ddManager);
    }

    @Override
    public int readPerm(long ddManager, int idx) {
        return Cudd_ReadPerm(ddManager, idx);
    }

    @Override
    public void ref(long dd) {
        Cudd_Ref(dd);
    }

    @Override
    public void deref(long ddManager, long dd) {
        Cudd_RecursiveDeref(ddManager, dd);
    }

    @Override
    public long regularPtr(long dd) {
        return Cudd_Regular(dd);
    }

    @Override
    public int readIndex(long dd) {
        return Cudd_NodeReadIndex(dd);
    }

    @Override
    public int isConstant(long dd) {
        return Cudd_IsConstant(dd);
    }

    @Override
    public long dagSize(long dd) {
        return Cudd_DagSize(dd);
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
    public boolean setVariableOrder(long ddManager, int[] permutation) {
        return Cudd_ShuffleHeap(ddManager, permutation) != 0;
    }

    @Override
    public void enableAutomaticReordering(long ddManager, DDReorderingType heuristic) {
        Cudd_AutodynEnable(ddManager, heuristic.cuddReorderingType());
    }

    @Override
    public void disableAutomaticReordering(long ddManager) {
        Cudd_AutodynDisable(ddManager);
    }

    @Override
    public void setNextReordering(long ddManager, int count) {
        Cudd_SetNextReordering(ddManager, count);
    }

}
