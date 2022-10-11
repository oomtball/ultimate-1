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
package info.scce.addlib.sylvan;

import info.scce.addlib.apply.DD_AOP_Fn;
import info.scce.addlib.apply.DD_MAOP_Fn;
import info.scce.addlib.nativelib.NativeDependencyError;
import info.scce.addlib.nativelib.NativeLibraryLoader;

@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.AvoidSynchronizedAtMethodLevel"})
public final class Sylvan {

    public static final boolean AVAILABLE;

    static {
        boolean available = true;

        try {
            NativeLibraryLoader.loadNativeLibrary(Sylvan.class, "libaddlib-sylvan");
        } catch (NativeDependencyError e) {
            e.printStackTrace();
            available = false;
        }

        AVAILABLE = available;
    }

    private Sylvan() {
    }

    public static String unsupportedMethod(String methodName) {
        return methodName + "() is not supported by the SylvanBackend";
    }

    public static synchronized void lace_start(int numWorkers, long dqSize) {
        native_lace_start(numWorkers, dqSize);
    }

    private static native void native_lace_start(int numWorkers, long dqSize);

    public static synchronized void lace_stop() {
        native_lace_stop();
    }

    private static native void native_lace_stop();

    public static synchronized void lace_resume() {
        native_lace_resume();
    }

    private static native void native_lace_resume();

    public static synchronized void lace_suspend() {
        native_lace_suspend();
    }

    private static native void native_lace_suspend();

    public static synchronized void lace_set_verbosity(int level) {
        native_lace_set_verbosity(level);
    }

    private static native void native_lace_set_verbosity(int level);

    public static synchronized void sylvan_set_limits(long memoryCap, int table_ratio, int initial_ratio) {
        native_sylvan_set_limits(memoryCap, table_ratio, initial_ratio);
    }

    public static native void native_sylvan_set_limits(long memoryCap, int table_ratio, int initial_ratio);

    public static synchronized void sylvan_init_package() {
        native_sylvan_init_package();
    }

    private static native void native_sylvan_init_package();

    public static synchronized void sylvan_init_mtbdd() {
        native_sylvan_init_mtbdd();
    }

    private static native void native_sylvan_init_mtbdd();

    public static synchronized long mtbdd_int64(long value) {
        return native_mtbdd_int64(value);
    }

    private static native long native_mtbdd_int64(long value);

    public static synchronized long mtbdd_double(double value) {
        return native_mtbdd_double(value);
    }

    private static native long native_mtbdd_double(double value);

    public static synchronized long mtbdd_fraction(long nom, long denom) {
        return native_mtbdd_fraction(nom, denom);
    }

    private static native long native_mtbdd_fraction(long nom, long denom);

    public static synchronized int mtbdd_isleaf(long bdd) {
        return native_mtbdd_isleaf(bdd);
    }

    private static native int native_mtbdd_isleaf(long bdd);

    public static synchronized int mtbdd_getvar(long node) {
        return native_mtbdd_getvar(node);
    }

    private static native int native_mtbdd_getvar(long node);

    public static synchronized long mtbdd_getlow(long mtbdd) {
        return native_mtbdd_getlow(mtbdd);
    }

    private static native long native_mtbdd_getlow(long mtbdd);

    public static synchronized long mtbdd_gethigh(long mtbdd) {
        return native_mtbdd_gethigh(mtbdd);
    }

    private static native long native_mtbdd_gethigh(long mtbdd);

    public static synchronized int mtbdd_gettype(long leaf) {
        return native_mtbdd_gettype(leaf);
    }

    private static native int native_mtbdd_gettype(long leaf);

    public static synchronized long mtbdd_getvalue(long leaf) {
        return native_mtbdd_getvalue(leaf);
    }

    private static native long native_mtbdd_getvalue(long leaf);

    public static synchronized long mtbdd_getint64(long leaf) {
        return native_mtbdd_getint64(leaf);
    }

    private static native long native_mtbdd_getint64(long leaf);

    public static synchronized double mtbdd_getdouble(long leaf) {
        return native_mtbdd_getdouble(leaf);
    }

    private static native double native_mtbdd_getdouble(long leaf);

    public static synchronized long mtbdd_makeleaf(int type, long value) {
        return native_mtbdd_makeleaf(type, value);
    }

    private static native long native_mtbdd_makeleaf(int type, long value);

    public static synchronized long mtbdd_makenode(int var, long low, long high) {
        return native_mtbdd_makenode(var, low, high);
    }

    private static native long native_mtbdd_makenode(int var, long low, long high);

    public static synchronized void mtbdd_protect(long a) {
        native_mtbdd_protect(a);
    }

    private static native void native_mtbdd_protect(long a);

    public static synchronized void mtbdd_unprotect(long a) {
        native_mtbdd_unprotect(a);
    }

    private static native void native_mtbdd_unprotect(long a);

    public static synchronized long mtbdd_ithvar(int var) {
        return native_mtbdd_ithvar(var);
    }

    private static native long native_mtbdd_ithvar(int var);

    public static synchronized long mtbdd_nodecount(long dd) {
        return native_mtbdd_nodecount(dd);
    }

    private static native long native_mtbdd_nodecount(long dd);

    public static synchronized void sylvan_quit() {
        native_sylvan_quit();
    }

    private static native void native_sylvan_quit();

    public static synchronized long mtbdd_false() {
        return native_mtbdd_false();
    }

    private static native long native_mtbdd_false();

    public static synchronized long mtbdd_true() {
        return native_mtbdd_true();
    }

    private static native long native_mtbdd_true();

    public static synchronized void sylvan_gc_enable() {
        native_sylvan_gc_enable();
    }

    private static native void native_sylvan_gc_enable();

    public static synchronized void sylvan_gc_disable() {
        native_sylvan_gc_disable();
    }

    private static native void native_sylvan_gc_disable();

    public static synchronized int sylvan_isconst(long bdd) {
        return native_sylvan_isconst(bdd);
    }

    private static native int native_sylvan_isconst(long bdd);

    public static synchronized int sylvan_isnode(long bdd) {
        return native_sylvan_isnode(bdd);
    }

    private static native int native_sylvan_isnode(long bdd);

    public static synchronized long sylvan_not(long bdd) {
        return native_sylvan_not(bdd);
    }

    private static native long native_sylvan_not(long bdd);

    public static synchronized long sylvan_ite(long a, long b, long c) {
        return native_sylvan_ite(a, b, c);
    }

    private static native long native_sylvan_ite(long a, long b, long c);

    public static synchronized long sylvan_and(long a, long b) {
        return native_sylvan_and(a, b);
    }

    private static native long native_sylvan_and(long a, long b);

    public static synchronized long sylvan_xor(long a, long b) {
        return native_sylvan_xor(a, b);
    }

    private static native long native_sylvan_xor(long a, long b);

    public static synchronized long sylvan_equiv(long a, long b) {
        return native_sylvan_equiv(a, b);
    }

    private static native long native_sylvan_equiv(long a, long b);

    public static synchronized long sylvan_or(long a, long b) {
        return native_sylvan_or(a, b);
    }

    private static native long native_sylvan_or(long a, long b);

    public static synchronized long sylvan_nand(long a, long b) {
        return native_sylvan_nand(a, b);
    }

    private static native long native_sylvan_nand(long a, long b);

    public static synchronized long sylvan_nor(long a, long b) {
        return native_sylvan_nor(a, b);
    }

    private static native long native_sylvan_nor(long a, long b);

    public static synchronized long sylvan_imp(long a, long b) {
        return native_sylvan_imp(a, b);
    }

    private static native long native_sylvan_imp(long a, long b);

    public static synchronized long sylvan_diff(long a, long b) {
        return native_sylvan_diff(a, b);
    }

    private static native long native_sylvan_diff(long a, long b);

    public static synchronized long sylvan_less(long a, long b) {
        return native_sylvan_less(a, b);
    }

    private static native long native_sylvan_less(long a, long b);

    public static synchronized long sylvan_ithvar(int var) {
        return native_sylvan_ithvar(var);
    }

    private static native long native_sylvan_ithvar(int var);

    public static synchronized long sylvan_nithvar(int var) {
        return native_sylvan_nithvar(var);
    }

    private static native long native_sylvan_nithvar(int var);

    public static synchronized long sylvan_compose(long f, long m, int var) {
        return native_sylvan_compose(f, m, var);
    }

    private static native long native_sylvan_compose(long f, long m, int var);

    public static synchronized long sylvan_vector_compose(long dd, long[] vector) {
        return native_sylvan_vector_compose(dd, vector);
    }

    private static native long native_sylvan_vector_compose(long dd, long[] vector);

    public static synchronized long mtbdd_plus(long a, long b) {
        return native_mtbdd_plus(a, b);
    }

    private static native long native_mtbdd_plus(long a, long b);

    public static long mtbdd_addApply(DD_AOP_Fn op, long f, long g) {
        long result = native_mtbdd_addApply(op, f, g);
        op.rethrowPostponedRtExceptionIfAny();
        return result;
    }

    private static native long native_mtbdd_addApply(DD_AOP_Fn op, long f, long g);

    public static long mtbdd_addMonadicApply(DD_MAOP_Fn op, long f) {
        long result = native_mtbdd_addMonadicApply(op, f);
        op.rethrowPostponedRtExceptionIfAny();
        return result;
    }

    private static native long native_mtbdd_addMonadicApply(DD_MAOP_Fn op, long f);

    public static synchronized long mtbdd_count_refs() {
        return native_mtbdd_count_refs();
    }

    private static native long native_mtbdd_count_refs();

    public static synchronized int mtbdd_leq(long f, long g) {
        return native_mtbdd_leq(f, g);
    }

    private static native int native_mtbdd_leq(long f, long g);

    public static synchronized long mtbdd_ite(long dd, long t, long e) {
        return native_mtbdd_ite(dd, t, e);
    }

    private static native long native_mtbdd_ite(long dd, long t, long e);

    public static synchronized long mtbdd_cmpl(long dd) {
        return native_mtbdd_cmpl(dd);
    }

    private static native long native_mtbdd_cmpl(long dd);

    public static synchronized long mtbdd_compose(long dd, long f, int var) {
        return native_mtbdd_compose(dd, f, var);
    }

    private static native long native_mtbdd_compose(long dd, long f, int var);

    public static synchronized long mtbdd_vector_compose(long dd, long[] vector) {
        return native_mtbdd_vector_compose(dd, vector);
    }

    private static native long native_mtbdd_vector_compose(long dd, long[] vector);

    public static synchronized long mtbdd_times(long f, long g) {
        return native_mtbdd_times(f, g);
    }

    private static native long native_mtbdd_times(long f, long g);

    public static synchronized long mtbdd_threshold_double_dleaf(long f, long g) {
        return native_mtbdd_threshold_double_dleaf(f, g);
    }

    private static native long native_mtbdd_threshold_double_dleaf(long f, long g);

    public static synchronized long mtbdd_minus(long f, long g) {
        return native_mtbdd_minus(f, g);
    }

    private static native long native_mtbdd_minus(long f, long g);

    public static synchronized long mtbdd_min(long f, long g) {
        return native_mtbdd_min(f, g);
    }

    private static native long native_mtbdd_min(long f, long g);

    public static synchronized long mtbdd_max(long f, long g) {
        return native_mtbdd_max(f, g);
    }

    private static native long native_mtbdd_max(long f, long g);

    public static synchronized long mtbdd_invalid() {
        return native_mtbdd_invalid();
    }

    private static native long native_mtbdd_invalid();

    public static synchronized long mtbdd_eval(long f, int[] inputs) {
        return native_mtbdd_eval(f, inputs);
    }

    private static native long native_mtbdd_eval(long f, int[] inputs);

    public static synchronized long mtbdd_divide(long a, long b) {
        return native_mtbdd_divide(a, b);
    }

    private static native long native_mtbdd_divide(long a, long b);

    public static synchronized long mtbdd_one_zero_maximum(long a, long b) {
        return native_mtbdd_one_zero_maximum(a, b);
    }

    private static native long native_mtbdd_one_zero_maximum(long a, long b);

    public static synchronized long mtbdd_or(long a, long b) {
        return native_mtbdd_or(a, b);
    }

    private static native long native_mtbdd_or(long a, long b);

    public static synchronized long mtbdd_nand(long a, long b) {
        return native_mtbdd_nand(a, b);
    }

    private static native long native_mtbdd_nand(long a, long b);

    public static synchronized long mtbdd_nor(long a, long b) {
        return native_mtbdd_nor(a, b);
    }

    private static native long native_mtbdd_nor(long a, long b);

    public static synchronized long mtbdd_xor(long a, long b) {
        return native_mtbdd_xor(a, b);
    }

    private static native long native_mtbdd_xor(long a, long b);

    public static synchronized long mtbdd_xnor(long a, long b) {
        return native_mtbdd_xnor(a, b);
    }

    private static native long native_mtbdd_xnor(long a, long b);

    public static synchronized long mtbdd_log(long a) {
        return native_mtbdd_log(a);
    }

    private static native long native_mtbdd_log(long a);
}
