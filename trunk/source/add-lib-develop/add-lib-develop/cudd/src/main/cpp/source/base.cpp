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
#include "info_scce_addlib_cudd_Cudd.h"
#include "cudd.h"

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Complement
	(JNIEnv *, jclass, jlong node) {

	return (jlong) Cudd_Complement((DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1IsComplement
	(JNIEnv *, jclass, jlong node) {

	return (jint) Cudd_IsComplement((DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Not
	(JNIEnv *, jclass, jlong node) {

	return (jlong) Cudd_Not((DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Init
	(JNIEnv *, jclass, jint numVars, jint numVarsZ, jint numSlots, jint cacheSize, jlong maxMemory) {

	return (jlong) Cudd_Init(numVars, numVarsZ, numSlots, cacheSize, maxMemory);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadEpsilon
	(JNIEnv *, jclass, jlong ddManager) {

	return (jdouble) Cudd_ReadEpsilon((DdManager *) ddManager);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetEpsilon
	(JNIEnv *, jclass, jlong ddManager, jdouble epsilon) {

	Cudd_SetEpsilon((DdManager *) ddManager, epsilon);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addConst
	(JNIEnv *, jclass, jlong ddManager, jdouble value) {

	return (jlong) Cudd_addConst((DdManager *) ddManager, value);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1V
	(JNIEnv *, jclass, jlong node) {

	return (jdouble) Cudd_V((DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1NodeReadIndex
	(JNIEnv *, jclass, jlong node) {

	return (jint) Cudd_NodeReadIndex((DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIteLimit
	(JNIEnv *, jclass, jlong ddManager, jlong f, jlong g, jlong h, jint limit) {

	return (jlong) Cudd_bddIteLimit((DdManager *) ddManager, (DdNode *) f, (DdNode *) g, (DdNode *) h, limit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddAndLimit
	(JNIEnv *, jclass, jlong ddManager, jlong f, jlong g, jint limit) {

	return (jlong) Cudd_bddAndLimit((DdManager *) ddManager, (DdNode *) f, (DdNode *) g, limit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddOrLimit
	(JNIEnv *, jclass, jlong ddManager, jlong f, jlong g, jint limit) {

	return (jlong) Cudd_bddOrLimit((DdManager *) ddManager, (DdNode *) f, (DdNode *) g, limit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddXnorLimit
	(JNIEnv *, jclass, jlong ddManager, jlong f, jlong g, jint limit) {

	return (jlong) Cudd_bddXnorLimit((DdManager *) ddManager, (DdNode *) f, (DdNode *) g, limit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addNewVar
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_addNewVar((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addNewVarAtLevel
	(JNIEnv *, jclass, jlong dd, jint level) {

	return (jlong) Cudd_addNewVarAtLevel((DdManager *) dd, level);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddNewVar
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_bddNewVar((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddNewVarAtLevel
	(JNIEnv *, jclass, jlong dd, jint level) {

	return (jlong) Cudd_bddNewVarAtLevel((DdManager *) dd, level);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsVar
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jint) Cudd_bddIsVar((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addIthVar
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jlong) Cudd_addIthVar((DdManager *) dd, i);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIthVar
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jlong) Cudd_bddIthVar((DdManager *) dd, i);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddIthVar
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jlong) Cudd_zddIthVar((DdManager *) dd, i);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddVarsFromBddVars
	(JNIEnv *, jclass, jlong dd, jint multiplicity) {

	return (jint) Cudd_zddVarsFromBddVars((DdManager *) dd, multiplicity);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1IsConstant
	(JNIEnv *, jclass, jlong node) {

	return (jint) Cudd_IsConstant((DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1IsNonConstant
	(JNIEnv *, jclass, jlong f) {

	return (jint) Cudd_IsNonConstant((DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1T
	(JNIEnv *, jclass, jlong node) {

	return (jlong) Cudd_T((DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1E
	(JNIEnv *, jclass, jlong node) {

	return (jlong) Cudd_E((DdNode *) node);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ResetStartTime
	(JNIEnv *, jclass, jlong unique) {

	Cudd_ResetStartTime((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1UpdateTimeLimit
	(JNIEnv *, jclass, jlong unique) {

	Cudd_UpdateTimeLimit((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1UnsetTimeLimit
	(JNIEnv *, jclass, jlong unique) {

	Cudd_UnsetTimeLimit((DdManager *) unique);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1TimeLimited
	(JNIEnv *, jclass, jlong unique) {

	return (jint) Cudd_TimeLimited((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1UnregisterTerminationCallback
	(JNIEnv *, jclass, jlong unique) {

	Cudd_UnregisterTerminationCallback((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1UnregisterOutOfMemoryCallback
	(JNIEnv *, jclass, jlong unique) {

	Cudd_UnregisterOutOfMemoryCallback((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1AutodynDisable
	(JNIEnv *, jclass, jlong unique) {

	Cudd_AutodynDisable((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1AutodynDisableZdd
	(JNIEnv *, jclass, jlong unique) {

	Cudd_AutodynDisableZdd((DdManager *) unique);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddRealignmentEnabled
	(JNIEnv *, jclass, jlong unique) {

	return (jint) Cudd_zddRealignmentEnabled((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddRealignEnable
	(JNIEnv *, jclass, jlong unique) {

	Cudd_zddRealignEnable((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddRealignDisable
	(JNIEnv *, jclass, jlong unique) {

	Cudd_zddRealignDisable((DdManager *) unique);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddRealignmentEnabled
	(JNIEnv *, jclass, jlong unique) {

	return (jint) Cudd_bddRealignmentEnabled((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddRealignEnable
	(JNIEnv *, jclass, jlong unique) {

	Cudd_bddRealignEnable((DdManager *) unique);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddRealignDisable
	(JNIEnv *, jclass, jlong unique) {

	Cudd_bddRealignDisable((DdManager *) unique);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadOne
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadOne((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadZddOne
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jlong) Cudd_ReadZddOne((DdManager *) dd, i);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadZero
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadZero((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadLogicZero
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadLogicZero((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadPlusInfinity
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadPlusInfinity((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadMinusInfinity
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadMinusInfinity((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadBackground
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadBackground((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetBackground
	(JNIEnv *, jclass, jlong dd, jlong bck) {

	Cudd_SetBackground((DdManager *) dd, (DdNode *) bck);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadCacheUsedSlots
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadCacheUsedSlots((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadCacheLookUps
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadCacheLookUps((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadCacheHits
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadCacheHits((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadRecursiveCalls
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadRecursiveCalls((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadSize
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadSize((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadZddSize
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadZddSize((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadUsedSlots
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadUsedSlots((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ExpectedUsedSlots
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ExpectedUsedSlots((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadReorderingTime
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadReorderingTime((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadGarbageCollections
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadGarbageCollections((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadGarbageCollectionTime
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadGarbageCollectionTime((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadNodesFreed
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadNodesFreed((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadNodesDropped
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadNodesDropped((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadUniqueLookUps
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadUniqueLookUps((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadUniqueLinks
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadUniqueLinks((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadSiftMaxVar
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadSiftMaxVar((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetSiftMaxVar
	(JNIEnv *, jclass, jlong dd, jint smv) {

	Cudd_SetSiftMaxVar((DdManager *) dd, smv);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadSiftMaxSwap
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadSiftMaxSwap((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetSiftMaxSwap
	(JNIEnv *, jclass, jlong dd, jint sms) {

	Cudd_SetSiftMaxSwap((DdManager *) dd, sms);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadMaxGrowth
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadMaxGrowth((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetMaxGrowth
	(JNIEnv *, jclass, jlong dd, jdouble mg) {

	Cudd_SetMaxGrowth((DdManager *) dd, mg);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadMaxGrowthAlternate
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadMaxGrowthAlternate((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetMaxGrowthAlternate
	(JNIEnv *, jclass, jlong dd, jdouble mg) {

	Cudd_SetMaxGrowthAlternate((DdManager *) dd, mg);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadReorderingCycle
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadReorderingCycle((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetReorderingCycle
	(JNIEnv *, jclass, jlong dd, jint cycle) {

	Cudd_SetReorderingCycle((DdManager *) dd, cycle);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadPerm
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jint) Cudd_ReadPerm((DdManager *) dd, i);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadPermZdd
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jint) Cudd_ReadPermZdd((DdManager *) dd, i);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadInvPerm
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jint) Cudd_ReadInvPerm((DdManager *) dd, i);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadInvPermZdd
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jint) Cudd_ReadInvPermZdd((DdManager *) dd, i);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadVars
	(JNIEnv *, jclass, jlong dd, jint i) {

	return (jlong) Cudd_ReadVars((DdManager *) dd, i);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1GarbageCollectionEnabled
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_GarbageCollectionEnabled((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1EnableGarbageCollection
	(JNIEnv *, jclass, jlong dd) {

	Cudd_EnableGarbageCollection((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DisableGarbageCollection
	(JNIEnv *, jclass, jlong dd) {

	Cudd_DisableGarbageCollection((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DeadAreCounted
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_DeadAreCounted((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1TurnOnCountDead
	(JNIEnv *, jclass, jlong dd) {

	Cudd_TurnOnCountDead((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1TurnOffCountDead
	(JNIEnv *, jclass, jlong dd) {

	Cudd_TurnOffCountDead((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadRecomb
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadRecomb((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetRecomb
	(JNIEnv *, jclass, jlong dd, jint recomb) {

	Cudd_SetRecomb((DdManager *) dd, recomb);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadSymmviolation
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadSymmviolation((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetSymmviolation
	(JNIEnv *, jclass, jlong dd, jint symmviolation) {

	Cudd_SetSymmviolation((DdManager *) dd, symmviolation);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadArcviolation
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadArcviolation((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetArcviolation
	(JNIEnv *, jclass, jlong dd, jint arcviolation) {

	Cudd_SetArcviolation((DdManager *) dd, arcviolation);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadPopulationSize
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadPopulationSize((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetPopulationSize
	(JNIEnv *, jclass, jlong dd, jint populationSize) {

	Cudd_SetPopulationSize((DdManager *) dd, populationSize);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadNumberXovers
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadNumberXovers((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetNumberXovers
	(JNIEnv *, jclass, jlong dd, jint numberXovers) {

	Cudd_SetNumberXovers((DdManager *) dd, numberXovers);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadPeakNodeCount
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadPeakNodeCount((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadPeakLiveNodeCount
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadPeakLiveNodeCount((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadNodeCount
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_ReadNodeCount((DdManager *) dd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddReadNodeCount
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_zddReadNodeCount((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1EnableReorderingReporting
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_EnableReorderingReporting((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DisableReorderingReporting
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_DisableReorderingReporting((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReorderingReporting
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReorderingReporting((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1EnableOrderingMonitoring
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_EnableOrderingMonitoring((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DisableOrderingMonitoring
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_DisableOrderingMonitoring((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1OrderingMonitoring
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_OrderingMonitoring((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadApplicationHook
	(JNIEnv *, jclass, jlong dd) {

	Cudd_ReadApplicationHook((DdManager *) dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ClearErrorCode
	(JNIEnv *, jclass, jlong dd) {

	Cudd_ClearErrorCode((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadSwapSteps
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_ReadSwapSteps((DdManager *) dd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddBindVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddBindVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddUnbindVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddUnbindVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddVarIsBound
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddVarIsBound((DdManager *) dd, index);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addExistAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong cube) {

	return (jlong) Cudd_addExistAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) cube);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addUnivAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong cube) {

	return (jlong) Cudd_addUnivAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) cube);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addOrAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong cube) {

	return (jlong) Cudd_addOrAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) cube);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addLog
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_addLog((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addFindMax
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_addFindMax((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addFindMin
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_addFindMin((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addIthBit
	(JNIEnv *, jclass, jlong dd, jlong f, jint bit) {

	return (jlong) Cudd_addIthBit((DdManager *) dd, (DdNode *) f, bit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addScalarInverse
	(JNIEnv *, jclass, jlong dd, jlong f, jlong epsilon) {

	return (jlong) Cudd_addScalarInverse((DdManager *) dd, (DdNode *) f, (DdNode *) epsilon);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addIte
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong h) {

	return (jlong) Cudd_addIte((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) h);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addIteConstant
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong h) {

	return (jlong) Cudd_addIteConstant((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) h);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addEvalConst
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_addEvalConst((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addLeq
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jint) Cudd_addLeq((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addCmpl
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_addCmpl((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addNegate
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_addNegate((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addRoundOff
	(JNIEnv *, jclass, jlong dd, jlong f, jint N) {

	return (jlong) Cudd_addRoundOff((DdManager *) dd, (DdNode *) f, N);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addResidue
	(JNIEnv *, jclass, jlong dd, jint n, jint m, jint options, jint top) {

	return (jlong) Cudd_addResidue((DdManager *) dd, n, m, options, top);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddAndAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong g, jlong cube) {

	return (jlong) Cudd_bddAndAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) g, (DdNode *) cube);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ApaNumberOfDigits
	(JNIEnv *, jclass, jint binaryDigits) {

	return (jint) Cudd_ApaNumberOfDigits(binaryDigits);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1UnderApprox
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold, jint safe, jdouble quality) {

	return (jlong) Cudd_UnderApprox((DdManager *) dd, (DdNode *) f, numVars, threshold, safe, quality);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1OverApprox
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold, jint safe, jdouble quality) {

	return (jlong) Cudd_OverApprox((DdManager *) dd, (DdNode *) f, numVars, threshold, safe, quality);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1RemapUnderApprox
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold, jdouble quality) {

	return (jlong) Cudd_RemapUnderApprox((DdManager *) dd, (DdNode *) f, numVars, threshold, quality);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1RemapOverApprox
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold, jdouble quality) {

	return (jlong) Cudd_RemapOverApprox((DdManager *) dd, (DdNode *) f, numVars, threshold, quality);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1BiasedUnderApprox
	(JNIEnv *, jclass, jlong dd, jlong f, jlong b, jint numVars, jint threshold, jdouble quality1, jdouble quality0) {

	return (jlong) Cudd_BiasedUnderApprox((DdManager *) dd, (DdNode *) f, (DdNode *) b, numVars, threshold, quality1, quality0);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1BiasedOverApprox
	(JNIEnv *, jclass, jlong dd, jlong f, jlong b, jint numVars, jint threshold, jdouble quality1, jdouble quality0) {

	return (jlong) Cudd_BiasedOverApprox((DdManager *) dd, (DdNode *) f, (DdNode *) b, numVars, threshold, quality1, quality0);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddExistAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong cube) {

	return (jlong) Cudd_bddExistAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) cube);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddXorExistAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong g, jlong cube) {

	return (jlong) Cudd_bddXorExistAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) g, (DdNode *) cube);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddUnivAbstract
	(JNIEnv *, jclass, jlong manager, jlong f, jlong cube) {

	return (jlong) Cudd_bddUnivAbstract((DdManager *) manager, (DdNode *) f, (DdNode *) cube);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddBooleanDiff
	(JNIEnv *, jclass, jlong manager, jlong f, jint x) {

	return (jlong) Cudd_bddBooleanDiff((DdManager *) manager, (DdNode *) f, x);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddVarIsDependent
	(JNIEnv *, jclass, jlong dd, jlong f, jlong var) {

	return (jint) Cudd_bddVarIsDependent((DdManager *) dd, (DdNode *) f, (DdNode *) var);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddCorrelation
	(JNIEnv *, jclass, jlong manager, jlong f, jlong g) {

	return (jdouble) Cudd_bddCorrelation((DdManager *) manager, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddCorrelationWeights
	(JNIEnv *env, jclass, jlong manager, jlong f, jlong g, jdoubleArray prob) {

	jdouble *probArrayElements = env->GetDoubleArrayElements(prob, 0);
	jdouble result = (jdouble) Cudd_bddCorrelationWeights((DdManager *) manager, (DdNode *) f, (DdNode *) g, reinterpret_cast<double *>(probArrayElements));
	env->ReleaseDoubleArrayElements(prob, probArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIte
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong h) {

	return (jlong) Cudd_bddIte((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) h);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIteConstant
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong h) {

	return (jlong) Cudd_bddIteConstant((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) h);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIntersect
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddIntersect((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddAnd
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddAnd((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddOr
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddOr((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddNand
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddNand((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddNor
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddNor((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddXor
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddXor((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddXnor
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddXnor((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddLeq
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jint) Cudd_bddLeq((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addBddIthBit
	(JNIEnv *, jclass, jlong dd, jlong f, jint bit) {

	return (jlong) Cudd_addBddIthBit((DdManager *) dd, (DdNode *) f, bit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1BddToAdd
	(JNIEnv *, jclass, jlong dd, jlong B) {

	return (jlong) Cudd_BddToAdd((DdManager *) dd, (DdNode *) B);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addBddPattern
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_addBddPattern((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddTransfer
	(JNIEnv *, jclass, jlong ddSource, jlong ddDestination, jlong f) {

	return (jlong) Cudd_bddTransfer((DdManager *) ddSource, (DdManager *) ddDestination, (DdNode *) f);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DebugCheck
	(JNIEnv *, jclass, jlong table) {

	return (jint) Cudd_DebugCheck((DdManager *) table);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CheckKeys
	(JNIEnv *, jclass, jlong table) {

	return (jint) Cudd_CheckKeys((DdManager *) table);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddClippingAnd
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jint maxDepth, jint direction) {

	return (jlong) Cudd_bddClippingAnd((DdManager *) dd, (DdNode *) f, (DdNode *) g, maxDepth, direction);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddClippingAndAbstract
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong cube, jint maxDepth, jint direction) {

	return (jlong) Cudd_bddClippingAndAbstract((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) cube, maxDepth, direction);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Cofactor
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_Cofactor((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CheckCube
	(JNIEnv *, jclass, jlong dd, jlong g) {

	return (jint) Cudd_CheckCube((DdManager *) dd, (DdNode *) g);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1VarsAreSymmetric
	(JNIEnv *, jclass, jlong dd, jlong f, jint index1, jint index2) {

	return (jint) Cudd_VarsAreSymmetric((DdManager *) dd, (DdNode *) f, index1, index2);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddCompose
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jint v) {

	return (jlong) Cudd_bddCompose((DdManager *) dd, (DdNode *) f, (DdNode *) g, v);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addCompose
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jint v) {

	return (jlong) Cudd_addCompose((DdManager *) dd, (DdNode *) f, (DdNode *) g, v);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addPermute
	(JNIEnv *env, jclass, jlong manager, jlong node, jintArray permut) {

	jint *permutArrayElements = env->GetIntArrayElements(permut, 0);
	jlong result = (jlong) Cudd_addPermute((DdManager *) manager, (DdNode *) node, reinterpret_cast<int *>(permutArrayElements));
	env->ReleaseIntArrayElements(permut, permutArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddPermute
	(JNIEnv *env, jclass, jlong manager, jlong node, jintArray permut) {

	jint *permutArrayElements = env->GetIntArrayElements(permut, 0);
	jlong result = (jlong) Cudd_bddPermute((DdManager *) manager, (DdNode *) node, reinterpret_cast<int *>(permutArrayElements));
	env->ReleaseIntArrayElements(permut, permutArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddVarMap
	(JNIEnv *, jclass, jlong manager, jlong f) {

	return (jlong) Cudd_bddVarMap((DdManager *) manager, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1FindEssential
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_FindEssential((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsVarEssential
	(JNIEnv *, jclass, jlong manager, jlong f, jint id, jint phase) {

	return (jint) Cudd_bddIsVarEssential((DdManager *) manager, (DdNode *) f, id, phase);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddConstrain
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_bddConstrain((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddRestrict
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_bddRestrict((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddNPAnd
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_bddNPAnd((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addConstrain
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_addConstrain((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addRestrict
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_addRestrict((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddLICompaction
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_bddLICompaction((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSqueeze
	(JNIEnv *, jclass, jlong dd, jlong l, jlong u) {

	return (jlong) Cudd_bddSqueeze((DdManager *) dd, (DdNode *) l, (DdNode *) u);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddInterpolate
	(JNIEnv *, jclass, jlong dd, jlong l, jlong u) {

	return (jlong) Cudd_bddInterpolate((DdManager *) dd, (DdNode *) l, (DdNode *) u);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddMinimize
	(JNIEnv *, jclass, jlong dd, jlong f, jlong c) {

	return (jlong) Cudd_bddMinimize((DdManager *) dd, (DdNode *) f, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SubsetCompress
	(JNIEnv *, jclass, jlong dd, jlong f, jint nvars, jint threshold) {

	return (jlong) Cudd_SubsetCompress((DdManager *) dd, (DdNode *) f, nvars, threshold);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SupersetCompress
	(JNIEnv *, jclass, jlong dd, jlong f, jint nvars, jint threshold) {

	return (jlong) Cudd_SupersetCompress((DdManager *) dd, (DdNode *) f, nvars, threshold);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Quit
	(JNIEnv *, jclass, jlong unique) {

	Cudd_Quit((DdManager *) unique);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1PrintLinear
	(JNIEnv *, jclass, jlong table) {

	return (jint) Cudd_PrintLinear((DdManager *) table);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadLinear
	(JNIEnv *, jclass, jlong table, jint x, jint y) {

	return (jint) Cudd_ReadLinear((DdManager *) table, x, y);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddLiteralSetIntersection
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_bddLiteralSetIntersection((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addOuterSum
	(JNIEnv *, jclass, jlong dd, jlong M, jlong r, jlong c) {

	return (jlong) Cudd_addOuterSum((DdManager *) dd, (DdNode *) M, (DdNode *) r, (DdNode *) c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CProjection
	(JNIEnv *, jclass, jlong dd, jlong R, jlong Y) {

	return (jlong) Cudd_CProjection((DdManager *) dd, (DdNode *) R, (DdNode *) Y);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1MinHammingDist
	(JNIEnv *env, jclass, jlong dd, jlong f, jintArray minterm, jint upperBound) {

	jint *mintermArrayElements = env->GetIntArrayElements(minterm, 0);
	jint result = (jint) Cudd_MinHammingDist((DdManager *) dd, (DdNode *) f, reinterpret_cast<int *>(mintermArrayElements), upperBound);
	env->ReleaseIntArrayElements(minterm, mintermArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddClosestCube
	(JNIEnv *env, jclass, jlong dd, jlong f, jlong g, jintArray distance) {

	jint *distanceArrayElements = env->GetIntArrayElements(distance, 0);
	jlong result = (jlong) Cudd_bddClosestCube((DdManager *) dd, (DdNode *) f, (DdNode *) g, reinterpret_cast<int *>(distanceArrayElements));
	env->ReleaseIntArrayElements(distance, distanceArrayElements, 0);
	return result;
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Ref
	(JNIEnv *, jclass, jlong n) {

	Cudd_Ref((DdNode *) n);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1RecursiveDeref
	(JNIEnv *, jclass, jlong table, jlong n) {

	Cudd_RecursiveDeref((DdManager *) table, (DdNode *) n);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1IterDerefBdd
	(JNIEnv *, jclass, jlong table, jlong n) {

	Cudd_IterDerefBdd((DdManager *) table, (DdNode *) n);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DelayedDerefBdd
	(JNIEnv *, jclass, jlong table, jlong n) {

	Cudd_DelayedDerefBdd((DdManager *) table, (DdNode *) n);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1RecursiveDerefZdd
	(JNIEnv *, jclass, jlong table, jlong n) {

	Cudd_RecursiveDerefZdd((DdManager *) table, (DdNode *) n);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Deref
	(JNIEnv *, jclass, jlong node) {

	Cudd_Deref((DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CheckZeroRef
	(JNIEnv *, jclass, jlong manager) {

	return (jint) Cudd_CheckZeroRef((DdManager *) manager);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ShuffleHeap
	(JNIEnv *env, jclass, jlong table, jintArray permutation) {

	jint *permutationArrayElements = env->GetIntArrayElements(permutation, 0);
	jint result = (jint) Cudd_ShuffleHeap((DdManager *) table, reinterpret_cast<int *>(permutationArrayElements));
	env->ReleaseIntArrayElements(permutation, permutationArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Eval
	(JNIEnv *env, jclass, jlong dd, jlong f, jintArray inputs) {

	jint *inputsArrayElements = env->GetIntArrayElements(inputs, 0);
	jlong result = (jlong) Cudd_Eval((DdManager *) dd, (DdNode *) f, reinterpret_cast<int *>(inputsArrayElements));
	env->ReleaseIntArrayElements(inputs, inputsArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ShortestPath
	(JNIEnv *env, jclass, jlong manager, jlong f, jintArray weight, jintArray support, jintArray length) {

	jint *weightArrayElements = env->GetIntArrayElements(weight, 0);
	jint *supportArrayElements = env->GetIntArrayElements(support, 0);
	jint *lengthArrayElements = env->GetIntArrayElements(length, 0);
	jlong result = (jlong) Cudd_ShortestPath((DdManager *) manager, (DdNode *) f, reinterpret_cast<int *>(weightArrayElements), reinterpret_cast<int *>(supportArrayElements), reinterpret_cast<int *>(lengthArrayElements));
	env->ReleaseIntArrayElements(weight, weightArrayElements, 0);
	env->ReleaseIntArrayElements(support, supportArrayElements, 0);
	env->ReleaseIntArrayElements(length, lengthArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1LargestCube
	(JNIEnv *env, jclass, jlong manager, jlong f, jintArray length) {

	jint *lengthArrayElements = env->GetIntArrayElements(length, 0);
	jlong result = (jlong) Cudd_LargestCube((DdManager *) manager, (DdNode *) f, reinterpret_cast<int *>(lengthArrayElements));
	env->ReleaseIntArrayElements(length, lengthArrayElements, 0);
	return result;
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ShortestLength
	(JNIEnv *env, jclass, jlong manager, jlong f, jintArray weight) {

	jint *weightArrayElements = env->GetIntArrayElements(weight, 0);
	jint result = (jint) Cudd_ShortestLength((DdManager *) manager, (DdNode *) f, reinterpret_cast<int *>(weightArrayElements));
	env->ReleaseIntArrayElements(weight, weightArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Decreasing
	(JNIEnv *, jclass, jlong dd, jlong f, jint i) {

	return (jlong) Cudd_Decreasing((DdManager *) dd, (DdNode *) f, i);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Increasing
	(JNIEnv *, jclass, jlong dd, jlong f, jint i) {

	return (jlong) Cudd_Increasing((DdManager *) dd, (DdNode *) f, i);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1EquivDC
	(JNIEnv *, jclass, jlong dd, jlong F, jlong G, jlong D) {

	return (jint) Cudd_EquivDC((DdManager *) dd, (DdNode *) F, (DdNode *) G, (DdNode *) D);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddLeqUnless
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong D) {

	return (jint) Cudd_bddLeqUnless((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) D);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddMakePrime
	(JNIEnv *, jclass, jlong dd, jlong cube, jlong f) {

	return (jlong) Cudd_bddMakePrime((DdManager *) dd, (DdNode *) cube, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddMaximallyExpand
	(JNIEnv *, jclass, jlong dd, jlong lb, jlong ub, jlong f) {

	return (jlong) Cudd_bddMaximallyExpand((DdManager *) dd, (DdNode *) lb, (DdNode *) ub, (DdNode *) f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddLargestPrimeUnate
	(JNIEnv *, jclass, jlong dd, jlong f, jlong phaseBdd) {

	return (jlong) Cudd_bddLargestPrimeUnate((DdManager *) dd, (DdNode *) f, (DdNode *) phaseBdd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SubsetHeavyBranch
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold) {

	return (jlong) Cudd_SubsetHeavyBranch((DdManager *) dd, (DdNode *) f, numVars, threshold);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SupersetHeavyBranch
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold) {

	return (jlong) Cudd_SupersetHeavyBranch((DdManager *) dd, (DdNode *) f, numVars, threshold);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SubsetShortPaths
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold, jint hardlimit) {

	return (jlong) Cudd_SubsetShortPaths((DdManager *) dd, (DdNode *) f, numVars, threshold, hardlimit);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SupersetShortPaths
	(JNIEnv *, jclass, jlong dd, jlong f, jint numVars, jint threshold, jint hardlimit) {

	return (jlong) Cudd_SupersetShortPaths((DdManager *) dd, (DdNode *) f, numVars, threshold, hardlimit);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SymmProfile
	(JNIEnv *, jclass, jlong table, jint lower, jint upper) {

	Cudd_SymmProfile((DdManager *) table, lower, upper);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Reserve
	(JNIEnv *, jclass, jlong manager, jint amount) {

	return (jint) Cudd_Reserve((DdManager *) manager, amount);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1PrintMinterm
	(JNIEnv *, jclass, jlong manager, jlong node) {

	return (jint) Cudd_PrintMinterm((DdManager *) manager, (DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddPrintCover
	(JNIEnv *, jclass, jlong dd, jlong l, jlong u) {

	return (jint) Cudd_bddPrintCover((DdManager *) dd, (DdNode *) l, (DdNode *) u);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1PrintDebug
	(JNIEnv *, jclass, jlong dd, jlong f, jint n, jint pr) {

	return (jint) Cudd_PrintDebug((DdManager *) dd, (DdNode *) f, n, pr);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1PrintSummary
	(JNIEnv *, jclass, jlong dd, jlong f, jint n, jint mode) {

	return (jint) Cudd_PrintSummary((DdManager *) dd, (DdNode *) f, n, mode);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1DagSize
	(JNIEnv *, jclass, jlong node) {

	return (jint) Cudd_DagSize((DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1EstimateCofactor
	(JNIEnv *, jclass, jlong dd, jlong node, jint i, jint phase) {

	return (jint) Cudd_EstimateCofactor((DdManager *) dd, (DdNode *) node, i, phase);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1EstimateCofactorSimple
	(JNIEnv *, jclass, jlong node, jint i) {

	return (jint) Cudd_EstimateCofactorSimple((DdNode *) node, i);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CountMinterm
	(JNIEnv *, jclass, jlong manager, jlong node, jint nvars) {

	return (jdouble) Cudd_CountMinterm((DdManager *) manager, (DdNode *) node, nvars);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CountPath
	(JNIEnv *, jclass, jlong node) {

	return (jdouble) Cudd_CountPath((DdNode *) node);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CountPathsToNonZero
	(JNIEnv *, jclass, jlong node) {

	return (jdouble) Cudd_CountPathsToNonZero((DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Support
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_Support((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SupportSize
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jint) Cudd_SupportSize((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CountLeaves
	(JNIEnv *, jclass, jlong node) {

	return (jint) Cudd_CountLeaves((DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1CubeArrayToBdd
	(JNIEnv *env, jclass, jlong dd, jintArray ArrayElements) {

	jint *arrayArrayElements = env->GetIntArrayElements(ArrayElements, 0);
	jlong result = (jlong) Cudd_CubeArrayToBdd((DdManager *) dd, reinterpret_cast<int *>(arrayArrayElements));
	env->ReleaseIntArrayElements(ArrayElements, arrayArrayElements, 0);
	return result;
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1BddToCubeArray
	(JNIEnv *env, jclass, jlong dd, jlong cube, jintArray ArrayElements) {

	jint *arrayArrayElements = env->GetIntArrayElements(ArrayElements, 0);
	jint result = (jint) Cudd_BddToCubeArray((DdManager *) dd, (DdNode *) cube, reinterpret_cast<int *>(arrayArrayElements));
	env->ReleaseIntArrayElements(ArrayElements, arrayArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1IndicesToCube
	(JNIEnv *env, jclass, jlong dd, jintArray ArrayElements, jint n) {

	jint *arrayArrayElements = env->GetIntArrayElements(ArrayElements, 0);
	jlong result = (jlong) Cudd_IndicesToCube((DdManager *) dd, reinterpret_cast<int *>(arrayArrayElements), n);
	env->ReleaseIntArrayElements(ArrayElements, arrayArrayElements, 0);
	return result;
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1AverageDistance
	(JNIEnv *, jclass, jlong dd) {

	return (jdouble) Cudd_AverageDistance((DdManager *) dd);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Density
	(JNIEnv *, jclass, jlong dd, jlong f, jint nvars) {

	return (jdouble) Cudd_Density((DdManager *) dd, (DdNode *) f, nvars);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddCount
	(JNIEnv *, jclass, jlong zdd, jlong P) {

	return (jint) Cudd_zddCount((DdManager *) zdd, (DdNode *) P);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddCountDouble
	(JNIEnv *, jclass, jlong zdd, jlong P) {

	return (jdouble) Cudd_zddCountDouble((DdManager *) zdd, (DdNode *) P);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddProduct
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_zddProduct((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddUnateProduct
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_zddUnateProduct((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddWeakDiv
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_zddWeakDiv((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddDivide
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_zddDivide((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddWeakDivF
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_zddWeakDivF((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddDivideF
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g) {

	return (jlong) Cudd_zddDivideF((DdManager *) dd, (DdNode *) f, (DdNode *) g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddComplement
	(JNIEnv *, jclass, jlong dd, jlong node) {

	return (jlong) Cudd_zddComplement((DdManager *) dd, (DdNode *) node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsop
	(JNIEnv *, jclass, jlong dd, jlong L, jlong U) {

	return (jlong) Cudd_bddIsop((DdManager *) dd, (DdNode *) L, (DdNode *) U);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1MakeBddFromZddCover
	(JNIEnv *, jclass, jlong dd, jlong node) {

	return (jlong) Cudd_MakeBddFromZddCover((DdManager *) dd, (DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddDagSize
	(JNIEnv *, jclass, jlong p_node) {

	return (jint) Cudd_zddDagSize((DdNode *) p_node);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddCountMinterm
	(JNIEnv *, jclass, jlong zdd, jlong node, jint path) {

	return (jdouble) Cudd_zddCountMinterm((DdManager *) zdd, (DdNode *) node, path);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddPrintSubtable
	(JNIEnv *, jclass, jlong table) {

	Cudd_zddPrintSubtable((DdManager *) table);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddPortFromBdd
	(JNIEnv *, jclass, jlong dd, jlong B) {

	return (jlong) Cudd_zddPortFromBdd((DdManager *) dd, (DdNode *) B);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddPortToBdd
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_zddPortToBdd((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddShuffleHeap
	(JNIEnv *env, jclass, jlong table, jintArray permutation) {

	jint *permutationArrayElements = env->GetIntArrayElements(permutation, 0);
	jint result = (jint) Cudd_zddShuffleHeap((DdManager *) table, reinterpret_cast<int *>(permutationArrayElements));
	env->ReleaseIntArrayElements(permutation, permutationArrayElements, 0);
	return result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddIte
	(JNIEnv *, jclass, jlong dd, jlong f, jlong g, jlong h) {

	return (jlong) Cudd_zddIte((DdManager *) dd, (DdNode *) f, (DdNode *) g, (DdNode *) h);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddUnion
	(JNIEnv *, jclass, jlong dd, jlong P, jlong Q) {

	return (jlong) Cudd_zddUnion((DdManager *) dd, (DdNode *) P, (DdNode *) Q);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddIntersect
	(JNIEnv *, jclass, jlong dd, jlong P, jlong Q) {

	return (jlong) Cudd_zddIntersect((DdManager *) dd, (DdNode *) P, (DdNode *) Q);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddDiff
	(JNIEnv *, jclass, jlong dd, jlong P, jlong Q) {

	return (jlong) Cudd_zddDiff((DdManager *) dd, (DdNode *) P, (DdNode *) Q);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddDiffConst
	(JNIEnv *, jclass, jlong zdd, jlong P, jlong Q) {

	return (jlong) Cudd_zddDiffConst((DdManager *) zdd, (DdNode *) P, (DdNode *) Q);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddSubset1
	(JNIEnv *, jclass, jlong dd, jlong P, jint var) {

	return (jlong) Cudd_zddSubset1((DdManager *) dd, (DdNode *) P, var);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddSubset0
	(JNIEnv *, jclass, jlong dd, jlong P, jint var) {

	return (jlong) Cudd_zddSubset0((DdManager *) dd, (DdNode *) P, var);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddChange
	(JNIEnv *, jclass, jlong dd, jlong P, jint var) {

	return (jlong) Cudd_zddChange((DdManager *) dd, (DdNode *) P, var);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddSymmProfile
	(JNIEnv *, jclass, jlong table, jint lower, jint upper) {

	Cudd_zddSymmProfile((DdManager *) table, lower, upper);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddPrintMinterm
	(JNIEnv *, jclass, jlong zdd, jlong node) {

	return (jint) Cudd_zddPrintMinterm((DdManager *) zdd, (DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddPrintCover
	(JNIEnv *, jclass, jlong zdd, jlong node) {

	return (jint) Cudd_zddPrintCover((DdManager *) zdd, (DdNode *) node);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddPrintDebug
	(JNIEnv *, jclass, jlong zdd, jlong f, jint n, jint pr) {

	return (jint) Cudd_zddPrintDebug((DdManager *) zdd, (DdNode *) f, n, pr);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddSupport
	(JNIEnv *, jclass, jlong dd, jlong f) {

	return (jlong) Cudd_zddSupport((DdManager *) dd, (DdNode *) f);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetPiVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddSetPiVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetPsVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddSetPsVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetNsVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddSetNsVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsPiVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddIsPiVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsPsVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddIsPsVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsNsVar
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddIsNsVar((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetPairIndex
	(JNIEnv *, jclass, jlong dd, jint index, jint pairIndex) {

	return (jint) Cudd_bddSetPairIndex((DdManager *) dd, index, pairIndex);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddReadPairIndex
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddReadPairIndex((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetVarToBeGrouped
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddSetVarToBeGrouped((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetVarHardGroup
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddSetVarHardGroup((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddResetVarToBeGrouped
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddResetVarToBeGrouped((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsVarToBeGrouped
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddIsVarToBeGrouped((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddSetVarToBeUngrouped
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddSetVarToBeUngrouped((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsVarToBeUngrouped
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddIsVarToBeUngrouped((DdManager *) dd, index);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddIsVarHardGroup
	(JNIEnv *, jclass, jlong dd, jint index) {

	return (jint) Cudd_bddIsVarHardGroup((DdManager *) dd, index);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1Regular
	(JNIEnv *, jclass, jlong dd) {

	return (jlong) Cudd_Regular((DdNode *) dd);
}