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
package info.scce.addlib.dd.xdd.grouplikedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class CountDDManagerTest extends DDManagerTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNeutralElement(ADDBackend addBackend) {
        CountDDManager ddManager = new CountDDManager(addBackend);

        /* Get constants */
        XDD<Integer> neutral = ddManager.neutral();
        XDD<Integer> someCount = ddManager.constant(53);

        /* Join with neutral element */
        XDD<Integer> neutral_join_someCount = neutral.join(someCount);
        XDD<Integer> someCount_join_neutral = someCount.join(neutral);
        XDD<Integer> neutral_join_neutral = neutral.join(neutral);

        /* Assert results */
        assertEquals(neutral_join_someCount, someCount);
        assertEquals(someCount_join_neutral, someCount);
        assertEquals(neutral_join_neutral, neutral);

        /* Release memory */
        neutral.recursiveDeref();
        someCount.recursiveDeref();
        neutral_join_someCount.recursiveDeref();
        someCount_join_neutral.recursiveDeref();
        neutral_join_neutral.recursiveDeref();

        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testJoin(ADDBackend addBackend) {
        CountDDManager ddManager = new CountDDManager(addBackend);

        /* Get constants */
        XDD<Integer> a = ddManager.constant(53);
        XDD<Integer> b = ddManager.constant(99);
        XDD<Integer> sum = ddManager.constant(53 + 99);

        /* Assert join */
        XDD<Integer> a_join_b = a.join(b);
        assertEquals(a_join_b, sum);

        /* Release memory */
        a.recursiveDeref();
        b.recursiveDeref();
        sum.recursiveDeref();
        a_join_b.recursiveDeref();

        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        CountDDManager ddManager = new CountDDManager(addBackend);

        /* Repeat test a few times with varying values */
        for (int x = 123; x < 4567; x += 89) {

            /* Assert parseElement is inverse of toString for the value of x */
            XDD<Integer> xddCount = ddManager.constant(x);
            String str = xddCount.toString();
            XDD<Integer> xddCountReproduced = ddManager.constant(ddManager.parseElement(str));
            assertEquals(xddCountReproduced, xddCount);

            /* Release memory */
            xddCount.recursiveDeref();
            xddCountReproduced.recursiveDeref();
        }
        assertRefCountZeroAndQuit(ddManager);
    }
}
