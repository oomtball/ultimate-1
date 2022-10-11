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

import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.backend.ZDDBackend;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.dd.DDManagerException;
import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.parser.ZDDLanguageLexer;
import info.scce.addlib.parser.ZDDLanguageParser;
import info.scce.addlib.parser.ZDDVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class ZDDManager extends DDManager<ZDD, ZDDBackend> {

    public ZDDManager() {
        this(BackendProvider.getZDDBackend());
    }

    public ZDDManager(ZDDBackend backend) {
        super(backend);
    }

    /* Construct primitive ZDDs */

    @Override
    public boolean reduceHeap(DDReorderingType heuristic, int minsize) {
        if (!heuristic.isZDDReorderingType()) {
            throw new DDManagerException("Unsupported reordering heuristic for ZDD");
        }
        return backend.reorder(ptr, heuristic, minsize) != 0;
    }

    @Override
    public boolean setVariableOrder(int[] permutation) {
        return backend.setVariableOrder(ptr, permutation);
    }

    @Override
    public void enableAutomaticReordering(DDReorderingType heuristic) {
        if (!heuristic.isZDDReorderingType()) {
            throw new DDManagerException("Unsupported reordering heuristic for ZDD");
        }
        backend.enableAutomaticReordering(ptr, heuristic);
    }

    @Override
    public void disableAutomaticReordering() {
        backend.disableAutomaticReordering(ptr);
    }

    public ZDD readOne() {
        long ddNodePtr = backend.readOne(ptr);
        return new ZDD(ddNodePtr, this).withRef();
    }

    public ZDD readZddOne(int i) {
        long ddNodePtr = backend.readZddOne(ptr, i);
        return new ZDD(ddNodePtr, this).withRef();
    }

    public ZDD readZero() {
        long ddNodePtr = backend.readZero(ptr);
        return new ZDD(ddNodePtr, this).withRef();
    }

    @Override
    public int readPerm(int i) {
        return backend.readPerm(ptr, i);
    }

    @Override
    public ZDD namedVar(String name) {
        return ithVar(varIdx(name));
    }

    @Override
    public ZDD ithVar(int i) {
        long ddNodePtr = backend.ithVar(ptr, i);
        return new ZDD(ddNodePtr, this).withRef();
    }

    @Override
    public ZDD parse(String zddAsString) {
        ANTLRInputStream inputStream = new ANTLRInputStream(zddAsString);
        ZDDLanguageLexer lexer = new ZDDLanguageLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ZDDLanguageParser parser = new ZDDLanguageParser(tokens);

        ZDDVisitor ast = new ZDDVisitor(this);
        return ast.visit(parser.start());
    }
}
