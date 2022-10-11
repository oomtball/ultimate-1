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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.scce.addlib.dd.DD;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class RecursiveIterator<D extends DD<?, D>> extends RecursiveTreeIterator<D> {

    private final Set<D> completed;

    public RecursiveIterator(D root) {
        this(Collections.singletonList(root));
    }

    public RecursiveIterator(List<D> roots) {
        super(roots);
        completed = new HashSet<>();
    }

    @Override
    protected @Nullable D processDD(D f, int i) {

        /* Ensure to process each node only if not completed */
        if (completed.contains(f)) {
            popDD();
            return null;
        }

        return processUnseenDD(f, i);
    }

    @Override
    protected D popDD() {

        /* Remember completed nodes */
        D f = super.popDD();
        completed.add(f);
        return f;
    }

    protected abstract @Nullable D processUnseenDD(D f, int i);
}
