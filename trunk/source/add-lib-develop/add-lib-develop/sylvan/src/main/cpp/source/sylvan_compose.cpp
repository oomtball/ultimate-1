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

/* Sylvan uses BDDMAP and MTBDDMAP for all functions related to compositions */
JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1compose
  (JNIEnv *, jclass, jlong f, jlong g, jint var)
{
  MTBDDMAP map = mtbdd_map_empty();

  /* Sylvan requires the ADD to be a boolean MTBDD before using mtbdd_compose */
  MTBDD gBool = mtbdd_threshold_double((MTBDD) g, 1.0);

  map = mtbdd_map_add(map, var, gBool);
  MTBDD result = mtbdd_compose((MTBDD) f, map);

  return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1compose
  (JNIEnv *, jclass, jlong f, jlong g, jint var)
{
  BDDMAP map = sylvan_map_empty();

  map = sylvan_map_add(map, var, (BDD) g);
  BDD result = sylvan_compose((BDD) f, map);

  return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1vector_1compose
  (JNIEnv *env, jclass, jlong f, jlongArray vector)
{
  MTBDDMAP map = mtbdd_map_empty();

  jlong *vectorElements = env->GetLongArrayElements(vector, 0);
  jsize vectorLength = env->GetArrayLength(vector);
  jlong *src = vectorElements;
  jlong *end = vectorElements + vectorLength;
  jint var = 0;

  /* Sylvan requires the ADDs to be boolean MTBDDs before using mtbdd_compose */
  while (src < end) {
    MTBDD gBool = mtbdd_threshold_double((MTBDD) *(src++), 1.0);
    /* Mapping variable -> ADD */
    map = mtbdd_map_add(map, var++, gBool);
  }

  MTBDD result = mtbdd_compose((MTBDD) f, map);
  env->ReleaseLongArrayElements(vector, vectorElements, 0);

  return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1vector_1compose
  (JNIEnv *env, jclass, jlong f, jlongArray vector)
{
  BDDMAP map = sylvan_map_empty();

  jlong *vectorElements = env->GetLongArrayElements(vector, 0);
  jsize vectorLength = env->GetArrayLength(vector);
  jlong *src = vectorElements;
  jlong *end = vectorElements + vectorLength;
  jint var = 0;

  while (src < end)
    /* Mapping variable -> BDD */
    map = sylvan_map_add(map, var++, (BDD) *(src++));

  BDD result = sylvan_compose((BDD) f, map);
  env->ReleaseLongArrayElements(vector, vectorElements, 0);

  return (jlong) result;
}