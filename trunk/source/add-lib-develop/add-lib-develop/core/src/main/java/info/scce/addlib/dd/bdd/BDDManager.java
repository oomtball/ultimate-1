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

import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.parser.BDDLanguageLexer;
import info.scce.addlib.parser.BDDLanguageParser;
import info.scce.addlib.parser.BDDVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class BDDManager extends DDManager<BDD, BDDBackend> {

    public BDDManager() {
        this(BackendProvider.getBDDBackend());
    }

    public BDDManager(BDDBackend backend) {
        super(backend);
    }

    /* Construct primitive BDDs */

    public BDD readOne() {
        long ddNodePtr = getBackend().readOne(ptr);
        return new BDD(ddNodePtr, this).withRef();
    }

    public BDD readLogicZero() {
        long ddNodePtr = getBackend().readLogicZero(ptr);
        return new BDD(ddNodePtr, this).withRef();
    }

    @Override
    public BDD namedVar(String name) {
        return ithVar(varIdx(name));
    }

    @Override
    public BDD ithVar(int var) {
        long ddNodePtr = getBackend().ithVar(ptr, var);
        return new BDD(ddNodePtr, this).withRef();
    }

    public BDD newVar() {
        long ddNodePtr = getBackend().newVar(ptr);
        return new BDD(ddNodePtr, this).withRef();
    }

    public BDD newVarAtLevel(int level) {
        long ddNodePtr = getBackend().newVarAtLevel(ptr, level);
        return new BDD(ddNodePtr, this).withRef();
    }

    @Override
    public BDD parse(String str) {
        ANTLRInputStream inputStream = new ANTLRInputStream(str);
        BDDLanguageLexer lexer = new BDDLanguageLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BDDLanguageParser parser = new BDDLanguageParser(tokens);
        BDDVisitor ast = new BDDVisitor(this);
        return ast.visit(parser.expr());
    }
}
