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
package info.scce.addlib.dd.add;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.parser.ADDLanguageLexer;
import info.scce.addlib.parser.ADDLanguageParser;
import info.scce.addlib.parser.ADDVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class ADDManager extends DDManager<ADD, ADDBackend> {

    public ADDManager() {
        this(BackendProvider.getADDBackend());
    }

    public ADDManager(ADDBackend backend) {
        super(backend);
    }

    public double readEpsilon() {
        return getBackend().readEpsilon(ptr);
    }

    public void setEpsilon(double epsilon) {
        getBackend().setEpsilon(ptr, epsilon);
    }

    public void setBackground(long background) {
        getBackend().setBackground(ptr, background);
    }

    /* Construct primitive ADDs */

    public ADD readOne() {
        long ddNodePtr = getBackend().readOne(ptr);
        return new ADD(ddNodePtr, this).withRef();
    }

    public ADD readZero() {
        long ddNodePtr = getBackend().readZero(ptr);
        return new ADD(ddNodePtr, this).withRef();
    }

    public ADD readPlusInfinity() {
        long ddNodePtr = getBackend().readPlusInfinity(ptr);
        return new ADD(ddNodePtr, this).withRef();
    }

    public ADD readMinusInfinity() {
        long ddNodePtr = getBackend().readMinusInfinity(ptr);
        return new ADD(ddNodePtr, this).withRef();
    }

    public ADD constant(double value) {
        long ddNodePtr = getBackend().constant(ptr, value);
        return new ADD(ddNodePtr, this).withRef();
    }

    @Override
    public ADD namedVar(String name) {
        return ithVar(varIdx(name));
    }

    @Override
    public ADD ithVar(int i) {
        long ddNodePtr = getBackend().ithVar(ptr, i);
        return new ADD(ddNodePtr, this).withRef();
    }

    public ADD newVar() {
        long ddNodePtr = getBackend().newVar(ptr);
        return new ADD(ddNodePtr, this).withRef();
    }

    public ADD newVarAtLevel(int level) {
        long ddNodePtr = getBackend().newVarAtLevel(ptr, level);
        return new ADD(ddNodePtr, this).withRef();
    }

    @Override
    public ADD parse(String str) {
        ANTLRInputStream inputStream = new ANTLRInputStream(str);
        ADDLanguageLexer lexer = new ADDLanguageLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ADDLanguageParser parser = new ADDLanguageParser(tokens);
        ADDVisitor ast = new ADDVisitor(this);
        return ast.visit(parser.expr());
    }
}
