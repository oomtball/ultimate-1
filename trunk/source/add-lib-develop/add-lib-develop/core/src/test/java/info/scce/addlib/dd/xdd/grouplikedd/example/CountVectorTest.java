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

import java.util.Random;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class CountVectorTest {

    @Test
    public void testJoin() {

        /* Get some */
        CountVector zero = CountVector.zero(3);
        CountVector a = new CountVector(1, 3, 7);
        CountVector b = new CountVector(9, 3, 2);
        CountVector c = new CountVector(4, 1, 5);

        /* Assert some sums */
        assertEquals(a.add(zero), a);
        assertEquals(a.add(b), new CountVector(10, 6, 9));
        assertEquals(a.add(c), new CountVector(5, 4, 12));
        assertEquals(b.add(c), new CountVector(13, 4, 7));
    }

    @Test
    public void testParseCountVector() {
        CountVector a = new CountVector(1, 2, 6);
        CountVector b = new CountVector(7, 3, 1);
        CountVector aParsed = CountVector.parseCountVector("[1, 2, 6]");
        CountVector bParsed = CountVector.parseCountVector("[7, 3, 1]");
        assertEquals(aParsed, a);
        assertEquals(bParsed, b);
    }

    @Test
    public void testParseCountVectorEmpty() {
        CountVector vec = new CountVector();
        String str = vec.toString();
        CountVector reproduced = CountVector.parseCountVector(str);
        assertEquals(reproduced, vec);
    }

    @Test
    public void testParseCountVectorRandom() {
        Random random = new Random(0);

        /* Repeat test a few times with random vlaues */
        for (int i = 0; i < 64; i++) {

            /* Assert that a parsing is reverse of toString for a random vector */
            CountVector randVec =
                    new CountVector(random.nextInt(16), random.nextInt(1234), random.nextInt(789456));
            String str = randVec.toString();
            CountVector reproduced = CountVector.parseCountVector(str);
            assertEquals(reproduced, randVec);
        }
    }
}