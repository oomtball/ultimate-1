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
package info.scce.addlib.dd.zdd;

import info.scce.addlib.dd.DD;
import info.scce.addlib.util.Conversions;

public class ZDD extends DD<ZDDManager, ZDD> {

    public ZDD(long ptr, ZDDManager manager) {
        super(ptr, manager);
    }



    /* Required DD methods */

    public boolean v() {
        assertConstant();
        ZDD one = ddManager.readOne();
        boolean isOne = this.equals(one);
        one.recursiveDeref();
        return isOne;
    }

    @Override
    protected ZDD thisCasted() {
        return this;
    }

    @Override
    public void recursiveDeref() {
        /* Throws an exception if ptr is unreferenced */
        this.ddManager.decRefCount(ptr);
        ddManager.getBackend().deref(ddManager().ptr(), ptr);
    }

    @Override
    public ZDD t() {
        assertNonConstant();
        long resultPtr = ddManager.getBackend().t(ptr);
        return new ZDD(resultPtr, ddManager);
    }

    @Override
    public ZDD e() {
        assertNonConstant();
        long resultPtr = ddManager.getBackend().e(ptr);
        return new ZDD(resultPtr, ddManager);
    }

    @Override
    public ZDD eval(boolean... input) {
        long resultPtr = ddManager.getBackend().eval(ddManager().ptr(), ptr, Conversions.asInts(input));
        return new ZDD(resultPtr, ddManager);
    }

    /* Other methods */

    public ZDD union(ZDD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().union(ddManager.ptr(), ptr, g.ptr);
        return new ZDD(resultPtr, ddManager).withRef();
    }

    public ZDD intersect(ZDD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().intersect(ddManager.ptr(), ptr, g.ptr);
        return new ZDD(resultPtr, ddManager).withRef();
    }

    public ZDD diff(ZDD g) {
        assertEqualDDManager(g);
        long resultPtr = ddManager.getBackend().diff(ddManager.ptr(), ptr, g.ptr);
        return new ZDD(resultPtr, ddManager).withRef();
    }

    public ZDD change(int var) {
        long resultPtr = ddManager.getBackend().change(ddManager.ptr(), ptr, var);
        return new ZDD(resultPtr, ddManager).withRef();
    }

    public ZDD subset0(int var) {
        long resultPtr = ddManager.getBackend().subset0(ddManager.ptr(), ptr, var);
        return new ZDD(resultPtr, ddManager).withRef();
    }

    public ZDD subset1(int var) {
        long resultPtr = ddManager.getBackend().subset1(ddManager.ptr(), ptr, var);
        return new ZDD(resultPtr, ddManager).withRef();
    }

    @Override
    public String toString() {
        if (isConstant()) {
            ZDD one = ddManager.readOne();
            String lbl = this.equals(one) ? "1" : "0";
            one.recursiveDeref();
            return lbl;
        }
        return super.toString();
    }
}
