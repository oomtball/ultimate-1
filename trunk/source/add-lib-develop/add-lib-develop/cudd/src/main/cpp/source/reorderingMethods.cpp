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

/* 
 * Ordinal values derived from Java enum 'Cudd_ReorderingType':
 * CUDD_REORDER_SAME            0
 * CUDD_REORDER_NONE            1
 * CUDD_REORDER_RANDOM          2
 * CUDD_REORDER_RANDOM_PIVOT    3
 * CUDD_REORDER_SIFT            4
 * CUDD_REORDER_SIFT_CONVERGE   5
 * CUDD_REORDER_SYMM_SIFT       6
 * CUDD_REORDER_SYMM_SIFT_CONV  7
 * CUDD_REORDER_WINDOW2         8
 * CUDD_REORDER_WINDOW3         9
 * CUDD_REORDER_WINDOW4         10
 * CUDD_REORDER_WINDOW2_CONV    11
 * CUDD_REORDER_WINDOW3_CONV    12
 * CUDD_REORDER_WINDOW4_CONV    13
 * CUDD_REORDER_GROUP_SIFT      14
 * CUDD_REORDER_GROUP_SIFT_CONV 15
 * CUDD_REORDER_ANNEALING       16
 * CUDD_REORDER_GENETIC         17
 * CUDD_REORDER_LINEAR          18
 * CUDD_REORDER_LINEAR_CONVERGE 19
 * CUDD_REORDER_LAZY_SIFT       20
 * CUDD_REORDER_EXACT           21
 */

Cudd_ReorderingType getReorderType(int type){
	Cudd_ReorderingType reorderingType = CUDD_REORDER_NONE;
	switch (type) {
		case 0: reorderingType = CUDD_REORDER_SAME; break;
		case 1: reorderingType = CUDD_REORDER_NONE; break;
		case 2: reorderingType = CUDD_REORDER_RANDOM; break;
		case 3: reorderingType = CUDD_REORDER_RANDOM_PIVOT; break;
		case 4: reorderingType = CUDD_REORDER_SIFT; break;
		case 5: reorderingType = CUDD_REORDER_SIFT_CONVERGE; break;
		case 6: reorderingType = CUDD_REORDER_SYMM_SIFT; break;
		case 7: reorderingType = CUDD_REORDER_SYMM_SIFT_CONV; break;
		case 8: reorderingType = CUDD_REORDER_WINDOW2; break;
		case 9: reorderingType = CUDD_REORDER_WINDOW3; break;
		case 10: reorderingType = CUDD_REORDER_WINDOW4; break;
		case 11: reorderingType = CUDD_REORDER_WINDOW2_CONV; break;
		case 12: reorderingType = CUDD_REORDER_WINDOW3_CONV; break;
		case 13: reorderingType = CUDD_REORDER_WINDOW4_CONV; break;
		case 14: reorderingType = CUDD_REORDER_GROUP_SIFT; break;
		case 15: reorderingType = CUDD_REORDER_GROUP_SIFT_CONV; break;
		case 16: reorderingType = CUDD_REORDER_ANNEALING; break;
		case 17: reorderingType = CUDD_REORDER_GENETIC; break;
		case 18: reorderingType = CUDD_REORDER_LINEAR; break;
		case 19: reorderingType = CUDD_REORDER_LINEAR_CONVERGE; break;
		case 20: reorderingType = CUDD_REORDER_LAZY_SIFT; break;
		case 21: reorderingType = CUDD_REORDER_EXACT; break;
	}
	return reorderingType;
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReduceHeap
	(JNIEnv *, jclass, jlong ddManager, jint heuristic, jint minsize) {

	Cudd_ReorderingType reorderingType = getReorderType(heuristic);
	return (jint) Cudd_ReduceHeap((DdManager *) ddManager, reorderingType, minsize);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1AutodynEnable
	(JNIEnv *, jclass, jlong ddManager, jint method) {

	Cudd_ReorderingType reorderingType = getReorderType(method);
	Cudd_AutodynEnable((DdManager *) ddManager, reorderingType);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1AutodynEnableZdd
	(JNIEnv *, jclass, jlong ddManager, jint method) {

	Cudd_ReorderingType reorderingType = getReorderType(method);
	Cudd_AutodynEnableZdd((DdManager *) ddManager, reorderingType);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1zddReduceHeap
	(JNIEnv *, jclass, jlong ddManager, jint heuristic, jint minsize) {

	Cudd_ReorderingType reorderingType = getReorderType(heuristic);
	return (jint) Cudd_zddReduceHeap((DdManager *) ddManager, reorderingType, minsize);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1SetNextReordering
	(JNIEnv *, jclass, jlong dd, jint next) {

	Cudd_SetNextReordering((DdManager *) dd, next);
}

JNIEXPORT int JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReadNextReordering
	(JNIEnv *, jclass, jlong dd) {

	return (jint) Cudd_ReadNextReordering((DdManager *) dd);
}

JNIEXPORT int JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1ReorderingStatus
	(JNIEnv *, jclass, jlong dd, jint method) {

    Cudd_ReorderingType reorderingType = getReorderType(method);
	return (jint) Cudd_ReorderingStatus((DdManager *) dd, (Cudd_ReorderingType *) reorderingType);
}