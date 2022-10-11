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
package info.scce.addlib.parser;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanVector;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanVectorLogicDDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class XDDVisitorTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testXDDSimple(ADDBackend addBackend) {
        BooleanVectorLogicDDManager ddManager = new BooleanVectorLogicDDManager(addBackend, 2);

        XDD<BooleanVector> one = ddManager.one();
        XDD<BooleanVector> parsedOne = ddManager.parse("\"[true, true]\"");
        assertEquals(parsedOne, one);
        parsedOne.recursiveDeref();

        XDD<BooleanVector> zero = ddManager.zero();
        XDD<BooleanVector> parsedZero = ddManager.parse("'[false, false]'");
        assertEquals(parsedZero, zero);
        parsedZero.recursiveDeref();

        XDD<BooleanVector> oneOrZero = one.or(zero);
        XDD<BooleanVector> parsedOneOrZero = ddManager.parse("'[true, true]' or '[false, false]'");
        assertEquals(parsedOneOrZero, oneOrZero);
        oneOrZero.recursiveDeref();
        parsedOneOrZero.recursiveDeref();

        XDD<BooleanVector> oneAndZero = one.and(zero);
        XDD<BooleanVector> parsedOneAndZero = ddManager.parse("'[true, true]' and '[false, false]'");
        assertEquals(parsedOneAndZero, oneAndZero);
        oneAndZero.recursiveDeref();
        parsedOneAndZero.recursiveDeref();

        XDD<BooleanVector> notOne = ddManager.parse("not '[true, true]'");
        XDD<BooleanVector> ff = ddManager.constant(new BooleanVector(false, false));
        assertEquals(notOne, ff);
        notOne.recursiveDeref();
        ff.recursiveDeref();

        XDD<BooleanVector> notZero = ddManager.parse("not '[false, false]'");
        XDD<BooleanVector> tt = ddManager.constant(new BooleanVector(true, true));
        assertEquals(notZero, tt);
        notZero.recursiveDeref();
        tt.recursiveDeref();

        one.recursiveDeref();
        zero.recursiveDeref();

        /* Release memory */
        assertEquals(ddManager.checkZeroRef(), 0);
        ddManager.quit();
    }
}
