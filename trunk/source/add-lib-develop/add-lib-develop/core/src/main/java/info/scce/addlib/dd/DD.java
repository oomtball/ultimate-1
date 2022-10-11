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
package info.scce.addlib.dd;

import info.scce.addlib.util.Conversions;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class DD<M extends DDManager<D, ?>, D extends DD<?, D>> {

    protected final long ptr;
    protected final M ddManager;

    public DD(long ptr, M ddManager) {
        this.ptr = ptr;
        this.ddManager = ddManager;
    }

    /* Common DD methods */

    public long ptr() {
        return ptr;
    }

    public long regularPtr() {
        return this.ddManager.backend.regularPtr(ptr);
    }

    public M ddManager() {
        return ddManager;
    }

    public D withRef() {
        ref();
        return thisCasted();
    }

    /* CUDD wrapper */

    public void ref() {
        this.ddManager.incRefCount(ptr);
        this.ddManager.backend.ref(ptr);
    }

    public void recursiveDeref() {

        /* Throws an exception if ptr is unreferenced */
        this.ddManager.decRefCount(ptr);
        this.ddManager.backend.deref(ddManager.ptr(), ptr);
    }

    public int readIndex() {
        assertNonConstant();
        return this.ddManager.backend.readIndex(ptr);
    }

    public int readPerm() {
        assertNonConstant();
        return this.ddManager.backend.readPerm(ddManager().ptr(), readIndex());
    }

    public String readName() {
        return ddManager().varName(readIndex());
    }

    public boolean isConstant() {
        return Conversions.asBoolean(this.ddManager.backend.isConstant(ptr));
    }

    public long dagSize() {
        return this.ddManager.backend.dagSize(ptr);
    }

    /* Required DD methods */

    protected abstract D thisCasted();

    public abstract D t();

    public abstract D e();

    public abstract D eval(boolean... input);

    /* Assertions */

    protected void assertEqualDDManager(DD<?, ?> dd) {
        if (!ddManager.equals(dd.ddManager())) {
            throw new DDManagerException(getClass().getSimpleName() + " operands must share the same " +
                    DDManager.class.getSimpleName());
        }
    }

    protected void assertEqualDDManager(DD<?, ?>... dds) {
        for (DD<?, ?> dd : dds) {
            assertEqualDDManager(dd);
        }
    }

    protected void assertConstant() {
        if (!isConstant()) {
            throw new DDException("Expected constant " + getClass().getSimpleName());
        }
    }

    protected void assertNonConstant() {
        if (isConstant()) {
            throw new DDException("Expected non-constant " + getClass().getSimpleName());
        }
    }

    /* Other methods */

    @Override
    public boolean equals(@Nullable Object otherObj) {
        if (otherObj instanceof DD<?, ?>) {
            DD<?, ?> other = (DD<?, ?>) otherObj;
            return ptr == other.ptr;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(ptr);
    }

    @Override
    public String toString() {
        if (isConstant()) {
            return "?";
        }
        return readName();
    }
}
