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
package info.scce.addlib.dd.xdd;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import info.scce.addlib.apply.DD_AOP_Fn;
import info.scce.addlib.apply.DD_MAOP_Fn;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.util.Conversions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.AvoidThrowingNullPointerException"})
public class XDD<E> extends RegularDD<XDDManager<E>, XDD<E>> {

    public XDD(long ptr, XDDManager<E> ddManager) {
        super(ptr, ddManager);
    }

    /* Value mapping */

    public @NonNull E v() {
        assertConstant();
        double valueId = ddManager.getBackend().v(ptr);
        return ddManager.v(valueId);
    }

    /* Operations */

    public XDD<E> apply(final BinaryOperator<E> op, XDD<E> g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().apply(ddManager.ptr(), new DD_AOP_Fn() {

            @Override
            public long apply(long ddManager, long f, long g) {
                if (Conversions.asBoolean(ddManager().getBackend().isConstant(f)) &&
                    Conversions.asBoolean(ddManager().getBackend().isConstant(g))) {
                    double leftElementIdentifier = ddManager().getBackend().v(f);
                    double rightElementIdentifier = ddManager().getBackend().v(g);
                    E left = XDD.this.ddManager.v(leftElementIdentifier);
                    E right = XDD.this.ddManager.v(rightElementIdentifier);
                    E result = op.apply(left, right);
                    double resultElementIdentifier = XDD.this.ddManager.valueId(result);
                    return ddManager().getBackend().constant(ddManager, resultElementIdentifier);
                }
                return ddManager().getBackend().invalid();
            }
        }, ptr, g.ptr);
        return new XDD<>(resultPtr, ddManager).withRef();
    }

    public XDD<E> apply2(final BinaryOperator<XDD<E>> op, XDD<E> g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager().getBackend().apply(ddManager.ptr(), new DD_AOP_Fn() {

            @Override
            public long apply(long ddManager, long fPtr, long gPtr) {
                XDD<E> f = new XDD<>(fPtr, XDD.this.ddManager);
                XDD<E> g = new XDD<>(gPtr, XDD.this.ddManager);
                XDD<E> result = op.apply(f, g);
                return result == null ? ddManager().getBackend().invalid() : result.ptr;
            }
        }, ptr, g.ptr);
        return new XDD<>(resultPtr, ddManager).withRef();
    }

    public XDD<E> monadicApply(final UnaryOperator<E> op) {
        long resultPtr = ddManager().getBackend().monadicApply(ddManager.ptr(), new DD_MAOP_Fn() {

            @Override
            public long apply(long ddManager, long f) {
                if (Conversions.asBoolean(ddManager().getBackend().isConstant(f))) {
                    double xElementIdentifier = ddManager().getBackend().v(f);
                    E x = XDD.this.ddManager.v(xElementIdentifier);
                    E result = op.apply(x);
                    double resultElementIdentifier = XDD.this.ddManager.valueId(result);
                    return ddManager().getBackend().constant(ddManager, resultElementIdentifier);
                }
                return ddManager().getBackend().invalid();
            }
        }, ptr);
        return new XDD<>(resultPtr, ddManager).withRef();
    }

    public XDD<E> monadicApply2(final UnaryOperator<XDD<E>> op) {
        long resultPtr = ddManager().getBackend().monadicApply(ddManager.ptr(), new DD_MAOP_Fn() {

            @Override
            public long apply(long ddManager, long fPtr) {
                XDD<E> f = new XDD<>(fPtr, XDD.this.ddManager);
                XDD<E> result = op.apply(f);
                return result == null ? ddManager().getBackend().invalid() : result.ptr;
            }
        }, ptr);
        return new XDD<>(resultPtr, ddManager).withRef();
    }

    public <E2, E3> XDD<E3> transform(XDDManager<E3> ddManagerTarget, BiFunction<E, E2, E3> op, XDD<E2> g) {
        throw new RuntimeException("Not implemented");
    }

    public <E2, E3> XDD<E3> transform2(XDDManager<E3> ddManagerTarget, BiFunction<XDD<E>, XDD<E2>, XDD<E3>> op,
                                       XDD<E2> g) {

        throw new RuntimeException("Not implemented");
    }

    public <E2> XDD<E2> monadicTransform(XDDManager<E2> ddManagerTarget, Function<E, E2> op) {
        HashMap<XDD<E>, XDD<E2>> cache = new HashMap<>();
        XDD<E2> result = this.monadicTransformRecursive(ddManagerTarget, op, cache);

        /* Dereference intermediate results */
        cache.remove(this);
        for (XDD<E2> g : cache.values()) {
            g.recursiveDeref();
        }

        return result;
    }

    private <E2> XDD<E2> monadicTransformRecursive(XDDManager<E2> ddManagerTarget, Function<E, E2> op,
                                                   Map<XDD<E>, XDD<E2>> cache) {

        XDD<E2> result = cache.get(this);
        if (result == null) {
            if (isConstant()) {
                E2 v2 = op.apply(v());
                result = ddManagerTarget.constant(v2);
            } else {
                String name = readName();
                int idx = ddManagerTarget.varIdx(name);
                XDD<E2> t = t().monadicTransformRecursive(ddManagerTarget, op, cache);
                XDD<E2> e = e().monadicTransformRecursive(ddManagerTarget, op, cache);
                result = ddManagerTarget.ithVar(idx, t, e);
            }
            cache.put(this, result);
        }
        return result;
    }

    public <E2> XDD<E2> monadicTransform2(XDDManager<E2> ddManagerTarget, Function<XDD<E>, XDD<E2>> op) {
        throw new RuntimeException("Not implemented");
    }

    /* Named operations defined in XDDManager */

    public XDD<E> inverse() {
        return monadicApply(ddManager::inverse);
    }

    public XDD<E> compl() {
        return monadicApply(ddManager::compl);
    }

    public XDD<E> not() {
        return monadicApply(ddManager::not);
    }

    public XDD<E> multInverse() {
        return monadicApply(ddManager::multInverse);
    }

    public XDD<E> addInverse() {
        return monadicApply(ddManager::addInverse);
    }

    public XDD<E> meet(XDD<E> g) {
        return apply(ddManager::meet, g);
    }

    public XDD<E> inf(XDD<E> g) {
        return apply(ddManager::inf, g);
    }

    public XDD<E> intersect(XDD<E> g) {
        return apply(ddManager::intersect, g);
    }

    public XDD<E> and(XDD<E> g) {
        return apply(ddManager::and, g);
    }

    public XDD<E> mult(XDD<E> g) {
        return apply(ddManager::mult, g);
    }

    public XDD<E> join(XDD<E> g) {
        return apply(ddManager::join, g);
    }

    public XDD<E> sup(XDD<E> g) {
        return apply(ddManager::sup, g);
    }

    public XDD<E> union(XDD<E> g) {
        return apply(ddManager::union, g);
    }

    public XDD<E> or(XDD<E> g) {
        return apply(ddManager::or, g);
    }

    public XDD<E> add(XDD<E> g) {
        return apply(ddManager::add, g);
    }

    /* Required DD methods */

    @Override
    protected XDD<E> thisCasted() {
        return this;
    }

    @Override
    public XDD<E> t() {
        assertNonConstant();
        long thenPtr = ddManager.getBackend().t(ptr);
        return new XDD<>(thenPtr, ddManager);
    }

    @Override
    public XDD<E> e() {
        assertNonConstant();
        long elsePtr = ddManager.getBackend().e(ptr);
        return new XDD<>(elsePtr, ddManager);
    }

    @Override
    public XDD<E> eval(boolean... input) {
        long evalPtr = ddManager.getBackend().eval(ddManager().ptr(), ptr, Conversions.asInts(input));
        return new XDD<>(evalPtr, ddManager);
    }

    public E eval(Map<String, Boolean> ddNameToTruthValueMap) {
        return eval(ddNameToTruthValueMap::get);
    }

    public E eval(Function<String, @Nullable Boolean> ddNameToTruthValue) {
        if (isConstant()) {
            return v();
        }
        String ddName = readName();
        Boolean value = ddNameToTruthValue.apply(ddName);
        if (value == null) {
            throw new NullPointerException(ddName + " has no assigned truth value.");
        }
        return value ? t().eval(ddNameToTruthValue) : e().eval(ddNameToTruthValue);
    }

    /* Other methods */

    @Override
    public String toString() {
        if (isConstant()) {
            return v().toString();
        }
        return super.toString();
    }
}
