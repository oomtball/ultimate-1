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
package info.scce.addlib.traverser;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import info.scce.addlib.dd.DD;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class RecursiveTreeIterator<D extends DD<?, D>> implements Iterator<D> {

    private final Iterator<D> roots;
    private final Stack<Call> callStack;
    private @Nullable D buffer;

    public RecursiveTreeIterator(D root) {
        this(Collections.singletonList(root));
    }

    public RecursiveTreeIterator(List<D> roots) {
        this.roots = roots.iterator();
        this.callStack = new Stack<>();
    }

    protected void pushDD(D dd) {
        Call call = new Call(dd);
        callStack.push(call);
    }

    protected D popDD() {
        return callStack.pop().dd();
    }

    @Override
    public boolean hasNext() {
        return fillBuffer() != null;
    }

    @Override
    public D next() {
        D bufferCpy = fillBuffer();
        if (bufferCpy == null) {
            throw new NoSuchElementException("The iteration has no more elements!");
        }
        buffer = null;
        return bufferCpy;
    }

    private @Nullable D fillBuffer() {
        while (buffer == null && (!callStack.isEmpty() || roots.hasNext())) {

            /* Move along the roots when one was completed */
            if (callStack.isEmpty()) {
                pushDD(roots.next());
            }

            /* Process calls until buffer filled */
            while (buffer == null && !callStack.isEmpty()) {
                Call top = callStack.peek();
                buffer = processDD(top.dd(), top.i());
                top.inc();
            }
        }
        return buffer;
    }

    protected abstract @Nullable D processDD(D f, int i);

    protected class Call {

        private final D dd;
        private int i;

        public Call(D dd) {
            this.dd = dd;
            this.i = 0;
        }

        public D dd() {
            return dd;
        }

        public int i() {
            return i;
        }

        public void inc() {
            i++;
        }
    }
}
