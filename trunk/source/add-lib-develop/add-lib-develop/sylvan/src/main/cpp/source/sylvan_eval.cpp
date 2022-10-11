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
#include "sylvan.h"

using namespace sylvan;

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1eval
  (JNIEnv *env, jclass, jlong f, jintArray inputs)
{
    MTBDD dd = (MTBDD) f;

    /* Convert JNI jintArray to a C++ int array */
    jint* inputElements = env->GetIntArrayElements(inputs, 0);
    jsize inputsLength = env->GetArrayLength(inputs);
    int* inputsArr = new int[inputsLength];
    jint *src = inputElements;
    jint *end = inputElements + inputsLength;
    int* dest = inputsArr;
    while (src < end) {
        *(dest++) = (int) *(src++);
    }

    /* Traverse through the DD with the given input */
    while(!mtbdd_isleaf(dd)) {
        uint32_t var = mtbdd_getvar(dd);
        if(var >= inputsLength) {
            return mtbdd_invalid;
        } else if(inputsArr[var]) {
            dd = mtbdd_gethigh(dd);
        } else {
            dd = mtbdd_getlow(dd);
        }
    }

    delete [] inputsArr;

    return (jlong) dd;
}

