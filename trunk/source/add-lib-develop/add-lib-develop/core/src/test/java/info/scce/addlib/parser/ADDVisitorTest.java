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
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ADDVisitorTest extends DDManagerTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testADDSimple(ADDBackend backend) {
        ADDManager ddManager = new ADDManager(backend);

        ADD zero = ddManager.constant(0);
        ADD parsedZero = ddManager.parse("0.0");
        assertEquals(parsedZero, zero);
        zero.recursiveDeref();
        parsedZero.recursiveDeref();

        ADD five = ddManager.constant(5.0);
        ADD parsedFive = ddManager.parse("5.0");
        assertEquals(parsedFive, five);
        parsedFive.recursiveDeref();

        ADD sevenPointSeven = ddManager.constant(7.7);
        ADD parsedSevenPointSeven = ddManager.parse("7.7");
        assertEquals(parsedSevenPointSeven, sevenPointSeven);
        parsedSevenPointSeven.recursiveDeref();

        ADD fivePlusSevenPointSeven = five.plus(sevenPointSeven);
        ADD parsedFivePlusparsedSevenPointSeven = ddManager.parse("5.0 + 7.7");
        assertEquals(parsedFivePlusparsedSevenPointSeven, fivePlusSevenPointSeven);
        parsedFivePlusparsedSevenPointSeven.recursiveDeref();
        fivePlusSevenPointSeven.recursiveDeref();

        ADD fiveMinusSevenPointSeven = five.minus(sevenPointSeven);
        ADD parsedFiveMinusSevenPointSeven = ddManager.parse("5.0 - 7.7");
        assertEquals(parsedFiveMinusSevenPointSeven, fiveMinusSevenPointSeven);
        fiveMinusSevenPointSeven.recursiveDeref();
        parsedFiveMinusSevenPointSeven.recursiveDeref();
        five.recursiveDeref();
        sevenPointSeven.recursiveDeref();

        ADD parsedFivePlusFiveTimesFive = ddManager.parse("5.0 + 5.0 * 5.0");
        assertEquals(parsedFivePlusFiveTimesFive.v(), 30.0, 0);
        parsedFivePlusFiveTimesFive.recursiveDeref();

        ADD parsedFivePlusFiveTimesFivePriority = ddManager.parse("(5.0 + 5.0) * 5.0");
        assertEquals(parsedFivePlusFiveTimesFivePriority.v(), 50.0, 0);
        parsedFivePlusFiveTimesFivePriority.recursiveDeref();

        ADD parsedFiveMinusTwoMinusOne = ddManager.parse("5.0 - 2.0 - 1.0");
        assertEquals(parsedFiveMinusTwoMinusOne.v(), 2.0, 0);
        parsedFiveMinusTwoMinusOne.recursiveDeref();

        ADD parsedFiveMinusTwoMinusOnePriority = ddManager.parse("5.0 - (2.0 - 1.0)");
        assertEquals(parsedFiveMinusTwoMinusOnePriority.v(), 4.0, 0);
        parsedFiveMinusTwoMinusOnePriority.recursiveDeref();

        /* Release memory */
        assertEquals(ddManager.checkZeroRef(), 0);
        ddManager.quit();
    }
}
