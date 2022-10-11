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

import java.util.Arrays;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PermutationTest {

    @Test
    public void testComposition() {
        Permutation f = new Permutation(2, 4, 3, 0, 1);
        Permutation g = new Permutation(3, 1, 2, 0, 4);
        Permutation expected = new Permutation(0, 4, 3, 2, 1);
        Permutation actual = f.compose(g);
        assertEquals(actual, expected);
    }

    @Test
    public void testIdentity() {
        int[] expectedData = {0, 1, 2, 3, 4};
        Permutation expected = new Permutation(expectedData);
        Permutation actual = Permutation.identity(5);
        assertEquals(actual, expected, "incorrect identity");
        assertTrue(Arrays.equals(expectedData, actual.data()));
    }

    @Test
    public void testIdentityAndInverse() {
        Permutation id = Permutation.identity(5);
        Permutation f = new Permutation(2, 4, 3, 0, 1);
        assertEquals(f, id.compose(f), "f = id*f does not hold");
        assertEquals(f, f.compose(id), "f = f*id does not hold");
        assertEquals(f.inverse().inverse(), f, "--f = f does not hold");
        assertEquals(id.inverse(), id, "-id = id does not hold");
    }

    @Test
    public void testParsePermutation() {
        Permutation perm = new Permutation(4, 3, 0, 2, 1);
        String str = perm.toString();
        Permutation permutationReproduced = Permutation.parsePermutation(str);
        assertEquals(permutationReproduced, perm);
    }

    @Test
    public void testParseBooleanVectorEmpty() {
        Permutation perm = new Permutation();
        String str = perm.toString();
        Permutation reproduced = Permutation.parsePermutation(str);
        assertEquals(reproduced, perm);
    }
}
