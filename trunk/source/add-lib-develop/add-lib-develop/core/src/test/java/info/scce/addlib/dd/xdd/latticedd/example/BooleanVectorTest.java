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

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class BooleanVectorTest {

    @Test
    public void testAnd() {
        BooleanVector a = new BooleanVector(true, false, true, false);
        BooleanVector b = new BooleanVector(true, true, false, false);
        BooleanVector expected_a_and_b = new BooleanVector(true, false, false, false);
        BooleanVector actual_a_and_b = a.and(b);
        assertEquals(actual_a_and_b, expected_a_and_b);
    }

    @Test
    public void testOr() {
        BooleanVector a = new BooleanVector(true, false, true, false);
        BooleanVector b = new BooleanVector(true, true, false, false);
        BooleanVector expected_a_or_b = new BooleanVector(true, true, true, false);
        BooleanVector actual_a_or_b = a.or(b);
        assertEquals(actual_a_or_b, expected_a_or_b);
    }

    @Test
    public void testNot() {
        BooleanVector a = new BooleanVector(true, false, true, true);
        BooleanVector expected_not_a = new BooleanVector(false, true, false, false);
        BooleanVector actual_not_a = a.not();
        assertEquals(actual_not_a, expected_not_a);
    }

    @Test
    public void testParseBooleanVectorRandom() {

        /* Repeat test a few times with random vlaues */
        for (int i = 0; i < 64; i++) {

            /* Assert that a parsing is reverse of toString for a random vector */
            BooleanVector randVec = new BooleanVector(Math.random() < 0.3, Math.random() < 0.8, Math.random() < 0.7, Math.random() < 0.5);
            String str = randVec.toString();
            BooleanVector reproduced = BooleanVector.parseBooleanVector(str);
            assertEquals(reproduced, randVec);
        }
    }

    @Test
    public void testParseBooleanVectorEmpty() {
        BooleanVector vec = new BooleanVector();
        String str = vec.toString();
        BooleanVector reproduced = BooleanVector.parseBooleanVector(str);
        assertEquals(reproduced, vec);
    }
}
