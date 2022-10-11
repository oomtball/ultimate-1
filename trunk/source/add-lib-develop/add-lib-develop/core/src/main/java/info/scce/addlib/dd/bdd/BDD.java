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
package info.scce.addlib.dd.bdd;

import java.util.HashMap;
import java.util.Map;

import info.scce.addlib.dd.DD;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanLogicDDManager;
import info.scce.addlib.util.Conversions;
import info.scce.addlib.utils.DDConversions;

public class BDD extends DD<BDDManager, BDD> {

    public BDD(long ptr, BDDManager ddManager) {
        super(ptr, ddManager);
    }

    public boolean v() {
        assertConstant();
        BDD one = ddManager.readOne();
        boolean isOne = this.equals(one);
        one.recursiveDeref();
        return isOne;
    }

    /* CUDD wrapper */

    public BDD not() {
        long resultPtr = this.ddManager.getBackend().not(ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD ite(BDD t, BDD e) {
        assertEqualDDManager(t, e);
        long resultPtr = this.ddManager.getBackend().ite(ddManager().ptr(), ptr, t.ptr, e.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD iteLimit(BDD t, BDD e, int limit) {
        assertEqualDDManager(t, e);
        long resultPtr = this.ddManager.getBackend().iteLimit(ddManager().ptr(), ptr, t.ptr, e.ptr, limit);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD iteConstant(BDD t, BDD e) {
        assertEqualDDManager(t, e);
        long resultPtr = this.ddManager.getBackend().iteConstant(ddManager.ptr(), ptr, t.ptr, e.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD intersect(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager.getBackend().intersect(ddManager.ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD and(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager.getBackend().and(ddManager.ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD andLimit(BDD g, int limit) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager.getBackend().andLimit(ddManager.ptr(), ptr, g.ptr, limit);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD or(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager.getBackend().or(ddManager.ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD orLimit(BDD g, int limit) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager.getBackend().orLimit(ddManager.ptr(), ptr, g.ptr, limit);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD nand(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager.getBackend().and(ddManager().ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD nor(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager().getBackend().nor(ddManager().ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD xor(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager().getBackend().xor(ddManager().ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD xnor(BDD g) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager().getBackend().xnor(ddManager().ptr(), ptr, g.ptr);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD xnorLimit(BDD g, int limit) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager().getBackend().xnorLimit(ddManager().ptr(), ptr, g.ptr, limit);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public boolean leq(BDD g) {
        assertEqualDDManager(g);
        int result = this.ddManager().getBackend().leq(ddManager().ptr(), ptr, g.ptr);
        return Conversions.asBoolean(result);
    }

    public BDD compose(BDD g, int v) {
        assertEqualDDManager(g);
        long resultPtr = this.ddManager().getBackend().compose(ddManager().ptr(), ptr, g.ptr, v);
        return new BDD(resultPtr, ddManager).withRef();
    }

    public BDD vectorCompose(BDD... vector) {
        assertEqualDDManager(vector);
        long resultPtr =
                this.ddManager().getBackend().vectorCompose(ddManager().ptr(), ptr, DDConversions.ptrs(vector));
        return new BDD(resultPtr, ddManager).withRef();
    }

    /* Required DD methods */

    @Override
    protected BDD thisCasted() {
        return this;
    }

    @Override
    public BDD t() {
        assertNonConstant();
        long resultPtr = ddManager.getBackend().t(ptr);
        return new BDD(resultPtr, ddManager);
    }

    @Override
    public BDD e() {
        assertNonConstant();
        long resultPtr = ddManager.getBackend().e(ptr);
        return new BDD(resultPtr, ddManager);
    }

    @Override
    public BDD eval(boolean... input) {
        long resultPtr = ddManager.getBackend().eval(ddManager().ptr(), ptr, Conversions.asInts(input));
        return new BDD(resultPtr, ddManager);
    }

    /* Conversion to XDD */

    public XDD<Boolean> toXDD(BooleanLogicDDManager ddManagerTarget) {
        HashMap<Long, XDD<Boolean>> cache = new HashMap<>();
        XDD<Boolean> result = this.toXDDRecursive(ddManagerTarget, cache);

        /* Dereference intermediate results */
        cache.remove(this.ptr);
        for (XDD<Boolean> g : cache.values()) {
            g.recursiveDeref();
        }

        return result;
    }

    private XDD<Boolean> toXDDRecursive(BooleanLogicDDManager ddManagerTarget, Map<Long, XDD<Boolean>> cache) {
        XDD<Boolean> result = cache.get(this.ptr);
        if (result == null) {
            if (isConstant()) {
                BDD bddOne = ddManager.readOne();
                boolean value = this.equals(bddOne);
                result = ddManagerTarget.constant(value);
                bddOne.recursiveDeref();
            } else {
                XDD<Boolean> t;
                XDD<Boolean> e;
                String name = readName();
                int idx = ddManagerTarget.varIdx(name);
                boolean isComplement = Conversions.asBoolean(ddManager.getBackend().isComplement(this.ptr));
                if (isComplement) {
                    BDD tmp = t().not();
                    t = tmp.toXDDRecursive(ddManagerTarget, cache);
                    tmp.recursiveDeref();
                    tmp = e().not();
                    e = tmp.toXDDRecursive(ddManagerTarget, cache);
                    tmp.recursiveDeref();
                } else {
                    t = t().toXDDRecursive(ddManagerTarget, cache);
                    e = e().toXDDRecursive(ddManagerTarget, cache);
                }
                result = ddManagerTarget.ithVar(idx, t, e);
            }
            cache.put(this.ptr, result);
        }
        return result;
    }

    /* Other methods */

    @Override
    public String toString() {
        if (isConstant()) {
            BDD one = ddManager.readOne();
            String lbl = this.equals(one) ? "1" : "0";
            one.recursiveDeref();
            return lbl;
        }
        return super.toString();
    }
}
