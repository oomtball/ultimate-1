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
package info.scce.addlib.cudd;

import info.scce.addlib.apply.DD_AOP_Fn;
import info.scce.addlib.apply.DD_MAOP_Fn;
import info.scce.addlib.nativelib.NativeDependencyError;
import info.scce.addlib.nativelib.NativeLibraryLoader;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.AvoidSynchronizedAtMethodLevel", "PMD.ExcessiveClassLength"})
public final class Cudd {

    /* Load native library dependencies on first usage */
    public static final int CUDD_UNIQUE_SLOTS = 256;
    public static final int CUDD_CACHE_SLOTS = 262144;

    public static final boolean AVAILABLE;

    static {
        boolean available = true;

        try {
            NativeLibraryLoader.loadNativeLibrary(Cudd.class, "libaddlib-cudd");
        } catch (NativeDependencyError e) {
            e.printStackTrace();
            available = false;
        }

        AVAILABLE = available;
    }

    private Cudd() {
    }

    /* CUDD methods */
    private static native long native_Cudd_Complement(long node);

    public static synchronized long Cudd_Complement(long node) {
        return native_Cudd_Complement(node);
    }

    private static native int native_Cudd_IsComplement(long node);

    public static synchronized int Cudd_IsComplement(long node) {
        return native_Cudd_IsComplement(node);
    }

    public static synchronized long Cudd_Not(long node) {
        return native_Cudd_Not(node);
    }

    private static native long native_Cudd_Not(long node);

    public static synchronized long Cudd_Init(int numVars, int numVarsZ, int numSlots, int cacheSize, long maxMemory) {
        return native_Cudd_Init(numVars, numVarsZ, numSlots, cacheSize, maxMemory);
    }

    private static native long native_Cudd_Init(int numVars, int numVarsZ, int numSlots, int cacheSize, long maxMemory);

    public static synchronized double Cudd_ReadEpsilon(long ddManager) {
        return native_Cudd_ReadEpsilon(ddManager);
    }

    private static native double native_Cudd_ReadEpsilon(long ddManager);

    public static synchronized void Cudd_SetEpsilon(long ddManager, double epsilon) {
        native_Cudd_SetEpsilon(ddManager, epsilon);
    }

    private static native void native_Cudd_SetEpsilon(long ddManager, double epsilon);

    public static synchronized long Cudd_addConst(long ddManager, double value) {
        return native_Cudd_addConst(ddManager, value);
    }

    private static native long native_Cudd_addConst(long ddManager, double value);

    public static synchronized double Cudd_V(long node) {
        return native_Cudd_V(node);
    }

    private static native double native_Cudd_V(long node);

    public static synchronized int Cudd_NodeReadIndex(long node) {
        return native_Cudd_NodeReadIndex(node);
    }

    private static native int native_Cudd_NodeReadIndex(long node);

    public static synchronized long Cudd_bddIteLimit(long ddManager, long f, long g, long h, int limit) {
        return native_Cudd_bddIteLimit(ddManager, f, g, h, limit);
    }

    private static native long native_Cudd_bddIteLimit(long ddManager, long f, long g, long h, int limit);

    public static synchronized long Cudd_bddIteConstant(long ddManager, long f, long g, long h) {
        return native_Cudd_bddIteConstant(ddManager, f, g, h);
    }

    private static native long native_Cudd_bddIteConstant(long ddManager, long f, long g, long h);

    public static synchronized long Cudd_bddAndLimit(long ddManager, long f, long g, int limit) {
        return native_Cudd_bddAndLimit(ddManager, f, g, limit);
    }

    private static native long native_Cudd_bddAndLimit(long ddManager, long f, long g, int limit);

    public static synchronized long Cudd_bddOrLimit(long ddManager, long f, long g, int limit) {
        return native_Cudd_bddOrLimit(ddManager, f, g, limit);
    }

    private static native long native_Cudd_bddOrLimit(long ddManager, long f, long g, int limit);

    public static synchronized long Cudd_bddXnorLimit(long ddManager, long f, long g, int limit) {
        return native_Cudd_bddXnorLimit(ddManager, f, g, limit);
    }

    private static native long native_Cudd_bddXnorLimit(long ddManager, long f, long g, int limit);

    public static synchronized long Cudd_bddVectorCompose(long ddManager, long f, long[] vector) {
        return native_Cudd_bddVectorCompose(ddManager, f, vector);
    }

    private static native long native_Cudd_bddVectorCompose(long ddManager, long f, long[] vector);

    public static synchronized long Cudd_addVectorCompose(long ddManager, long f, long[] vector) {
        return native_Cudd_addVectorCompose(ddManager, f, vector);
    }

    private static native long native_Cudd_addVectorCompose(long ddManager, long f, long[] vector);

    public static synchronized long Cudd_addApply(long ddManager, DD_AOP op, long f, long g) {
        return native_Cudd_addApply(ddManager, op.ordinal(), f, g);
    }

    private static native long native_Cudd_addApply(long ddManager, int ddAop, long f, long g);

    public static synchronized long Cudd_addApply(long ddManager, DD_AOP_Fn op, long f, long g) {
        long result = native_Cudd_addApply(ddManager, op, f, g);
        op.rethrowPostponedRtExceptionIfAny();
        return result;
    }

    private static native long native_Cudd_addApply(long ddManager, DD_AOP_Fn op, long f, long g);

    public static synchronized long Cudd_addMonadicApply(long ddManager, DD_MAOP op, long f) {
        return native_Cudd_addMonadicApply(ddManager, op.ordinal(), f);
    }

    private static native long native_Cudd_addMonadicApply(long ddManager, int ddMaop, long f);

    public static synchronized long Cudd_addMonadicApply(long ddManager, DD_MAOP_Fn op, long f) {
        long result = native_Cudd_addMonadicApply(ddManager, op, f);
        op.rethrowPostponedRtExceptionIfAny();
        return result;
    }

    private static native long native_Cudd_addMonadicApply(long ddManager, DD_MAOP_Fn op, long f);

    public static synchronized int Cudd_ReduceHeap(long ddManager, Cudd_ReorderingType heuristic, int minsize) {
        return native_Cudd_ReduceHeap(ddManager, heuristic.ordinal(), minsize);
    }

    public static synchronized native int native_Cudd_ReduceHeap(long ddManager, int heuristic, int minsize);

    /* Generated CUDD methods */

    public static synchronized long Cudd_addNewVar(long dd) {
        return native_Cudd_addNewVar(dd);
    }

    private static native long native_Cudd_addNewVar(long dd);

    public static synchronized long Cudd_addNewVarAtLevel(long dd, int level) {
        return native_Cudd_addNewVarAtLevel(dd, level);
    }

    private static native long native_Cudd_addNewVarAtLevel(long dd, int level);

    public static synchronized long Cudd_bddNewVar(long dd) {
        return native_Cudd_bddNewVar(dd);
    }

    private static native long native_Cudd_bddNewVar(long dd);

    public static synchronized long Cudd_bddNewVarAtLevel(long dd, int level) {
        return native_Cudd_bddNewVarAtLevel(dd, level);
    }

    private static native long native_Cudd_bddNewVarAtLevel(long dd, int level);

    public static synchronized int Cudd_bddIsVar(long dd, long f) {
        return native_Cudd_bddIsVar(dd, f);
    }

    private static native int native_Cudd_bddIsVar(long dd, long f);

    public static synchronized long Cudd_addIthVar(long dd, int i) {
        return native_Cudd_addIthVar(dd, i);
    }

    private static native long native_Cudd_addIthVar(long dd, int i);

    public static synchronized long Cudd_bddIthVar(long dd, int i) {
        return native_Cudd_bddIthVar(dd, i);
    }

    private static native long native_Cudd_bddIthVar(long dd, int i);

    public static synchronized long Cudd_zddIthVar(long dd, int i) {
        return native_Cudd_zddIthVar(dd, i);
    }

    private static native long native_Cudd_zddIthVar(long dd, int i);

    public static synchronized int Cudd_zddVarsFromBddVars(long dd, int multiplicity) {
        return native_Cudd_zddVarsFromBddVars(dd, multiplicity);
    }

    private static native int native_Cudd_zddVarsFromBddVars(long dd, int multiplicity);

    public static synchronized int Cudd_IsConstant(long node) {
        return native_Cudd_IsConstant(node);
    }

    private static native int native_Cudd_IsConstant(long node);

    public static synchronized int Cudd_IsNonConstant(long f) {
        return native_Cudd_IsNonConstant(f);
    }

    private static native int native_Cudd_IsNonConstant(long f);

    public static synchronized long Cudd_T(long node) {
        return native_Cudd_T(node);
    }

    private static native long native_Cudd_T(long node);

    public static synchronized long Cudd_E(long node) {
        return native_Cudd_E(node);
    }

    private static native long native_Cudd_E(long node);

    public static synchronized void Cudd_ResetStartTime(long unique) {
        native_Cudd_ResetStartTime(unique);
    }

    private static native void native_Cudd_ResetStartTime(long unique);

    public static synchronized void Cudd_UpdateTimeLimit(long unique) {
        native_Cudd_UpdateTimeLimit(unique);
    }

    private static native void native_Cudd_UpdateTimeLimit(long unique);

    public static synchronized void Cudd_UnsetTimeLimit(long unique) {
        native_Cudd_UnsetTimeLimit(unique);
    }

    private static native void native_Cudd_UnsetTimeLimit(long unique);

    public static synchronized int Cudd_TimeLimited(long unique) {
        return native_Cudd_TimeLimited(unique);
    }

    private static native int native_Cudd_TimeLimited(long unique);

    public static synchronized void Cudd_UnregisterTerminationCallback(long unique) {
        native_Cudd_UnregisterTerminationCallback(unique);
    }

    private static native void native_Cudd_UnregisterTerminationCallback(long unique);

    public static synchronized void Cudd_UnregisterOutOfMemoryCallback(long unique) {
        native_Cudd_UnregisterOutOfMemoryCallback(unique);
    }

    private static native void native_Cudd_UnregisterOutOfMemoryCallback(long unique);

    public static synchronized void Cudd_AutodynDisable(long unique) {
        native_Cudd_AutodynDisable(unique);
    }

    private static native void native_Cudd_AutodynDisable(long unique);

    public static synchronized void Cudd_AutodynDisableZdd(long unique) {
        native_Cudd_AutodynDisableZdd(unique);
    }

    private static native void native_Cudd_AutodynDisableZdd(long unique);

    public static synchronized int Cudd_zddRealignmentEnabled(long unique) {
        return native_Cudd_zddRealignmentEnabled(unique);
    }

    private static native int native_Cudd_zddRealignmentEnabled(long unique);

    public static synchronized void Cudd_zddRealignEnable(long unique) {
        native_Cudd_zddRealignEnable(unique);
    }

    private static native void native_Cudd_zddRealignEnable(long unique);

    public static synchronized void Cudd_zddRealignDisable(long unique) {
        native_Cudd_zddRealignDisable(unique);
    }

    private static native void native_Cudd_zddRealignDisable(long unique);

    public static synchronized int Cudd_bddRealignmentEnabled(long unique) {
        return native_Cudd_bddRealignmentEnabled(unique);
    }

    private static native int native_Cudd_bddRealignmentEnabled(long unique);

    public static synchronized void Cudd_bddRealignEnable(long unique) {
        native_Cudd_bddRealignEnable(unique);
    }

    private static native void native_Cudd_bddRealignEnable(long unique);

    public static synchronized void Cudd_bddRealignDisable(long unique) {
        native_Cudd_bddRealignDisable(unique);
    }

    private static native void native_Cudd_bddRealignDisable(long unique);

    public static synchronized long Cudd_ReadOne(long dd) {
        return native_Cudd_ReadOne(dd);
    }

    private static native long native_Cudd_ReadOne(long dd);

    public static synchronized long Cudd_ReadZddOne(long dd, int i) {
        return native_Cudd_ReadZddOne(dd, i);
    }

    private static native long native_Cudd_ReadZddOne(long dd, int i);

    public static synchronized long Cudd_ReadZero(long dd) {
        return native_Cudd_ReadZero(dd);
    }

    private static native long native_Cudd_ReadZero(long dd);

    public static synchronized long Cudd_ReadLogicZero(long dd) {
        return native_Cudd_ReadLogicZero(dd);
    }

    private static native long native_Cudd_ReadLogicZero(long dd);

    public static synchronized long Cudd_ReadPlusInfinity(long dd) {
        return native_Cudd_ReadPlusInfinity(dd);
    }

    private static native long native_Cudd_ReadPlusInfinity(long dd);

    public static synchronized long Cudd_ReadMinusInfinity(long dd) {
        return native_Cudd_ReadMinusInfinity(dd);
    }

    private static native long native_Cudd_ReadMinusInfinity(long dd);

    public static synchronized long Cudd_ReadBackground(long dd) {
        return native_Cudd_ReadBackground(dd);
    }

    private static native long native_Cudd_ReadBackground(long dd);

    public static synchronized void Cudd_SetBackground(long dd, long bck) {
        native_Cudd_SetBackground(dd, bck);
    }

    private static native void native_Cudd_SetBackground(long dd, long bck);

    public static synchronized double Cudd_ReadCacheUsedSlots(long dd) {
        return native_Cudd_ReadCacheUsedSlots(dd);
    }

    private static native double native_Cudd_ReadCacheUsedSlots(long dd);

    public static synchronized double Cudd_ReadCacheLookUps(long dd) {
        return native_Cudd_ReadCacheLookUps(dd);
    }

    private static native double native_Cudd_ReadCacheLookUps(long dd);

    public static synchronized double Cudd_ReadCacheHits(long dd) {
        return native_Cudd_ReadCacheHits(dd);
    }

    private static native double native_Cudd_ReadCacheHits(long dd);

    public static synchronized double Cudd_ReadRecursiveCalls(long dd) {
        return native_Cudd_ReadRecursiveCalls(dd);
    }

    private static native double native_Cudd_ReadRecursiveCalls(long dd);

    public static synchronized int Cudd_ReadSize(long dd) {
        return native_Cudd_ReadSize(dd);
    }

    private static native int native_Cudd_ReadSize(long dd);

    public static synchronized int Cudd_ReadZddSize(long dd) {
        return native_Cudd_ReadZddSize(dd);
    }

    private static native int native_Cudd_ReadZddSize(long dd);

    public static synchronized double Cudd_ReadUsedSlots(long dd) {
        return native_Cudd_ReadUsedSlots(dd);
    }

    private static native double native_Cudd_ReadUsedSlots(long dd);

    public static synchronized double Cudd_ExpectedUsedSlots(long dd) {
        return native_Cudd_ExpectedUsedSlots(dd);
    }

    private static native double native_Cudd_ExpectedUsedSlots(long dd);

    public static synchronized long Cudd_ReadReorderingTime(long dd) {
        return native_Cudd_ReadReorderingTime(dd);
    }

    private static native long native_Cudd_ReadReorderingTime(long dd);

    public static synchronized int Cudd_ReadGarbageCollections(long dd) {
        return native_Cudd_ReadGarbageCollections(dd);
    }

    private static native int native_Cudd_ReadGarbageCollections(long dd);

    public static synchronized long Cudd_ReadGarbageCollectionTime(long dd) {
        return native_Cudd_ReadGarbageCollectionTime(dd);
    }

    private static native long native_Cudd_ReadGarbageCollectionTime(long dd);

    public static synchronized double Cudd_ReadNodesFreed(long dd) {
        return native_Cudd_ReadNodesFreed(dd);
    }

    private static native double native_Cudd_ReadNodesFreed(long dd);

    public static synchronized double Cudd_ReadNodesDropped(long dd) {
        return native_Cudd_ReadNodesDropped(dd);
    }

    private static native double native_Cudd_ReadNodesDropped(long dd);

    public static synchronized double Cudd_ReadUniqueLookUps(long dd) {
        return native_Cudd_ReadUniqueLookUps(dd);
    }

    private static native double native_Cudd_ReadUniqueLookUps(long dd);

    public static synchronized double Cudd_ReadUniqueLinks(long dd) {
        return native_Cudd_ReadUniqueLinks(dd);
    }

    private static native double native_Cudd_ReadUniqueLinks(long dd);

    public static synchronized int Cudd_ReadSiftMaxVar(long dd) {
        return native_Cudd_ReadSiftMaxVar(dd);
    }

    private static native int native_Cudd_ReadSiftMaxVar(long dd);

    public static synchronized void Cudd_SetSiftMaxVar(long dd, int smv) {
        native_Cudd_SetSiftMaxVar(dd, smv);
    }

    private static native void native_Cudd_SetSiftMaxVar(long dd, int smv);

    public static synchronized int Cudd_ReadSiftMaxSwap(long dd) {
        return native_Cudd_ReadSiftMaxSwap(dd);
    }

    private static native int native_Cudd_ReadSiftMaxSwap(long dd);

    public static synchronized void Cudd_SetSiftMaxSwap(long dd, int sms) {
        native_Cudd_SetSiftMaxSwap(dd, sms);
    }

    private static native void native_Cudd_SetSiftMaxSwap(long dd, int sms);

    public static synchronized double Cudd_ReadMaxGrowth(long dd) {
        return native_Cudd_ReadMaxGrowth(dd);
    }

    private static native double native_Cudd_ReadMaxGrowth(long dd);

    public static synchronized void Cudd_SetMaxGrowth(long dd, double mg) {
        native_Cudd_SetMaxGrowth(dd, mg);
    }

    private static native void native_Cudd_SetMaxGrowth(long dd, double mg);

    public static synchronized double Cudd_ReadMaxGrowthAlternate(long dd) {
        return native_Cudd_ReadMaxGrowthAlternate(dd);
    }

    private static native double native_Cudd_ReadMaxGrowthAlternate(long dd);

    public static synchronized void Cudd_SetMaxGrowthAlternate(long dd, double mg) {
        native_Cudd_SetMaxGrowthAlternate(dd, mg);
    }

    private static native void native_Cudd_SetMaxGrowthAlternate(long dd, double mg);

    public static synchronized int Cudd_ReadReorderingCycle(long dd) {
        return native_Cudd_ReadReorderingCycle(dd);
    }

    private static native int native_Cudd_ReadReorderingCycle(long dd);

    public static synchronized void Cudd_SetReorderingCycle(long dd, int cycle) {
        native_Cudd_SetReorderingCycle(dd, cycle);
    }

    private static native void native_Cudd_SetReorderingCycle(long dd, int cycle);

    public static synchronized int Cudd_ReadPerm(long dd, int i) {
        return native_Cudd_ReadPerm(dd, i);
    }

    private static native int native_Cudd_ReadPerm(long dd, int i);

    public static synchronized int Cudd_ReadPermZdd(long dd, int i) {
        return native_Cudd_ReadPermZdd(dd, i);
    }

    private static native int native_Cudd_ReadPermZdd(long dd, int i);

    public static synchronized int Cudd_ReadInvPerm(long dd, int i) {
        return native_Cudd_ReadInvPerm(dd, i);
    }

    private static native int native_Cudd_ReadInvPerm(long dd, int i);

    public static synchronized int Cudd_ReadInvPermZdd(long dd, int i) {
        return native_Cudd_ReadInvPermZdd(dd, i);
    }

    private static native int native_Cudd_ReadInvPermZdd(long dd, int i);

    public static synchronized long Cudd_ReadVars(long dd, int i) {
        return native_Cudd_ReadVars(dd, i);
    }

    private static native long native_Cudd_ReadVars(long dd, int i);

    public static synchronized int Cudd_GarbageCollectionEnabled(long dd) {
        return native_Cudd_GarbageCollectionEnabled(dd);
    }

    private static native int native_Cudd_GarbageCollectionEnabled(long dd);

    public static synchronized void Cudd_EnableGarbageCollection(long dd) {
        native_Cudd_EnableGarbageCollection(dd);
    }

    private static native void native_Cudd_EnableGarbageCollection(long dd);

    public static synchronized void Cudd_DisableGarbageCollection(long dd) {
        native_Cudd_DisableGarbageCollection(dd);
    }

    private static native void native_Cudd_DisableGarbageCollection(long dd);

    public static synchronized int Cudd_DeadAreCounted(long dd) {
        return native_Cudd_DeadAreCounted(dd);
    }

    private static native int native_Cudd_DeadAreCounted(long dd);

    public static synchronized void Cudd_TurnOnCountDead(long dd) {
        native_Cudd_TurnOnCountDead(dd);
    }

    private static native void native_Cudd_TurnOnCountDead(long dd);

    public static synchronized void Cudd_TurnOffCountDead(long dd) {
        native_Cudd_TurnOffCountDead(dd);
    }

    private static native void native_Cudd_TurnOffCountDead(long dd);

    public static synchronized int Cudd_ReadRecomb(long dd) {
        return native_Cudd_ReadRecomb(dd);
    }

    private static native int native_Cudd_ReadRecomb(long dd);

    public static synchronized void Cudd_SetRecomb(long dd, int recomb) {
        native_Cudd_SetRecomb(dd, recomb);
    }

    private static native void native_Cudd_SetRecomb(long dd, int recomb);

    public static synchronized int Cudd_ReadSymmviolation(long dd) {
        return native_Cudd_ReadSymmviolation(dd);
    }

    private static native int native_Cudd_ReadSymmviolation(long dd);

    public static synchronized void Cudd_SetSymmviolation(long dd, int symmviolation) {
        native_Cudd_SetSymmviolation(dd, symmviolation);
    }

    private static native void native_Cudd_SetSymmviolation(long dd, int symmviolation);

    public static synchronized int Cudd_ReadArcviolation(long dd) {
        return native_Cudd_ReadArcviolation(dd);
    }

    private static native int native_Cudd_ReadArcviolation(long dd);

    public static synchronized void Cudd_SetArcviolation(long dd, int arcviolation) {
        native_Cudd_SetArcviolation(dd, arcviolation);
    }

    private static native void native_Cudd_SetArcviolation(long dd, int arcviolation);

    public static synchronized int Cudd_ReadPopulationSize(long dd) {
        return native_Cudd_ReadPopulationSize(dd);
    }

    private static native int native_Cudd_ReadPopulationSize(long dd);

    public static synchronized void Cudd_SetPopulationSize(long dd, int populationSize) {
        native_Cudd_SetPopulationSize(dd, populationSize);
    }

    private static native void native_Cudd_SetPopulationSize(long dd, int populationSize);

    public static synchronized int Cudd_ReadNumberXovers(long dd) {
        return native_Cudd_ReadNumberXovers(dd);
    }

    private static native int native_Cudd_ReadNumberXovers(long dd);

    public static synchronized void Cudd_SetNumberXovers(long dd, int numberXovers) {
        native_Cudd_SetNumberXovers(dd, numberXovers);
    }

    private static native void native_Cudd_SetNumberXovers(long dd, int numberXovers);

    public static synchronized long Cudd_ReadPeakNodeCount(long dd) {
        return native_Cudd_ReadPeakNodeCount(dd);
    }

    private static native long native_Cudd_ReadPeakNodeCount(long dd);

    public static synchronized int Cudd_ReadPeakLiveNodeCount(long dd) {
        return native_Cudd_ReadPeakLiveNodeCount(dd);
    }

    private static native int native_Cudd_ReadPeakLiveNodeCount(long dd);

    public static synchronized long Cudd_ReadNodeCount(long dd) {
        return native_Cudd_ReadNodeCount(dd);
    }

    private static native long native_Cudd_ReadNodeCount(long dd);

    public static synchronized long Cudd_zddReadNodeCount(long dd) {
        return native_Cudd_zddReadNodeCount(dd);
    }

    private static native long native_Cudd_zddReadNodeCount(long dd);

    public static synchronized int Cudd_EnableReorderingReporting(long dd) {
        return native_Cudd_EnableReorderingReporting(dd);
    }

    private static native int native_Cudd_EnableReorderingReporting(long dd);

    public static synchronized int Cudd_DisableReorderingReporting(long dd) {
        return native_Cudd_DisableReorderingReporting(dd);
    }

    private static native int native_Cudd_DisableReorderingReporting(long dd);

    public static synchronized int Cudd_ReorderingReporting(long dd) {
        return native_Cudd_ReorderingReporting(dd);
    }

    private static native int native_Cudd_ReorderingReporting(long dd);

    public static synchronized int Cudd_EnableOrderingMonitoring(long dd) {
        return native_Cudd_EnableOrderingMonitoring(dd);
    }

    private static native int native_Cudd_EnableOrderingMonitoring(long dd);

    public static synchronized int Cudd_DisableOrderingMonitoring(long dd) {
        return native_Cudd_DisableOrderingMonitoring(dd);
    }

    private static native int native_Cudd_DisableOrderingMonitoring(long dd);

    public static synchronized int Cudd_OrderingMonitoring(long dd) {
        return native_Cudd_OrderingMonitoring(dd);
    }

    private static native int native_Cudd_OrderingMonitoring(long dd);

    public static synchronized void Cudd_ReadApplicationHook(long dd) {
        native_Cudd_ReadApplicationHook(dd);
    }

    private static native void native_Cudd_ReadApplicationHook(long dd);

    public static synchronized void Cudd_ClearErrorCode(long dd) {
        native_Cudd_ClearErrorCode(dd);
    }

    private static native void native_Cudd_ClearErrorCode(long dd);

    public static synchronized double Cudd_ReadSwapSteps(long dd) {
        return native_Cudd_ReadSwapSteps(dd);
    }

    private static native double native_Cudd_ReadSwapSteps(long dd);

    public static synchronized int Cudd_bddBindVar(long dd, int index) {
        return native_Cudd_bddBindVar(dd, index);
    }

    private static native int native_Cudd_bddBindVar(long dd, int index);

    public static synchronized int Cudd_bddUnbindVar(long dd, int index) {
        return native_Cudd_bddUnbindVar(dd, index);
    }

    private static native int native_Cudd_bddUnbindVar(long dd, int index);

    public static synchronized int Cudd_bddVarIsBound(long dd, int index) {
        return native_Cudd_bddVarIsBound(dd, index);
    }

    private static native int native_Cudd_bddVarIsBound(long dd, int index);

    public static synchronized long Cudd_addExistAbstract(long manager, long f, long cube) {
        return native_Cudd_addExistAbstract(manager, f, cube);
    }

    private static native long native_Cudd_addExistAbstract(long manager, long f, long cube);

    public static synchronized long Cudd_addUnivAbstract(long manager, long f, long cube) {
        return native_Cudd_addUnivAbstract(manager, f, cube);
    }

    private static native long native_Cudd_addUnivAbstract(long manager, long f, long cube);

    public static synchronized long Cudd_addOrAbstract(long manager, long f, long cube) {
        return native_Cudd_addOrAbstract(manager, f, cube);
    }

    private static native long native_Cudd_addOrAbstract(long manager, long f, long cube);

    public static synchronized long Cudd_addLog(long dd, long f) {
        return native_Cudd_addLog(dd, f);
    }

    private static native long native_Cudd_addLog(long dd, long f);

    public static synchronized long Cudd_addFindMax(long dd, long f) {
        return native_Cudd_addFindMax(dd, f);
    }

    private static native long native_Cudd_addFindMax(long dd, long f);

    public static synchronized long Cudd_addFindMin(long dd, long f) {
        return native_Cudd_addFindMin(dd, f);
    }

    private static native long native_Cudd_addFindMin(long dd, long f);

    public static synchronized long Cudd_addIthBit(long dd, long f, int bit) {
        return native_Cudd_addIthBit(dd, f, bit);
    }

    private static native long native_Cudd_addIthBit(long dd, long f, int bit);

    public static synchronized long Cudd_addScalarInverse(long dd, long f, long epsilon) {
        return native_Cudd_addScalarInverse(dd, f, epsilon);
    }

    private static native long native_Cudd_addScalarInverse(long dd, long f, long epsilon);

    public static synchronized long Cudd_addIte(long dd, long f, long g, long h) {
        return native_Cudd_addIte(dd, f, g, h);
    }

    private static native long native_Cudd_addIte(long dd, long f, long g, long h);

    public static synchronized long Cudd_addIteConstant(long dd, long f, long g, long h) {
        return native_Cudd_addIteConstant(dd, f, g, h);
    }

    private static native long native_Cudd_addIteConstant(long dd, long f, long g, long h);

    public static synchronized long Cudd_addEvalConst(long dd, long f, long g) {
        return native_Cudd_addEvalConst(dd, f, g);
    }

    private static native long native_Cudd_addEvalConst(long dd, long f, long g);

    public static synchronized int Cudd_addLeq(long dd, long f, long g) {
        return native_Cudd_addLeq(dd, f, g);
    }

    private static native int native_Cudd_addLeq(long dd, long f, long g);

    public static synchronized long Cudd_addCmpl(long dd, long f) {
        return native_Cudd_addCmpl(dd, f);
    }

    private static native long native_Cudd_addCmpl(long dd, long f);

    public static synchronized long Cudd_addNegate(long dd, long f) {
        return native_Cudd_addNegate(dd, f);
    }

    private static native long native_Cudd_addNegate(long dd, long f);

    public static synchronized long Cudd_addRoundOff(long dd, long f, int N) {
        return native_Cudd_addRoundOff(dd, f, N);
    }

    private static native long native_Cudd_addRoundOff(long dd, long f, int N);

    public static synchronized long Cudd_addResidue(long dd, int n, int m, int options, int top) {
        return native_Cudd_addResidue(dd, n, m, options, top);
    }

    private static native long native_Cudd_addResidue(long dd, int n, int m, int options, int top);

    public static synchronized long Cudd_bddAndAbstract(long manager, long f, long g, long cube) {
        return native_Cudd_bddAndAbstract(manager, f, g, cube);
    }

    private static native long native_Cudd_bddAndAbstract(long manager, long f, long g, long cube);

    public static synchronized int Cudd_ApaNumberOfDigits(int binaryDigits) {
        return native_Cudd_ApaNumberOfDigits(binaryDigits);
    }

    private static native int native_Cudd_ApaNumberOfDigits(int binaryDigits);

    public static synchronized long Cudd_UnderApprox(long dd, long f, int numVars, int threshold, int safe, double quality) {
        return native_Cudd_UnderApprox(dd, f, numVars, threshold, safe, quality);
    }

    private static native long native_Cudd_UnderApprox(long dd, long f, int numVars, int threshold, int safe, double quality);

    public static synchronized long Cudd_OverApprox(long dd, long f, int numVars, int threshold, int safe, double quality) {
        return native_Cudd_OverApprox(dd, f, numVars, threshold, safe, quality);
    }

    private static native long native_Cudd_OverApprox(long dd, long f, int numVars, int threshold, int safe, double quality);

    public static synchronized long Cudd_RemapUnderApprox(long dd, long f, int numVars, int threshold, double quality) {
        return native_Cudd_RemapUnderApprox(dd, f, numVars, threshold, quality);
    }

    private static native long native_Cudd_RemapUnderApprox(long dd, long f, int numVars, int threshold, double quality);

    public static synchronized long Cudd_RemapOverApprox(long dd, long f, int numVars, int threshold, double quality) {
        return native_Cudd_RemapOverApprox(dd, f, numVars, threshold, quality);
    }

    private static native long native_Cudd_RemapOverApprox(long dd, long f, int numVars, int threshold, double quality);

    public static synchronized long Cudd_BiasedUnderApprox(long dd, long f, long b, int numVars, int threshold, double quality1, double quality0) {
        return native_Cudd_BiasedUnderApprox(dd, f, b, numVars, threshold, quality1, quality0);
    }

    private static native long native_Cudd_BiasedUnderApprox(long dd, long f, long b, int numVars, int threshold, double quality1, double quality0);

    public static synchronized long Cudd_BiasedOverApprox(long dd, long f, long b, int numVars, int threshold, double quality1, double quality0) {
        return native_Cudd_BiasedOverApprox(dd, f, b, numVars, threshold, quality1, quality0);
    }

    private static native long native_Cudd_BiasedOverApprox(long dd, long f, long b, int numVars, int threshold, double quality1, double quality0);

    public static synchronized long Cudd_bddExistAbstract(long manager, long f, long cube) {
        return native_Cudd_bddExistAbstract(manager, f, cube);
    }

    private static native long native_Cudd_bddExistAbstract(long manager, long f, long cube);

    public static synchronized long Cudd_bddXorExistAbstract(long manager, long f, long g, long cube) {
        return native_Cudd_bddXorExistAbstract(manager, f, g, cube);
    }

    private static native long native_Cudd_bddXorExistAbstract(long manager, long f, long g, long cube);

    public static synchronized long Cudd_bddUnivAbstract(long manager, long f, long cube) {
        return native_Cudd_bddUnivAbstract(manager, f, cube);
    }

    private static native long native_Cudd_bddUnivAbstract(long manager, long f, long cube);

    public static synchronized long Cudd_bddBooleanDiff(long manager, long f, int x) {
        return native_Cudd_bddBooleanDiff(manager, f, x);
    }

    private static native long native_Cudd_bddBooleanDiff(long manager, long f, int x);

    public static synchronized int Cudd_bddVarIsDependent(long dd, long f, long var) {
        return native_Cudd_bddVarIsDependent(dd, f, var);
    }

    private static native int native_Cudd_bddVarIsDependent(long dd, long f, long var);

    public static synchronized double Cudd_bddCorrelation(long manager, long f, long g) {
        return native_Cudd_bddCorrelation(manager, f, g);
    }

    private static native double native_Cudd_bddCorrelation(long manager, long f, long g);

    public static synchronized double Cudd_bddCorrelationWeights(long manager, long f, long g, double[] prob) {
        return native_Cudd_bddCorrelationWeights(manager, f, g, prob);
    }

    private static native double native_Cudd_bddCorrelationWeights(long manager, long f, long g, double[] prob);

    public static synchronized long Cudd_bddIte(long dd, long f, long g, long h) {
        return native_Cudd_bddIte(dd, f, g, h);
    }

    private static native long native_Cudd_bddIte(long dd, long f, long g, long h);

    public static synchronized long Cudd_bddIntersect(long dd, long f, long g) {
        return native_Cudd_bddIntersect(dd, f, g);
    }

    private static native long native_Cudd_bddIntersect(long dd, long f, long g);

    public static synchronized long Cudd_bddAnd(long dd, long f, long g) {
        return native_Cudd_bddAnd(dd, f, g);
    }

    private static native long native_Cudd_bddAnd(long dd, long f, long g);

    public static synchronized long Cudd_bddOr(long dd, long f, long g) {
        return native_Cudd_bddOr(dd, f, g);
    }

    private static native long native_Cudd_bddOr(long dd, long f, long g);

    public static synchronized long Cudd_bddNand(long dd, long f, long g) {
        return native_Cudd_bddNand(dd, f, g);
    }

    private static native long native_Cudd_bddNand(long dd, long f, long g);

    public static synchronized long Cudd_bddNor(long dd, long f, long g) {
        return native_Cudd_bddNor(dd, f, g);
    }

    private static native long native_Cudd_bddNor(long dd, long f, long g);

    public static synchronized long Cudd_bddXor(long dd, long f, long g) {
        return native_Cudd_bddXor(dd, f, g);
    }

    private static native long native_Cudd_bddXor(long dd, long f, long g);

    public static synchronized long Cudd_bddXnor(long dd, long f, long g) {
        return native_Cudd_bddXnor(dd, f, g);
    }

    private static native long native_Cudd_bddXnor(long dd, long f, long g);

    public static synchronized int Cudd_bddLeq(long dd, long f, long g) {
        return native_Cudd_bddLeq(dd, f, g);
    }

    private static native int native_Cudd_bddLeq(long dd, long f, long g);

    public static synchronized long Cudd_addBddIthBit(long dd, long f, int bit) {
        return native_Cudd_addBddIthBit(dd, f, bit);
    }

    private static native long native_Cudd_addBddIthBit(long dd, long f, int bit);

    public static synchronized long Cudd_BddToAdd(long dd, long B) {
        return native_Cudd_BddToAdd(dd, B);
    }

    private static native long native_Cudd_BddToAdd(long dd, long B);

    public static synchronized long Cudd_addBddPattern(long dd, long f) {
        return native_Cudd_addBddPattern(dd, f);
    }

    private static native long native_Cudd_addBddPattern(long dd, long f);

    public static synchronized long Cudd_bddTransfer(long ddSource, long ddDestination, long f) {
        return native_Cudd_bddTransfer(ddSource, ddDestination, f);
    }

    private static native long native_Cudd_bddTransfer(long ddSource, long ddDestination, long f);

    public static synchronized int Cudd_DebugCheck(long table) {
        return native_Cudd_DebugCheck(table);
    }

    private static native int native_Cudd_DebugCheck(long table);

    public static synchronized int Cudd_CheckKeys(long table) {
        return native_Cudd_CheckKeys(table);
    }

    private static native int native_Cudd_CheckKeys(long table);

    public static synchronized long Cudd_bddClippingAnd(long dd, long f, long g, int maxDepth, int direction) {
        return native_Cudd_bddClippingAnd(dd, f, g, maxDepth, direction);
    }

    private static native long native_Cudd_bddClippingAnd(long dd, long f, long g, int maxDepth, int direction);

    public static synchronized long Cudd_bddClippingAndAbstract(long dd, long f, long g, long cube, int maxDepth, int direction) {
        return native_Cudd_bddClippingAndAbstract(dd, f, g, cube, maxDepth, direction);
    }

    private static native long native_Cudd_bddClippingAndAbstract(long dd, long f, long g, long cube, int maxDepth, int direction);

    public static synchronized long Cudd_Cofactor(long dd, long f, long g) {
        return native_Cudd_Cofactor(dd, f, g);
    }

    private static native long native_Cudd_Cofactor(long dd, long f, long g);

    public static synchronized int Cudd_CheckCube(long dd, long g) {
        return native_Cudd_CheckCube(dd, g);
    }

    private static native int native_Cudd_CheckCube(long dd, long g);

    public static synchronized int Cudd_VarsAreSymmetric(long dd, long f, int index1, int index2) {
        return native_Cudd_VarsAreSymmetric(dd, f, index1, index2);
    }

    private static native int native_Cudd_VarsAreSymmetric(long dd, long f, int index1, int index2);

    public static synchronized long Cudd_bddCompose(long dd, long f, long g, int v) {
        return native_Cudd_bddCompose(dd, f, g, v);
    }

    private static native long native_Cudd_bddCompose(long dd, long f, long g, int v);

    public static synchronized long Cudd_addCompose(long dd, long f, long g, int v) {
        return native_Cudd_addCompose(dd, f, g, v);
    }

    private static native long native_Cudd_addCompose(long dd, long f, long g, int v);

    public static synchronized long Cudd_addPermute(long manager, long node, int[] permut) {
        return native_Cudd_addPermute(manager, node, permut);
    }

    private static native long native_Cudd_addPermute(long manager, long node, int[] permut);

    public static synchronized long Cudd_bddPermute(long manager, long node, int[] permut) {
        return native_Cudd_bddPermute(manager, node, permut);
    }

    private static native long native_Cudd_bddPermute(long manager, long node, int[] permut);

    public static synchronized long Cudd_bddVarMap(long manager, long f) {
        return native_Cudd_bddVarMap(manager, f);
    }

    private static native long native_Cudd_bddVarMap(long manager, long f);

    public static synchronized long Cudd_FindEssential(long dd, long f) {
        return native_Cudd_FindEssential(dd, f);
    }

    private static native long native_Cudd_FindEssential(long dd, long f);

    public static synchronized int Cudd_bddIsVarEssential(long manager, long f, int id, int phase) {
        return native_Cudd_bddIsVarEssential(manager, f, id, phase);
    }

    private static native int native_Cudd_bddIsVarEssential(long manager, long f, int id, int phase);

    public static synchronized long Cudd_bddConstrain(long dd, long f, long c) {
        return native_Cudd_bddConstrain(dd, f, c);
    }

    private static native long native_Cudd_bddConstrain(long dd, long f, long c);

    public static synchronized long Cudd_bddRestrict(long dd, long f, long c) {
        return native_Cudd_bddRestrict(dd, f, c);
    }

    private static native long native_Cudd_bddRestrict(long dd, long f, long c);

    public static synchronized long Cudd_bddNPAnd(long dd, long f, long c) {
        return native_Cudd_bddNPAnd(dd, f, c);
    }

    private static native long native_Cudd_bddNPAnd(long dd, long f, long c);

    public static synchronized long Cudd_addConstrain(long dd, long f, long c) {
        return native_Cudd_addConstrain(dd, f, c);
    }

    private static native long native_Cudd_addConstrain(long dd, long f, long c);

    public static synchronized long Cudd_addRestrict(long dd, long f, long c) {
        return native_Cudd_addRestrict(dd, f, c);
    }

    private static native long native_Cudd_addRestrict(long dd, long f, long c);

    public static synchronized long Cudd_bddLICompaction(long dd, long f, long c) {
        return native_Cudd_bddLICompaction(dd, f, c);
    }

    private static native long native_Cudd_bddLICompaction(long dd, long f, long c);

    public static synchronized long Cudd_bddSqueeze(long dd, long l, long u) {
        return native_Cudd_bddSqueeze(dd, l, u);
    }

    private static native long native_Cudd_bddSqueeze(long dd, long l, long u);

    public static synchronized long Cudd_bddInterpolate(long dd, long l, long u) {
        return native_Cudd_bddInterpolate(dd, l, u);
    }

    private static native long native_Cudd_bddInterpolate(long dd, long l, long u);

    public static synchronized long Cudd_bddMinimize(long dd, long f, long c) {
        return native_Cudd_bddMinimize(dd, f, c);
    }

    private static native long native_Cudd_bddMinimize(long dd, long f, long c);

    public static synchronized long Cudd_SubsetCompress(long dd, long f, int nvars, int threshold) {
        return native_Cudd_SubsetCompress(dd, f, nvars, threshold);
    }

    private static native long native_Cudd_SubsetCompress(long dd, long f, int nvars, int threshold);

    public static synchronized long Cudd_SupersetCompress(long dd, long f, int nvars, int threshold) {
        return native_Cudd_SupersetCompress(dd, f, nvars, threshold);
    }

    private static native long native_Cudd_SupersetCompress(long dd, long f, int nvars, int threshold);

    public static synchronized void Cudd_Quit(long unique) {
        native_Cudd_Quit(unique);
    }

    private static native void native_Cudd_Quit(long unique);

    public static synchronized int Cudd_PrintLinear(long table) {
        return native_Cudd_PrintLinear(table);
    }

    private static native int native_Cudd_PrintLinear(long table);

    public static synchronized int Cudd_ReadLinear(long table, int x, int y) {
        return native_Cudd_ReadLinear(table, x, y);
    }

    private static native int native_Cudd_ReadLinear(long table, int x, int y);

    public static synchronized long Cudd_bddLiteralSetIntersection(long dd, long f, long g) {
        return native_Cudd_bddLiteralSetIntersection(dd, f, g);
    }

    private static native long native_Cudd_bddLiteralSetIntersection(long dd, long f, long g);

    public static synchronized long Cudd_addOuterSum(long dd, long M, long r, long c) {
        return native_Cudd_addOuterSum(dd, M, r, c);
    }

    private static native long native_Cudd_addOuterSum(long dd, long M, long r, long c);

    public static synchronized long Cudd_CProjection(long dd, long R, long Y) {
        return native_Cudd_CProjection(dd, R, Y);
    }

    private static native long native_Cudd_CProjection(long dd, long R, long Y);

    public static synchronized int Cudd_MinHammingDist(long dd, long f, int[] minterm, int upperBound) {
        return native_Cudd_MinHammingDist(dd, f, minterm, upperBound);
    }

    private static native int native_Cudd_MinHammingDist(long dd, long f, int[] minterm, int upperBound);

    public static synchronized long Cudd_bddClosestCube(long dd, long f, long g, int[] distance) {
        return native_Cudd_bddClosestCube(dd, f, g, distance);
    }

    private static native long native_Cudd_bddClosestCube(long dd, long f, long g, int[] distance);

    public static synchronized void Cudd_Ref(long n) {
        native_Cudd_Ref(n);
    }

    private static native void native_Cudd_Ref(long n);

    public static synchronized void Cudd_RecursiveDeref(long table, long n) {
        native_Cudd_RecursiveDeref(table, n);
    }

    private static native void native_Cudd_RecursiveDeref(long table, long n);

    public static synchronized void Cudd_IterDerefBdd(long table, long n) {
        native_Cudd_IterDerefBdd(table, n);
    }

    private static native void native_Cudd_IterDerefBdd(long table, long n);

    public static synchronized void Cudd_DelayedDerefBdd(long table, long n) {
        native_Cudd_DelayedDerefBdd(table, n);
    }

    private static native void native_Cudd_DelayedDerefBdd(long table, long n);

    public static synchronized void Cudd_RecursiveDerefZdd(long table, long n) {
        native_Cudd_RecursiveDerefZdd(table, n);
    }

    private static native void native_Cudd_RecursiveDerefZdd(long table, long n);

    public static synchronized void Cudd_Deref(long node) {
        native_Cudd_Deref(node);
    }

    private static native void native_Cudd_Deref(long node);

    public static synchronized int Cudd_CheckZeroRef(long manager) {
        return native_Cudd_CheckZeroRef(manager);
    }

    private static native int native_Cudd_CheckZeroRef(long manager);

    public static synchronized int Cudd_ShuffleHeap(long table, int[] permutation) {
        return native_Cudd_ShuffleHeap(table, permutation);
    }

    private static native int native_Cudd_ShuffleHeap(long table, int[] permutation);

    public static synchronized long Cudd_Eval(long dd, long f, int[] inputs) {
        return native_Cudd_Eval(dd, f, inputs);
    }

    private static native long native_Cudd_Eval(long dd, long f, int[] inputs);

    public static synchronized long Cudd_ShortestPath(long manager, long f, int[] weight, int[] support, int[] length) {
        return native_Cudd_ShortestPath(manager, f, weight, support, length);
    }

    private static native long native_Cudd_ShortestPath(long manager, long f, int[] weight, int[] support, int[] length);

    public static synchronized long Cudd_LargestCube(long manager, long f, int[] length) {
        return native_Cudd_LargestCube(manager, f, length);
    }

    private static native long native_Cudd_LargestCube(long manager, long f, int[] length);

    public static synchronized int Cudd_ShortestLength(long manager, long f, int[] weight) {
        return native_Cudd_ShortestLength(manager, f, weight);
    }

    private static native int native_Cudd_ShortestLength(long manager, long f, int[] weight);

    public static synchronized long Cudd_Decreasing(long dd, long f, int i) {
        return native_Cudd_Decreasing(dd, f, i);
    }

    private static native long native_Cudd_Decreasing(long dd, long f, int i);

    public static synchronized long Cudd_Increasing(long dd, long f, int i) {
        return native_Cudd_Increasing(dd, f, i);
    }

    private static native long native_Cudd_Increasing(long dd, long f, int i);

    public static synchronized int Cudd_EquivDC(long dd, long F, long G, long D) {
        return native_Cudd_EquivDC(dd, F, G, D);
    }

    private static native int native_Cudd_EquivDC(long dd, long F, long G, long D);

    public static synchronized int Cudd_bddLeqUnless(long dd, long f, long g, long D) {
        return native_Cudd_bddLeqUnless(dd, f, g, D);
    }

    private static native int native_Cudd_bddLeqUnless(long dd, long f, long g, long D);

    public static synchronized long Cudd_bddMakePrime(long dd, long cube, long f) {
        return native_Cudd_bddMakePrime(dd, cube, f);
    }

    private static native long native_Cudd_bddMakePrime(long dd, long cube, long f);

    public static synchronized long Cudd_bddMaximallyExpand(long dd, long lb, long ub, long f) {
        return native_Cudd_bddMaximallyExpand(dd, lb, ub, f);
    }

    private static native long native_Cudd_bddMaximallyExpand(long dd, long lb, long ub, long f);

    public static synchronized long Cudd_bddLargestPrimeUnate(long dd, long f, long phaseBdd) {
        return native_Cudd_bddLargestPrimeUnate(dd, f, phaseBdd);
    }

    private static native long native_Cudd_bddLargestPrimeUnate(long dd, long f, long phaseBdd);

    public static synchronized long Cudd_SubsetHeavyBranch(long dd, long f, int numVars, int threshold) {
        return native_Cudd_SubsetHeavyBranch(dd, f, numVars, threshold);
    }

    private static native long native_Cudd_SubsetHeavyBranch(long dd, long f, int numVars, int threshold);

    public static synchronized long Cudd_SupersetHeavyBranch(long dd, long f, int numVars, int threshold) {
        return native_Cudd_SupersetHeavyBranch(dd, f, numVars, threshold);
    }

    private static native long native_Cudd_SupersetHeavyBranch(long dd, long f, int numVars, int threshold);

    public static synchronized long Cudd_SubsetShortPaths(long dd, long f, int numVars, int threshold, int hardlimit) {
        return native_Cudd_SubsetShortPaths(dd, f, numVars, threshold, hardlimit);
    }

    private static native long native_Cudd_SubsetShortPaths(long dd, long f, int numVars, int threshold, int hardlimit);

    public static synchronized long Cudd_SupersetShortPaths(long dd, long f, int numVars, int threshold, int hardlimit) {
        return native_Cudd_SupersetShortPaths(dd, f, numVars, threshold, hardlimit);
    }

    private static native long native_Cudd_SupersetShortPaths(long dd, long f, int numVars, int threshold, int hardlimit);

    public static synchronized void Cudd_SymmProfile(long table, int lower, int upper) {
        native_Cudd_SymmProfile(table, lower, upper);
    }

    private static native void native_Cudd_SymmProfile(long table, int lower, int upper);

    public static synchronized int Cudd_Reserve(long manager, int amount) {
        return native_Cudd_Reserve(manager, amount);
    }

    private static native int native_Cudd_Reserve(long manager, int amount);

    public static synchronized int Cudd_PrintMinterm(long manager, long node) {
        return native_Cudd_PrintMinterm(manager, node);
    }

    private static native int native_Cudd_PrintMinterm(long manager, long node);

    public static synchronized int Cudd_bddPrintCover(long dd, long l, long u) {
        return native_Cudd_bddPrintCover(dd, l, u);
    }

    private static native int native_Cudd_bddPrintCover(long dd, long l, long u);

    public static synchronized int Cudd_PrintDebug(long dd, long f, int n, int pr) {
        return native_Cudd_PrintDebug(dd, f, n, pr);
    }

    private static native int native_Cudd_PrintDebug(long dd, long f, int n, int pr);

    public static synchronized int Cudd_PrintSummary(long dd, long f, int n, int mode) {
        return native_Cudd_PrintSummary(dd, f, n, mode);
    }

    private static native int native_Cudd_PrintSummary(long dd, long f, int n, int mode);

    public static synchronized int Cudd_DagSize(long node) {
        return native_Cudd_DagSize(node);
    }

    private static native int native_Cudd_DagSize(long node);

    public static synchronized int Cudd_EstimateCofactor(long dd, long node, int i, int phase) {
        return native_Cudd_EstimateCofactor(dd, node, i, phase);
    }

    private static native int native_Cudd_EstimateCofactor(long dd, long node, int i, int phase);

    public static synchronized int Cudd_EstimateCofactorSimple(long node, int i) {
        return native_Cudd_EstimateCofactorSimple(node, i);
    }

    private static native int native_Cudd_EstimateCofactorSimple(long node, int i);

    public static synchronized double Cudd_CountMinterm(long manager, long node, int nvars) {
        return native_Cudd_CountMinterm(manager, node, nvars);
    }

    private static native double native_Cudd_CountMinterm(long manager, long node, int nvars);

    public static synchronized double Cudd_CountPath(long node) {
        return native_Cudd_CountPath(node);
    }

    private static native double native_Cudd_CountPath(long node);

    public static synchronized double Cudd_CountPathsToNonZero(long node) {
        return native_Cudd_CountPathsToNonZero(node);
    }

    private static native double native_Cudd_CountPathsToNonZero(long node);

    public static synchronized long Cudd_Support(long dd, long f) {
        return native_Cudd_Support(dd, f);
    }

    private static native long native_Cudd_Support(long dd, long f);

    public static synchronized int Cudd_SupportSize(long dd, long f) {
        return native_Cudd_SupportSize(dd, f);
    }

    private static native int native_Cudd_SupportSize(long dd, long f);

    public static synchronized int Cudd_CountLeaves(long node) {
        return native_Cudd_CountLeaves(node);
    }

    private static native int native_Cudd_CountLeaves(long node);

    public static synchronized long Cudd_CubeArrayToBdd(long dd, int[] array) {
        return native_Cudd_CubeArrayToBdd(dd, array);
    }

    private static native long native_Cudd_CubeArrayToBdd(long dd, int[] array);

    public static synchronized int Cudd_BddToCubeArray(long dd, long cube, int[] array) {
        return native_Cudd_BddToCubeArray(dd, cube, array);
    }

    private static native int native_Cudd_BddToCubeArray(long dd, long cube, int[] array);

    public static synchronized long Cudd_IndicesToCube(long dd, int[] array, int n) {
        return native_Cudd_IndicesToCube(dd, array, n);
    }

    private static native long native_Cudd_IndicesToCube(long dd, int[] array, int n);

    public static synchronized double Cudd_AverageDistance(long dd) {
        return native_Cudd_AverageDistance(dd);
    }

    private static native double native_Cudd_AverageDistance(long dd);

    public static synchronized double Cudd_Density(long dd, long f, int nvars) {
        return native_Cudd_Density(dd, f, nvars);
    }

    private static native double native_Cudd_Density(long dd, long f, int nvars);

    public static synchronized int Cudd_zddCount(long zdd, long P) {
        return native_Cudd_zddCount(zdd, P);
    }

    private static native int native_Cudd_zddCount(long zdd, long P);

    public static synchronized double Cudd_zddCountDouble(long zdd, long P) {
        return native_Cudd_zddCountDouble(zdd, P);
    }

    private static native double native_Cudd_zddCountDouble(long zdd, long P);

    public static synchronized long Cudd_zddProduct(long dd, long f, long g) {
        return native_Cudd_zddProduct(dd, f, g);
    }

    private static native long native_Cudd_zddProduct(long dd, long f, long g);

    public static synchronized long Cudd_zddUnateProduct(long dd, long f, long g) {
        return native_Cudd_zddUnateProduct(dd, f, g);
    }

    private static native long native_Cudd_zddUnateProduct(long dd, long f, long g);

    public static synchronized long Cudd_zddWeakDiv(long dd, long f, long g) {
        return native_Cudd_zddWeakDiv(dd, f, g);
    }

    private static native long native_Cudd_zddWeakDiv(long dd, long f, long g);

    public static synchronized long Cudd_zddDivide(long dd, long f, long g) {
        return native_Cudd_zddDivide(dd, f, g);
    }

    private static native long native_Cudd_zddDivide(long dd, long f, long g);

    public static synchronized long Cudd_zddWeakDivF(long dd, long f, long g) {
        return native_Cudd_zddWeakDivF(dd, f, g);
    }

    private static native long native_Cudd_zddWeakDivF(long dd, long f, long g);

    public static synchronized long Cudd_zddDivideF(long dd, long f, long g) {
        return native_Cudd_zddDivideF(dd, f, g);
    }

    private static native long native_Cudd_zddDivideF(long dd, long f, long g);

    public static synchronized long Cudd_zddComplement(long dd, long node) {
        return native_Cudd_zddComplement(dd, node);
    }

    private static native long native_Cudd_zddComplement(long dd, long node);

    public static synchronized long Cudd_bddIsop(long dd, long L, long U) {
        return native_Cudd_bddIsop(dd, L, U);
    }

    private static native long native_Cudd_bddIsop(long dd, long L, long U);

    public static synchronized long Cudd_MakeBddFromZddCover(long dd, long node) {
        return native_Cudd_MakeBddFromZddCover(dd, node);
    }

    private static native long native_Cudd_MakeBddFromZddCover(long dd, long node);

    public static synchronized int Cudd_zddDagSize(long p_node) {
        return native_Cudd_zddDagSize(p_node);
    }

    private static native int native_Cudd_zddDagSize(long p_node);

    public static synchronized double Cudd_zddCountMinterm(long zdd, long node, int path) {
        return native_Cudd_zddCountMinterm(zdd, node, path);
    }

    private static native double native_Cudd_zddCountMinterm(long zdd, long node, int path);

    public static synchronized void Cudd_zddPrintSubtable(long table) {
        native_Cudd_zddPrintSubtable(table);
    }

    private static native void native_Cudd_zddPrintSubtable(long table);

    public static synchronized long Cudd_zddPortFromBdd(long dd, long B) {
        return native_Cudd_zddPortFromBdd(dd, B);
    }

    private static native long native_Cudd_zddPortFromBdd(long dd, long B);

    public static synchronized long Cudd_zddPortToBdd(long dd, long f) {
        return native_Cudd_zddPortToBdd(dd, f);
    }

    private static native long native_Cudd_zddPortToBdd(long dd, long f);

    public static synchronized int Cudd_zddShuffleHeap(long table, int[] permutation) {
        return native_Cudd_zddShuffleHeap(table, permutation);
    }

    private static native int native_Cudd_zddShuffleHeap(long table, int[] permutation);

    public static synchronized long Cudd_zddIte(long dd, long f, long g, long h) {
        return native_Cudd_zddIte(dd, f, g, h);
    }

    private static native long native_Cudd_zddIte(long dd, long f, long g, long h);

    public static synchronized long Cudd_zddUnion(long dd, long P, long Q) {
        return native_Cudd_zddUnion(dd, P, Q);
    }

    private static native long native_Cudd_zddUnion(long dd, long P, long Q);

    public static synchronized long Cudd_zddIntersect(long dd, long P, long Q) {
        return native_Cudd_zddIntersect(dd, P, Q);
    }

    private static native long native_Cudd_zddIntersect(long dd, long P, long Q);

    public static synchronized long Cudd_zddDiff(long dd, long P, long Q) {
        return native_Cudd_zddDiff(dd, P, Q);
    }

    private static native long native_Cudd_zddDiff(long dd, long P, long Q);

    public static synchronized long Cudd_zddDiffConst(long zdd, long P, long Q) {
        return native_Cudd_zddDiffConst(zdd, P, Q);
    }

    private static native long native_Cudd_zddDiffConst(long zdd, long P, long Q);

    public static synchronized long Cudd_zddSubset1(long dd, long P, int var) {
        return native_Cudd_zddSubset1(dd, P, var);
    }

    private static native long native_Cudd_zddSubset1(long dd, long P, int var);

    public static synchronized long Cudd_zddSubset0(long dd, long P, int var) {
        return native_Cudd_zddSubset0(dd, P, var);
    }

    private static native long native_Cudd_zddSubset0(long dd, long P, int var);

    public static synchronized long Cudd_zddChange(long dd, long P, int var) {
        return native_Cudd_zddChange(dd, P, var);
    }

    private static native long native_Cudd_zddChange(long dd, long P, int var);

    public static synchronized void Cudd_zddSymmProfile(long table, int lower, int upper) {
        native_Cudd_zddSymmProfile(table, lower, upper);
    }

    private static native void native_Cudd_zddSymmProfile(long table, int lower, int upper);

    public static synchronized int Cudd_zddPrintMinterm(long zdd, long node) {
        return native_Cudd_zddPrintMinterm(zdd, node);
    }

    private static native int native_Cudd_zddPrintMinterm(long zdd, long node);

    public static synchronized int Cudd_zddPrintCover(long zdd, long node) {
        return native_Cudd_zddPrintCover(zdd, node);
    }

    private static native int native_Cudd_zddPrintCover(long zdd, long node);

    public static synchronized int Cudd_zddPrintDebug(long zdd, long f, int n, int pr) {
        return native_Cudd_zddPrintDebug(zdd, f, n, pr);
    }

    private static native int native_Cudd_zddPrintDebug(long zdd, long f, int n, int pr);

    public static synchronized long Cudd_zddSupport(long dd, long f) {
        return native_Cudd_zddSupport(dd, f);
    }

    private static native long native_Cudd_zddSupport(long dd, long f);

    public static synchronized int Cudd_bddSetPiVar(long dd, int index) {
        return native_Cudd_bddSetPiVar(dd, index);
    }

    private static native int native_Cudd_bddSetPiVar(long dd, int index);

    public static synchronized int Cudd_bddSetPsVar(long dd, int index) {
        return native_Cudd_bddSetPsVar(dd, index);
    }

    private static native int native_Cudd_bddSetPsVar(long dd, int index);

    public static synchronized int Cudd_bddSetNsVar(long dd, int index) {
        return native_Cudd_bddSetNsVar(dd, index);
    }

    private static native int native_Cudd_bddSetNsVar(long dd, int index);

    public static synchronized int Cudd_bddIsPiVar(long dd, int index) {
        return native_Cudd_bddIsPiVar(dd, index);
    }

    private static native int native_Cudd_bddIsPiVar(long dd, int index);

    public static synchronized int Cudd_bddIsPsVar(long dd, int index) {
        return native_Cudd_bddIsPsVar(dd, index);
    }

    private static native int native_Cudd_bddIsPsVar(long dd, int index);

    public static synchronized int Cudd_bddIsNsVar(long dd, int index) {
        return native_Cudd_bddIsNsVar(dd, index);
    }

    private static native int native_Cudd_bddIsNsVar(long dd, int index);

    public static synchronized int Cudd_bddSetPairIndex(long dd, int index, int pairIndex) {
        return native_Cudd_bddSetPairIndex(dd, index, pairIndex);
    }

    private static native int native_Cudd_bddSetPairIndex(long dd, int index, int pairIndex);

    public static synchronized int Cudd_bddReadPairIndex(long dd, int index) {
        return native_Cudd_bddReadPairIndex(dd, index);
    }

    private static native int native_Cudd_bddReadPairIndex(long dd, int index);

    public static synchronized int Cudd_bddSetVarToBeGrouped(long dd, int index) {
        return native_Cudd_bddSetVarToBeGrouped(dd, index);
    }

    private static native int native_Cudd_bddSetVarToBeGrouped(long dd, int index);

    public static synchronized int Cudd_bddSetVarHardGroup(long dd, int index) {
        return native_Cudd_bddSetVarHardGroup(dd, index);
    }

    private static native int native_Cudd_bddSetVarHardGroup(long dd, int index);

    public static synchronized int Cudd_bddResetVarToBeGrouped(long dd, int index) {
        return native_Cudd_bddResetVarToBeGrouped(dd, index);
    }

    private static native int native_Cudd_bddResetVarToBeGrouped(long dd, int index);

    public static synchronized int Cudd_bddIsVarToBeGrouped(long dd, int index) {
        return native_Cudd_bddIsVarToBeGrouped(dd, index);
    }

    private static native int native_Cudd_bddIsVarToBeGrouped(long dd, int index);

    public static synchronized int Cudd_bddSetVarToBeUngrouped(long dd, int index) {
        return native_Cudd_bddSetVarToBeUngrouped(dd, index);
    }

    private static native int native_Cudd_bddSetVarToBeUngrouped(long dd, int index);

    public static synchronized int Cudd_bddIsVarToBeUngrouped(long dd, int index) {
        return native_Cudd_bddIsVarToBeUngrouped(dd, index);
    }

    private static native int native_Cudd_bddIsVarToBeUngrouped(long dd, int index);

    public static synchronized int Cudd_bddIsVarHardGroup(long dd, int index) {
        return native_Cudd_bddIsVarHardGroup(dd, index);
    }

    private static native int native_Cudd_bddIsVarHardGroup(long dd, int index);

    public static int Cudd_zddReduceHeap(long table, Cudd_ReorderingType heuristic, int minsize) {
        return native_Cudd_zddReduceHeap(table, heuristic.ordinal(), minsize);
    }

    private static native int native_Cudd_zddReduceHeap(long table, int heuristic, int minsize);

    public static void Cudd_AutodynEnable(long unique, Cudd_ReorderingType method) {
        native_Cudd_AutodynEnable(unique, method.ordinal());
    }

    private static native void native_Cudd_AutodynEnable(long unique, int heuristic);

    public static void Cudd_AutodynEnableZdd(long unique, Cudd_ReorderingType method) {
        native_Cudd_AutodynEnableZdd(unique, method.ordinal());
    }

    private static native void native_Cudd_AutodynEnableZdd(long unique, int heuristic);

    public static void Cudd_SetNextReordering(long dd, int next) {
        native_Cudd_SetNextReordering(dd, next);
    }

    private static native void native_Cudd_SetNextReordering(long dd, int next);

    public static int Cudd_ReadNextReordering(long dd) {
        return native_Cudd_ReadNextReordering(dd);
    }

    private static native int native_Cudd_ReadNextReordering(long dd);

    public static int Cudd_ReorderingStatus(long dd, int method) {
        return native_Cudd_ReorderingStatus(dd, method);
    }

    private static native int native_Cudd_ReorderingStatus(long unique, int method);

    public static long Cudd_Regular(long dd) {
        return native_Cudd_Regular(dd);
    }

    private static native long native_Cudd_Regular(long dd);
}