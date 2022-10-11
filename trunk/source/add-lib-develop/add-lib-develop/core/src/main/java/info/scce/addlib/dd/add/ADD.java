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
package info.scce.addlib.dd.add;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import info.scce.addlib.apply.DD_AOP_Fn;
import info.scce.addlib.apply.DD_MAOP_Fn;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.ringlikedd.example.ArithmeticDDManager;
import info.scce.addlib.util.Conversions;
import info.scce.addlib.utils.DDConversions;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class ADD extends RegularDD<ADDManager, ADD> {

    public ADD(long ptr, ADDManager ddManager) {
        super(ptr, ddManager);
    }

    /* CUDD wrapper */

    public double v() {
        assertConstant();
        return ddManager.getBackend().v(ptr);
    }

    public ADD ite(ADD t, ADD e) {
        assertEqualDDManager(t, e);
        long resultPtr = ddManager.getBackend().ite(ddManager().ptr(), ptr, t.ptr, e.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD iteConstant(ADD t, ADD e) {
        assertEqualDDManager(t, e);
        long resultPtr = ddManager.getBackend().iteConstant(ddManager().ptr(), ptr, t.ptr, e.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD evalConst(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().evalConst(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD cmpl() {
        long resultPtr = ddManager.getBackend().cmpl(ddManager().ptr(), ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public boolean leq(ADD g) {
        assertEqualDDManager(g);
        int result = ddManager.getBackend().leq(ddManager().ptr(), ptr, g.ptr);
        return result > 0;
    }

    public ADD compose(ADD g, int v) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().compose(ddManager.ptr(), ptr, g.ptr, v);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD vectorCompose(ADD... vector) {
        assertEqualDDManager(vector);
        long resultPtr = ddManager.getBackend().vectorComposeADD(ddManager().ptr(), ptr, DDConversions.ptrs(vector));
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD plus(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().plus(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD times(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().times(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD threshold(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().threshold(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD setNZ(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().setNZ(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD divide(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().divide(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD minus(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().minus(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD minimum(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().minimum(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD maximum(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().maximum(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD oneZeroMaximum(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().oneZeroMaximum(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD diff(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().diff(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD agreement(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().agreement(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD or(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().or(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD nand(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().nand(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD nor(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().nor(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD xor(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().xor(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD xnor(ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().xnor(ddManager().ptr(), ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD log() {
        long resultPtr = ddManager.getBackend().log(ddManager().ptr(), ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    /* Non-trivial operations */

    public ADD apply(final BinaryOperator<Double> op, ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().apply(ddManager().ptr(), new DD_AOP_Fn() {

            @Override
            public long apply(long ddManagerPtr, long f, long g) {
                if (Conversions.asBoolean(ddManager.getBackend().isConstant(f)) &&
                    Conversions.asBoolean(ddManager.getBackend().isConstant(g))) {
                    double left = ddManager.getBackend().v(f);
                    double right = ddManager.getBackend().v(g);
                    try {
                        double result = op.apply(left, right);
                        return ddManager.getBackend().constant(ddManagerPtr, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ddManager.getBackend().invalid();
                    }
                }
                return ddManager.getBackend().invalid();
            }
        }, this.ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD apply2(final BinaryOperator<ADD> op, final ADD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager().getBackend().apply(ddManager().ptr(), new DD_AOP_Fn() {

            @Override
            public long apply(long ddManager, long fPtr, long gPtr) {
                ADD f = new ADD(fPtr, ADD.this.ddManager);
                ADD g = new ADD(gPtr, ADD.this.ddManager);
                try {
                    ADD result = op.apply(f, g);
                    return result == null ? ddManager().getBackend().invalid() : result.ptr;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ddManager().getBackend().invalid();
                }
            }
        }, ptr, g.ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD monadicApply(final UnaryOperator<Double> op) {
        long resultPtr = ddManager().getBackend().monadicApply(ddManager().ptr(), new DD_MAOP_Fn() {

            @Override
            public long apply(long ddManagerPtr, long f) {
                if (Conversions.asBoolean(ddManager.getBackend().isConstant(f))) {
                    double x = ddManager().getBackend().v(f);
                    try {
                        double result = op.apply(x);
                        return ddManager().getBackend().constant(ddManagerPtr, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ddManager().getBackend().invalid();
                    }
                }
                return ddManager().getBackend().invalid();
            }
        }, ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    public ADD monadicApply2(final UnaryOperator<ADD> op) {
        long resultPtr = ddManager().getBackend().monadicApply(ddManager().ptr(), new DD_MAOP_Fn() {

            @Override
            public long apply(long ddManager, long fPtr) {
                ADD f = new ADD(fPtr, ADD.this.ddManager);
                try {
                    ADD result = op.apply(f);
                    return result == null ? ddManager().getBackend().invalid() : result.ptr;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ddManager().getBackend().invalid();
                }
            }
        }, ptr);
        return new ADD(resultPtr, ddManager).withRef();
    }

    /* Conversion to XDD */

    public XDD<Double> toXDD(ArithmeticDDManager ddManagerTarget) {
        HashMap<ADD, XDD<Double>> cache = new HashMap<>();
        XDD<Double> result = this.toXDDRecursive(ddManagerTarget, cache);

        /* Dereference intermediate results */
        cache.remove(this);
        for (XDD<Double> g : cache.values()) {
            g.recursiveDeref();
        }

        return result;
    }

    private XDD<Double> toXDDRecursive(ArithmeticDDManager ddManagerTarget, Map<ADD, XDD<Double>> cache) {
        XDD<Double> result = cache.get(this);
        if (result == null) {
            if (isConstant()) {
                result = ddManagerTarget.constant(v());
            } else {
                String name = readName();
                int idx = ddManagerTarget.varIdx(name);
                XDD<Double> t = t().toXDDRecursive(ddManagerTarget, cache);
                XDD<Double> e = e().toXDDRecursive(ddManagerTarget, cache);
                result = ddManagerTarget.ithVar(idx, t, e);
            }
            cache.put(this, result);
        }
        return result;
    }

    /* Required DD methods */

    @Override
    protected ADD thisCasted() {
        return this;
    }

    @Override
    public ADD t() {
        assertNonConstant();
        long resultPtr = ddManager.getBackend().t(ptr);
        return new ADD(resultPtr, ddManager);
    }

    @Override
    public ADD e() {
        assertNonConstant();
        long resultPtr = ddManager.getBackend().e(ptr);
        return new ADD(resultPtr, ddManager);
    }

    @Override
    public ADD eval(boolean... input) {
        long resultPtr = ddManager.getBackend().eval(ddManager().ptr(), ptr, Conversions.asInts(input));
        return new ADD(resultPtr, ddManager);
    }

    /* Other methods */

    @Override
    public String toString() {
        if (isConstant()) {
            return Double.toString(v());
        }
        return super.toString();
    }
}
