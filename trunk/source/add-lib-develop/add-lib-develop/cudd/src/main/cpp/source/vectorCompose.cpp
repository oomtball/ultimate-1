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

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1bddVectorCompose
	(JNIEnv *env, jclass, jlong ddManager, jlong f, jlongArray vector) {

    /* Convert JNI jlongArray to a DdNode array */
	jlong *vectorElements = env->GetLongArrayElements(vector, 0);
	jsize vectorLength = env->GetArrayLength(vector);
	DdNode **ddVector = new DdNode *[vectorLength];
	jlong *src = vectorElements;
	jlong *end = vectorElements + vectorLength;
	DdNode **dest = ddVector;
	while (src < end)
		*(dest++) = (DdNode *) *(src++);

	/* Compose BDD with DdNode array */
	DdNode *result = Cudd_bddVectorCompose((DdManager *) ddManager, (DdNode *) f, ddVector);

	delete [] ddVector;
	env->ReleaseLongArrayElements(vector, vectorElements, 0);

	return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_cudd_Cudd_native_1Cudd_1addVectorCompose
	(JNIEnv *env, jclass, jlong ddManager, jlong f, jlongArray vector) {

	/* Convert JNI jlongArray to a DdNode array */
	jlong *vectorElements = env->GetLongArrayElements(vector, 0);
	jsize vectorLength = env->GetArrayLength(vector);
	DdNode **ddVector = new DdNode *[vectorLength];
	jlong *src = vectorElements;
	jlong *end = vectorElements + vectorLength;
	DdNode **dest = ddVector;
	while (src < end)
		*(dest++) = (DdNode *) *(src++);

	/* Compose ADD with DdNode array */
	DdNode *result = Cudd_addVectorCompose((DdManager *) ddManager, (DdNode *) f, ddVector);

	delete [] ddVector;
	env->ReleaseLongArrayElements(vector, vectorElements, 0);

	return (jlong) result;
}
