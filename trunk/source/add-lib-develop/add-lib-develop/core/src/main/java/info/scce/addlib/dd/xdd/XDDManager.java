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

import java.util.NoSuchElementException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.dd.DDManagerException;
import info.scce.addlib.parser.XDDLanguageLexer;
import info.scce.addlib.parser.XDDLanguageParser;
import info.scce.addlib.parser.XDDVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.checkerframework.checker.nullness.qual.NonNull;

public class XDDManager<T> extends DDManager<XDD<T>, ADDBackend> {

    private final BiMap<T, Double> valueMap;
    private double nextValueId;

    public XDDManager() {
        this(BackendProvider.getADDBackend());
    }

    public XDDManager(ADDBackend backend) {
        super(backend);
        valueMap = HashBiMap.create();
        nextValueId = 0;
    }

    /* Construct constant XDDs */

    public XDD<T> constant(T value) {
        long resultPtr = getBackend().constant(ptr, valueId(value));
        return new XDD<>(resultPtr, this).withRef();
    }

    public XDD<T> neutral() {
        return constant(neutralElement());
    }

    public XDD<T> bot() {
        return constant(botElement());
    }

    public XDD<T> top() {
        return constant(topElement());
    }

    public XDD<T> zero() {
        return constant(zeroElement());
    }

    public XDD<T> one() {
        return constant(oneElement());
    }

    /* Construct single variable XDDs */

    @Override
    public XDD<T> namedVar(String name) {
        return ithVar(varIdx(name));
    }

    public XDD<T> namedVar(String name, XDD<T> t, XDD<T> e) {
        return ithVar(varIdx(name), t, e);
    }

    public XDD<T> namedVar(String name, T t, T e) {
        return ithVar(varIdx(name), t, e);
    }

    public XDD<T> namedIthVar(String name, int i, T t, T e) {
        addVarName(name, i);
        return ithVar(i, t, e);
    }

    public XDD<T> namedIthVar(String name, int i, XDD<T> t, XDD<T> e) {
        addVarName(name, i);
        return ithVar(i, t, e);
    }

    @Override
    public XDD<T> ithVar(int i) {
        return ithVar(i, oneElement(), zeroElement());
    }

    public XDD<T> ithVar(int i, XDD<T> t, XDD<T> e) {
        long varPtr = getBackend().xddIthVar(ptr(), i);
        return varFromUnreferencedAddVarPtr(varPtr, t, e);
    }

    public XDD<T> ithVar(int i, T t, T e) {
        long varPtr = getBackend().xddIthVar(ptr(), i);
        return varFromUnreferencedAddVarPtr(varPtr, t, e);
    }

    public XDD<T> newVar() {
        return newVar(oneElement(), zeroElement());
    }

    public XDD<T> newVar(XDD<T> t, XDD<T> e) {
        long varPtr = getBackend().newVar(ptr());
        return varFromUnreferencedAddVarPtr(varPtr, t, e);
    }

    public XDD<T> newVar(T t, T e) {
        long varPtr = getBackend().newVar(ptr());
        return varFromUnreferencedAddVarPtr(varPtr, t, e);
    }

    public XDD<T> newVarAtLevel(int level) {
        return newVarAtLevel(level, oneElement(), zeroElement());
    }

    public XDD<T> newVarAtLevel(int level, XDD<T> t, XDD<T> e) {
        long varPtr = getBackend().newVarAtLevel(ptr(), level);
        return varFromUnreferencedAddVarPtr(varPtr, t, e);
    }

    public XDD<T> newVarAtLevel(int level, T t, T e) {
        long varPtr = getBackend().newVarAtLevel(ptr(), level);
        return varFromUnreferencedAddVarPtr(varPtr, t, e);
    }

    private XDD<T> varFromUnreferencedAddVarPtr(long unreferencedAddVarPtr, T v1, T v0) {
        getBackend().ref(unreferencedAddVarPtr);
        long addVarPtr = unreferencedAddVarPtr;
        XDD<T> t = constant(v1);
        XDD<T> e = constant(v0);
        long resultPtr = getBackend().ite(ptr(), addVarPtr, t.ptr(), e.ptr());
        XDD<T> result = new XDD<>(resultPtr, this).withRef();
        backend.deref(ptr(), addVarPtr);
        t.recursiveDeref();
        e.recursiveDeref();
        return result;
    }

    private XDD<T> varFromUnreferencedAddVarPtr(long unreferencedAddVarPtr, XDD<T> t, XDD<T> e) {
        backend.ref(unreferencedAddVarPtr);
        long addVarPtr = unreferencedAddVarPtr;
        long resultPtr = getBackend().ite(ptr(), addVarPtr, t.ptr(), e.ptr());
        XDD<T> result = new XDD<>(resultPtr, this).withRef();
        backend.deref(ptr(), addVarPtr);
        return result;
    }

    /* Value mapping */

    public @NonNull T v(double valueId) {
        // try asynchronous calls first to increase speed
        T value = valueMap.inverse().get(valueId);
        if (value != null) {
            return value;
        }
        synchronized (this) {
            value = valueMap.inverse().get(valueId);
        }
        if (value == null) {
            throw new NoSuchElementException(
                    String.format("The method assumes that the key '%f' is already in the map!",
                                  valueId));
        }
        return value;
    }

    public double valueId(T element) {
        synchronized (this) {
            Double value = valueMap.get(element);
            if (value == null) {
                value = nextValueId++;
                valueMap.put(element, value);
            }
            return value;
        }
    }

    /* Algebraic structure definition */

    protected T neutralElement() {
        throw undefinedInAlgebraicStructureException("neutralElement");
    }

    protected T botElement() {
        throw undefinedInAlgebraicStructureException("botElement");
    }

    protected T topElement() {
        throw undefinedInAlgebraicStructureException("topElement");
    }

    protected T zeroElement() {
        throw undefinedInAlgebraicStructureException("zeroElement");
    }

    protected T oneElement() {
        throw undefinedInAlgebraicStructureException("oneElement");
    }

    protected T inverse(T x) {
        throw undefinedInAlgebraicStructureException("inverse");
    }

    protected T compl(T x) {
        throw undefinedInAlgebraicStructureException("compl");
    }

    protected T not(T x) {
        throw undefinedInAlgebraicStructureException("not");
    }

    protected T multInverse(T x) {
        throw undefinedInAlgebraicStructureException("multInverse");
    }

    protected T addInverse(T x) {
        throw undefinedInAlgebraicStructureException("addInverse");
    }

    protected T meet(T left, T right) {
        throw undefinedInAlgebraicStructureException("meet");
    }

    protected T inf(T left, T right) {
        throw undefinedInAlgebraicStructureException("inf");
    }

    protected T intersect(T left, T right) {
        throw undefinedInAlgebraicStructureException("intersect");
    }

    protected T and(T left, T right) {
        throw undefinedInAlgebraicStructureException("and");
    }

    protected T mult(T left, T right) {
        throw undefinedInAlgebraicStructureException("mult");
    }

    protected T join(T left, T right) {
        throw undefinedInAlgebraicStructureException("join");
    }

    protected T sup(T left, T right) {
        throw undefinedInAlgebraicStructureException("sup");
    }

    protected T union(T left, T right) {
        throw undefinedInAlgebraicStructureException("union");
    }

    protected T or(T left, T right) {
        throw undefinedInAlgebraicStructureException("or");
    }

    protected T add(T left, T right) {
        throw undefinedInAlgebraicStructureException("add");
    }

    public T parseElement(String str) {
        throw undefinedInAlgebraicStructureException("parseElement");
    }

    protected DDManagerException undefinedInAlgebraicStructureException(String what) {
        return new DDManagerException(getClass().getSimpleName() + " does not define " + what);
    }

    @Override
    public XDD<T> parse(String str) {
        ANTLRInputStream inputStream = new ANTLRInputStream(str);
        XDDLanguageLexer lexer = new XDDLanguageLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XDDLanguageParser parser = new XDDLanguageParser(tokens);
        ParseTree tree = parser.start();
        XDDVisitor<T> ast = new XDDVisitor<>(this);
        return ast.visit(tree);
    }
}
