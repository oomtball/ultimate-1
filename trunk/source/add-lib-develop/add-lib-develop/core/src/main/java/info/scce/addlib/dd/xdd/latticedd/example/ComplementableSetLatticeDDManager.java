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
package info.scce.addlib.dd.xdd.latticedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.xdd.latticedd.ComplementableLatticeDDManager;

public class ComplementableSetLatticeDDManager<E> extends ComplementableLatticeDDManager<ComplementableSet<E>> {

    public ComplementableSetLatticeDDManager() {
        this(BackendProvider.getADDBackend());
    }

    public ComplementableSetLatticeDDManager(ADDBackend backend) {
        super(backend);
    }

    @Override
    protected ComplementableSet<E> meet(ComplementableSet<E> left, ComplementableSet<E> right) {
        return left.intersect(right);
    }

    @Override
    protected ComplementableSet<E> join(ComplementableSet<E> left, ComplementableSet<E> right) {
        return left.union(right);
    }

    @Override
    protected ComplementableSet<E> botElement() {
        return ComplementableSet.emptySet();
    }

    @Override
    protected ComplementableSet<E> topElement() {
        return ComplementableSet.completeSet();
    }

    @Override
    protected ComplementableSet<E> compl(ComplementableSet<E> x) {
        return x.complement();
    }

    @Override
    protected ComplementableSet<E> intersect(ComplementableSet<E> left, ComplementableSet<E> right) {
        return meet(left, right);
    }

    @Override
    protected ComplementableSet<E> union(ComplementableSet<E> left, ComplementableSet<E> right) {
        return join(left, right);
    }

    @Override
    public ComplementableSet<E> parseElement(String str) {
        return ComplementableSet.parseComplementableSet(str, this::parseComplementableSetElement);
    }

    protected E parseComplementableSetElement(String str) {
        throw undefinedInAlgebraicStructureException("parseComplementableSetElement");
    }
}
