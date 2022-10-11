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
#include "info_scce_addlib_sylvan_Sylvan.h"
#include "sylvan_apply.h"
#include "sylvan_threshold.h"

/*
 * Class:     info_scce_addlib_cudd_Sylvan
 * Method:    native_mtbdd_addApply
 * Signature: (JLinfo/scce/addlib/cudd/DD_AOP_Fn;JJ)J
 *
 * Sylvan caches mtbdd_apply results internally and identifies operations by their function pointer.
 * For this reason every Java callback must be wrapped by a separate C function. We use a pool of N
 * functions and clear Sylvan's cache only once all of them are used.
 */
JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1addApply
  (JNIEnv *env, jclass, jobject op, jlong f, jlong g) {

    /* Get operation identifier */
	jclass opClass = env->GetObjectClass(op);
	jmethodID opMID = env->GetMethodID(opClass, "id", "()J");
	jlong opID = env->CallLongMethod(op, opMID);
	
	/* Ensure to associate a C function with this operation identifier */
	mtbdd_apply_op aop = NULL;
	if (addlib_applyAdapterFunctionByOpIDSylvan.find(opID) == addlib_applyAdapterFunctionByOpIDSylvan.end()) {

		/* Clear cache if neccessary */
		if (addlib_applyAdapterNextFunctionSylvan >= ADDLIB_APPLY_ADAPTER_FUNCTION_N) {
			addlib_clearCache();
			addlib_applyAdapterNextFunctionSylvan = 0;
			addlib_applyAdapterFunctionByOpIDSylvan.clear();
		}

		/* Associate this operation identifier with the next unassigned C function */
		aop = addlib_applyAdapterFunctionSylvan(addlib_applyAdapterNextFunctionSylvan++);
		addlib_applyAdapterFunctionByOpIDSylvan[opID] = aop;
	} 

	/* Lookup in cache if not yet known */
	if (aop == NULL)
		aop = addlib_applyAdapterFunctionByOpIDSylvan[opID];

	/* Invoke Cudd_addApply with the associated C function */
	addlib_applyAdapterEnvSylvan= env;
	addlib_applyAdapterOpObjSylvan = op;
	addlib_applyAdapterOpMIDSylvan = env->GetMethodID(opClass, "applyAndPostponeRtException", "(JJJ)J");
	env->GetJavaVM(&addlib_applyAdapterJvmSylvan);
    return (jlong) mtbdd_apply((MTBDD)f, (MTBDD)g, aop);
  }

/*
 * Class:     info_scce_addlib_cudd_Sylvan
 * Method:    native_mtbdd_addMonadicApply
 * Signature: (JLinfo/scce/addlib/cudd/DD_MAOP_Fn;J)J
 *
 * Sylvan caches mtbdd_uapply results internally and identifies operations by their function pointer.
 * For this reason every Java callback must be wrapped by a separate C function. We use a pool of N
 * functions and clear Sylvan's cache only once all of them are used.
 */
JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1addMonadicApply
  (JNIEnv *env, jclass, jobject op, jlong f) {

	/* Get operation identifier */
	jclass opClass = env->GetObjectClass(op);
	jmethodID opMID = env->GetMethodID(opClass, "id", "()J");
	jlong opID = env->CallLongMethod(op, opMID);

	/* Ensure to associate a C function with this operation identifier */
	mtbdd_uapply_op maop = NULL;
	if (addlib_monadicApplyAdapterFunctionByOpIDSylvan.find(opID) == addlib_monadicApplyAdapterFunctionByOpIDSylvan.end()) {

		/* Clear cache if neccessary */
		if (addlib_monadicApplyAdapterNextFunctionSylvan >= ADDLIB_MONADIC_APPLY_ADAPTER_FUNCTION_N) {
			addlib_clearCache();
			addlib_monadicApplyAdapterNextFunctionSylvan = 0;
			addlib_monadicApplyAdapterFunctionByOpIDSylvan.clear();
		}

		/* Associate this operation identifier with the next unassigned C function */
		maop = addlib_monadicApplyAdapterFunctionSylvan(addlib_monadicApplyAdapterNextFunctionSylvan++);
		addlib_monadicApplyAdapterFunctionByOpIDSylvan[opID] = maop;
	}

	/* Lookup in cache if not yet known */
	if (maop == NULL)
		maop = addlib_monadicApplyAdapterFunctionByOpIDSylvan[opID];

	/* Invoke Cudd_addMonadicApply with the associated C function */
	addlib_monadicApplyAdapterEnvSylvan = env;
	addlib_monadicApplyAdapterOpObjSylvan = op;
	addlib_monadicApplyAdapterOpMIDSylvan = env->GetMethodID(opClass, "applyAndPostponeRtException", "(JJ)J");
	env->GetJavaVM(&addlib_monadicApplyAdapterJvmSylvan);
	return (jlong) mtbdd_uapply((MTBDD)f, maop, 0);
  }

/* Threshold function for ADDs with doubles in leaves */
JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1threshold_1double_1dleaf
  (JNIEnv *env, jclass, jlong f, jlong g) {
    MTBDD threshold_dd = (MTBDD) g;
    if (mtbdd_isleaf(threshold_dd)) {
        double threshold = mtbdd_getdouble(threshold_dd);
        return (jlong) mtbdd_threshold_double_dleaf_helper((MTBDD) f, threshold);
    }
    return mtbdd_invalid;
}

/* Helper functions for apply methods */

/* Apply a binary function on the leaves of the two MTBDD */
MTBDD binaryApplyHelper(DoubleBinaryOperator op, MTBDD* f, MTBDD* g) {
    MTBDD a = *f, b = *g;
    if (mtbdd_isleaf(a) > 0 && mtbdd_isleaf(b) > 0) {
        double fVal = mtbdd_getdouble(a);
        double gVal = mtbdd_getdouble(b);
        double result = op(fVal, gVal);
        return mtbdd_double(result);
    }
    return mtbdd_invalid;
}

/* Apply a monadic function on the leaves of MTBDD */
MTBDD monadicApplyHelper(DoubleUnaryOperator op, MTBDD f) {
    if (mtbdd_isleaf(f) > 0) {
        double fVal = mtbdd_getdouble(f);
        double result = op(fVal);
        long ptr = mtbdd_double(result);
        return ptr;
    }
    return mtbdd_invalid;
}

/* Apply functions */

/* Divide value of leaves from first ADD through value of leaves from second ADD */
TASK_2(MTBDD, mtbdd_divide, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto divideFunc = [](double a, double b) {
        return a / b;
    };
    return binaryApplyHelper(divideFunc, fPtr, gPtr);
}

/* 1 if value from first ADD is larger than second one, otherwise 0 */
TASK_2(MTBDD, mtbdd_one_zero_maximum, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto oneZeroMaximumFunc = [](double a, double b) {
        return a > b ? 1.0 : 0.0;
    };
    return binaryApplyHelper(oneZeroMaximumFunc, fPtr, gPtr);
}

/* Binary or for ADDs */
TASK_2(MTBDD, mtbdd_or, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto orFunc = [](double a, double b) {
        return (a != 0.0 || b != 0.0) ? 1.0 : 0.0;
    };
    return binaryApplyHelper(orFunc, fPtr, gPtr);
}

/* Binary nand for ADDs */
TASK_2(MTBDD, mtbdd_nand, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto nandFunc = [](double a, double b) {
        return (a != 0.0 && b != 0.0) ? 0.0 : 1.0;
    };
    return binaryApplyHelper(nandFunc, fPtr, gPtr);
}

/* Binary nor for ADDs */
TASK_2(MTBDD, mtbdd_nor, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto norFunc = [](double a, double b) {
        return (a != 0.0 || b != 0.0) ? 0.0 : 1.0;
    };
    return binaryApplyHelper(norFunc, fPtr, gPtr);
}

/* Binary xor for ADDs */
TASK_2(MTBDD, mtbdd_xor, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto xorFunc = [](double a, double b) {
        return (a != 0.0 ^ b != 0.0) ? 1.0 : 0.0;
    };
    return binaryApplyHelper(xorFunc, fPtr, gPtr);
}

/* Binary xnor for ADDs */
TASK_2(MTBDD, mtbdd_xnor, MTBDD*, fPtr, MTBDD*, gPtr) {
    auto xnorFunc = [](double a, double b) {
        return (a != 0.0 ^ b != 0.0) ? 0.0 : 1.0;
    };
    return binaryApplyHelper(xnorFunc, fPtr, gPtr);
}


JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1divide
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_divide));
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1one_1zero_1maximum
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_one_zero_maximum));
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1or
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_or));
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1nand
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_nand));
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1nor
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_nor));
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1xor
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_xor));
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1xnor
  (JNIEnv *env, jclass, jlong f, jlong g) {
    return (jlong) mtbdd_apply((MTBDD) f, (MTBDD) g, TASK(mtbdd_xnor));
}


/* monadicApply functions */

TASK_2(MTBDD, mtbdd_log, MTBDD, f, size_t, svalue) {
    auto logFunc = [](double a) {
        return log(a);
    };
    return monadicApplyHelper(log, f);
}


JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1log
  (JNIEnv *env, jclass, jlong f) {
    return (jlong) mtbdd_uapply((MTBDD) f, TASK(mtbdd_log), 0);
}

/* Clears the operations cache. Does not change the order compared to Cudd. */
void addlib_clearCache() {
    sylvan_clear_cache();
}