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
package info.scce.addlib.dd.xdd.ringlikedd.example;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class SquareMatrixTest {

    @Test
    public void testSquareMatrix() {

        /* Get some test matrices */
        SquareMatrix m0 = new SquareMatrix(new double[][] {{1, 2}, {4, 8}});
        SquareMatrix m1 = new SquareMatrix(new double[][] {{2, 0}, {8, 16}});

        /* Assert additive operations */
        SquareMatrix expectedSum = new SquareMatrix(new double[][] {{3, 2}, {12, 24}});
        SquareMatrix expectedAddInverse0 = new SquareMatrix(new double[][] {{-1, -2}, {-4, -8}});
        SquareMatrix expectedAddInverse1 = new SquareMatrix(new double[][] {{-2, -0}, {-8, -16}});
        assertEquals(m0.add(m1), expectedSum);
        assertEquals(m0.addInverse(), expectedAddInverse0);
        assertEquals(m1.addInverse(), expectedAddInverse1);
    }

    @Test
    public void testParseSquareMatrix() {
        SquareMatrix m = new SquareMatrix(new double[][] {{0.0, -2.0}, {4.0, 16.0}});
        String str = m.toString();
        SquareMatrix reproduced = SquareMatrix.parseSquareMatrix(str);
        assertEquals(reproduced, m);
    }
}
