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

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1lace_1start(JNIEnv *, jclass, jint numWorkers, jlong dqSize)
{
  lace_start(numWorkers, dqSize);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1lace_1stop
  (JNIEnv *, jclass)
{
  lace_stop();
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1lace_1resume
  (JNIEnv *, jclass)
{
  lace_resume();
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1lace_1suspend
  (JNIEnv *, jclass)
{
  lace_suspend();
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1lace_1set_1verbosity
  (JNIEnv *, jclass, jint level)
{
  lace_set_verbosity(level);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1set_1limits(JNIEnv *, jclass, jlong memoryCap, jint table_ratio, jint initial_ratio)
{
  sylvan_set_limits(memoryCap, table_ratio, initial_ratio);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1init_1package(JNIEnv *, jclass)
{
  sylvan_init_package();
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1init_1mtbdd(JNIEnv *, jclass)
{
  sylvan_init_mtbdd();
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1double(JNIEnv *, jclass, jdouble value)
{
  return (jlong)mtbdd_double(value);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1int64(JNIEnv *, jclass, jlong value)
{
  return (jlong)mtbdd_int64(value);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1fraction(JNIEnv *, jclass, jlong nom, jlong denom)
{
  return (jlong)mtbdd_fraction(nom, denom);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1isleaf(JNIEnv *, jclass, jlong bdd)
{
  return (jint)mtbdd_isleaf((MTBDD)bdd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1getvar(JNIEnv *, jclass, jlong node)
{
  return (jint)mtbdd_getvar((MTBDD)node);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1getlow(JNIEnv *, jclass, jlong mtbdd)
{
  return (jlong)mtbdd_getlow((MTBDD)mtbdd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1gethigh(JNIEnv *, jclass, jlong mtbdd)
{
  return (jlong)mtbdd_gethigh((MTBDD)mtbdd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1gettype(JNIEnv *, jclass, jlong leaf)
{
  return (jint)mtbdd_gettype((MTBDD)leaf);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1getvalue(JNIEnv *, jclass, jlong leaf)
{
  return (jlong)mtbdd_getvalue((MTBDD)leaf);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1getint64(JNIEnv *, jclass, jlong leaf)
{
  return (jlong)mtbdd_getint64((MTBDD)leaf);
}

JNIEXPORT jdouble JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1getdouble(JNIEnv *, jclass, jlong leaf)
{
  return (jdouble)mtbdd_getdouble((MTBDD)leaf);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1makeleaf(JNIEnv *, jclass, jint type, jlong value)
{
  return (jlong)mtbdd_makeleaf((int)type, (long)value);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1makenode(JNIEnv *, jclass, jint var, jlong low, jlong high)
{
  return (jlong)mtbdd_makenode((int)var, (MTBDD)low, (MTBDD)high);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1protect(JNIEnv *, jclass, jlong a)
{
  sylvan_ref((MTBDD)a);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1unprotect(JNIEnv *, jclass, jlong a)
{
  sylvan_deref((MTBDD)a);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1ithvar(JNIEnv *, jclass, jint var)
{
  return (jlong)mtbdd_ithvar((int)var);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1nodecount(JNIEnv *, jclass, jlong dd)
{
  return (jlong)mtbdd_nodecount((MTBDD)dd);
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1quit(JNIEnv *, jclass)
{
  sylvan_quit();
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1false(JNIEnv *, jclass)
{
  return (jlong)mtbdd_false;
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1true(JNIEnv *, jclass)
{
  return (jlong)mtbdd_true;
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1gc_1enable(JNIEnv *, jclass)
{
  sylvan_gc_enable();
}

JNIEXPORT void JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1gc_1disable(JNIEnv *, jclass)
{
  sylvan_gc_disable();
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1isconst(JNIEnv *, jclass, jlong bdd)
{
  return (jint)sylvan_isconst((MTBDD)bdd);
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1isnode(JNIEnv *, jclass, jlong bdd)
{
  return (jint)sylvan_isnode((MTBDD)bdd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1not(JNIEnv *, jclass, jlong bdd)
{
  return (jlong)sylvan_not((MTBDD)bdd);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1ite(JNIEnv *, jclass, jlong a, jlong b, jlong c)
{
  return (jlong)sylvan_ite(a, b, c);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1and(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_and((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1xor(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_xor((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1equiv(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_equiv((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1or(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_or((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1nand(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_nand((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1nor(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_nor((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1imp(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_imp((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1diff(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_diff((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1less(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)sylvan_less((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1ithvar(JNIEnv *, jclass, jint var)
{
  return (jlong)sylvan_ithvar((int)var);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1nithvar(JNIEnv *, jclass, jint var)
{
  return (jlong)sylvan_nithvar((int)var);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1sylvan_1compose(JNIEnv *, jclass, jlong f, jlong m)
{
  return (jlong)sylvan_compose((MTBDD)f, (MTBDD)m);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1plus(JNIEnv *, jclass, jlong a, jlong b)
{
  return (jlong)mtbdd_plus((MTBDD)a, (MTBDD)b);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1count_1refs
  (JNIEnv *, jclass)
{
  return (jlong)mtbdd_count_protected();
}

JNIEXPORT jint JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1leq
  (JNIEnv *, jclass, jlong f, jlong g)
{
  return (jint)(mtbdd_leq((MTBDD)f, (MTBDD)g) == mtbdd_true);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1ite
  (JNIEnv *, jclass, jlong dd, jlong t, jlong e)
{
  MTBDD gBool = (MTBDD) dd;
  if (mtbdd_gethigh(gBool) != mtbdd_true && mtbdd_getlow(gBool) != mtbdd_false) {
    gBool = mtbdd_threshold_double((MTBDD) dd, 1.0);
  }
  return (jlong)mtbdd_ite(gBool, t, e);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1cmpl
  (JNIEnv *, jclass, jlong f)
{
  return (jlong)mtbdd_cmpl(f);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1times
  (JNIEnv *, jclass, jlong f, jlong g)
{
  return (jlong)mtbdd_times((MTBDD)f, (MTBDD)g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1threshold_1double
  (JNIEnv *, jclass, jlong f, jdouble threshold)
{
  return (jlong)mtbdd_threshold_double((MTBDD)f, (double)threshold);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1minus
  (JNIEnv *, jclass, jlong f, jlong g)
{
  return (jlong)mtbdd_minus((MTBDD)f, (MTBDD)g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1min
  (JNIEnv *, jclass, jlong f, jlong g)
{
  return (jlong)mtbdd_min((MTBDD)f, (MTBDD)g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1max
  (JNIEnv *, jclass, jlong f, jlong g)
{
  return (jlong)mtbdd_max((MTBDD)f, (MTBDD)g);
}

JNIEXPORT jlong JNICALL Java_info_scce_addlib_sylvan_Sylvan_native_1mtbdd_1invalid
  (JNIEnv *, jclass)
{
    return (jlong) mtbdd_invalid;
}
