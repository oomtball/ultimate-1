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
#include "apply.h"

/*
 * Pre-defined for standard methods of ADD
 * Ordinal values derived from Java enum 'DD_AOP':
 * Cudd_addPlus           0
 * Cudd_addTimes          1
 * Cudd_addThreshold      2
 * Cudd_addSetNZ          3
 * Cudd_addDivide         4
 * Cudd_addMinus          5
 * Cudd_addMinimum        6
 * Cudd_addMaximum        7
 * Cudd_addOneZeroMaximum 8
 * Cudd_addDiff           9
 * Cudd_addAgreement      10
 * Cudd_addOr             11
 * Cudd_addNand           12
 * Cudd_addNor            13
 * Cudd_addXor            14
 * Cudd_addXnor           15
 */
JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addApply__JIJJ
	(JNIEnv *, jclass, jlong ddManager, jint op, jlong f, jlong g) {

	DD_AOP aop = NULL;
	switch (op) {
		case 0: aop = Cudd_addPlus; break;
		case 1: aop = Cudd_addTimes; break;
		case 2: aop = Cudd_addThreshold; break;
		case 3: aop = Cudd_addSetNZ; break;
		case 4: aop = Cudd_addDivide; break;
		case 5: aop = Cudd_addMinus; break;
		case 6: aop = Cudd_addMinimum; break;
		case 7: aop = Cudd_addMaximum; break;
		case 8: aop = Cudd_addOneZeroMaximum; break;
		case 9: aop = Cudd_addDiff; break;
		case 10: aop = Cudd_addAgreement; break;
		case 11: aop = Cudd_addOr; break;
		case 12: aop = Cudd_addNand; break;
		case 13: aop = Cudd_addNor; break;
		case 14: aop = Cudd_addXor; break;
		case 15: aop = Cudd_addXnor; break;
	}
	if (aop != NULL)
		return (jlong) Cudd_addApply((DdManager *) ddManager, aop, (DdNode *) f, (DdNode *) g);
	return (jlong) NULL;
}

/* 
 * CUDD caches Cudd_addApply results internally and identifies operations by their function pointer. For this reason 
 * every Java callback must be wrapped by a separate C function. We use a pool of n functions and clear CUDD's cache 
 * only once all of them are taken. 
 */

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addApply__JLinfo_scce_addlib_apply_DD_1AOP_1Fn_2JJ
	(JNIEnv *env, jclass, jlong ddManager, jobject op, jlong f, jlong g) {
	
	/* Get operation identifier */
	jclass opClass = env->GetObjectClass(op);
	jmethodID opMID = env->GetMethodID(opClass, "id", "()J");
	jlong opID = env->CallLongMethod(op, opMID);
	
	/* Ensure to associate a C function with this operation identifier */
	DD_AOP aop = NULL;
	if (addlib_applyAdapterFunctionByOpID.find(opID) == addlib_applyAdapterFunctionByOpID.end()) {

		/* Clear cache if neccessary */
		if (addlib_applyAdapterNextFunction >= ADDLIB_APPLY_ADAPTER_FUNCTION_N) {
			addlib_clearCache((DdManager *) ddManager);
			addlib_applyAdapterNextFunction = 0;
			addlib_applyAdapterFunctionByOpID.clear();
		}

		/* Associate this operation identifier with the next unassigned C function */
		aop = addlib_applyAdapterFunction(addlib_applyAdapterNextFunction++);
		addlib_applyAdapterFunctionByOpID[opID] = aop;
	} 

	/* Lookup in cache if not yet known */
	if (aop == NULL)
		aop = addlib_applyAdapterFunctionByOpID[opID];

	/* Invoke Cudd_addApply with the associated C function */
	addlib_applyAdapterEnv = env;
	addlib_applyAdapterOpObj = op;
	addlib_applyAdapterOpMID = env->GetMethodID(opClass, "applyAndPostponeRtException", "(JJJ)J");
	return (jlong) Cudd_addApply(
		(DdManager *) ddManager, 
		aop, 
		(DdNode *) f, 
		(DdNode *) g);
}

/* 
 * Ordinal values derived from Java enum 'DD_MAOP':
 * Cudd_addLog 0
 */
JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addMonadicApply__JIJ
	(JNIEnv *, jclass, jlong ddManager, jint op, jlong f) {

	DD_MAOP maop = NULL;
	switch (op) {
		case 0: maop = Cudd_addLog; break;
	}
	if (maop != NULL)
		return (jlong) Cudd_addMonadicApply((DdManager *) ddManager, maop, (DdNode *) f);
	return (jlong) NULL;
}

/* 
 * CUDD caches Cudd_addMonadicApply results internally and identifies operations by their function pointer. For this 
 * reason every Java callback must be wrapped by a separate C function. We use a pool of n functions and clear CUDD's 
 * cache only once all of them are taken. 
 */

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addMonadicApply__JLinfo_scce_addlib_apply_DD_1MAOP_1Fn_2J
	(JNIEnv *env, jclass, jlong ddManager, jobject op, jlong f) {

	/* Get operation identifier */
	jclass opClass = env->GetObjectClass(op);
	jmethodID opMID = env->GetMethodID(opClass, "id", "()J");
	jlong opID = env->CallLongMethod(op, opMID);

	/* Ensure to associate a C function with this operation identifier */
	DD_MAOP maop = NULL;
	if (addlib_monadicApplyAdapterFunctionByOpID.find(opID) == addlib_monadicApplyAdapterFunctionByOpID.end()) {

		/* Clear cache if neccessary */
		if (addlib_monadicApplyAdapterNextFunction >= ADDLIB_MONADIC_APPLY_ADAPTER_FUNCTION_N) {
			addlib_clearCache((DdManager *) ddManager);
			addlib_monadicApplyAdapterNextFunction = 0;
			addlib_monadicApplyAdapterFunctionByOpID.clear();
		}

		/* Associate this operation identifier with the next unassigned C function */
		maop = addlib_monadicApplyAdapterFunction(addlib_monadicApplyAdapterNextFunction++);
		addlib_monadicApplyAdapterFunctionByOpID[opID] = maop;
	}

	/* Lookup in cache if not yet known */
	if (maop == NULL)
		maop = addlib_monadicApplyAdapterFunctionByOpID[opID];

	/* Invoke Cudd_addMonadicApply with the associated C function */
	addlib_monadicApplyAdapterEnv = env;
	addlib_monadicApplyAdapterOpObj = op;
	addlib_monadicApplyAdapterOpMID = env->GetMethodID(opClass, "applyAndPostponeRtException", "(JJ)J");
	return (jlong) Cudd_addMonadicApply(
		(DdManager *) ddManager, 
		maop, 
		(DdNode *) f);
}

/* Clears the operations cache. Might change the order due to Cudd_ReduceHeap. */
void addlib_clearCache(DdManager *ddManager) {
	Cudd_ReorderingType reorderingType;
	Cudd_ReorderingStatus(ddManager, &reorderingType);
	if (reorderingType == CUDD_REORDER_NONE)
		reorderingType = CUDD_REORDER_RANDOM;
	Cudd_ReduceHeap(ddManager, reorderingType, 0);
}
