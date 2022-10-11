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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MultisetTest {

    private Multiset<Integer> a;
    private Multiset<Integer> b;
    private Multiset<Integer> c;

    @BeforeClass
    public void setUp() {
        a = new Multiset<>(1, 2, 3);
        b = new Multiset<>(1, 2, 2, 3, 3);
        c = new Multiset<>(2, 3, 3, 3);
    }

    @Test
    public void testIntersect() {
        Multiset<Integer> expected_a_meets_c = new Multiset<>(2, 3);
        assertEquals(a.intersect(c), expected_a_meets_c);
        assertEquals(c.intersect(a), expected_a_meets_c);
        Multiset<Integer> expected_b_meets_c = new Multiset<>(2, 3, 3);
        assertEquals(b.intersect(c), expected_b_meets_c);
        assertEquals(c.intersect(b), expected_b_meets_c);
    }

    @Test
    public void testUnion() {
        Multiset<Integer> expected_a_union_c = new Multiset<>(1, 2, 3, 3, 3);
        assertEquals(a.union(c), expected_a_union_c);
        assertEquals(c.union(a), expected_a_union_c);
        Multiset<Integer> expected_b_union_c = new Multiset<>(1, 2, 2, 3, 3, 3);
        assertEquals(b.union(c), expected_b_union_c);
        assertEquals(c.union(b), expected_b_union_c);
    }

    @Test
    public void testIncludes() {
        boolean expected_b_includes_a = true;
        assertEquals(b.includes(a), expected_b_includes_a);
        boolean expected_b_includes_c = false;
        assertEquals(b.includes(c), expected_b_includes_c);
        boolean expected_c_includes_b = false;
        assertEquals(c.includes(b), expected_c_includes_b);
    }

    @Test
    public void testPlus() {
        Multiset<Integer> expected_a_sum_c = new Multiset<>(1, 2, 2, 3, 3, 3, 3);
        assertEquals(a.plus(c), expected_a_sum_c);
        assertEquals(c.plus(a), expected_a_sum_c);
        Multiset<Integer> expected_b_sum_c = new Multiset<>(1, 2, 2, 2, 3, 3, 3, 3, 3);
        assertEquals(b.plus(c), expected_b_sum_c);
        assertEquals(c.plus(b), expected_b_sum_c);
    }

    @Test
    public void testSize() {
        int expected_size_a = 3;
        assertEquals(a.size(), expected_size_a);
        int expected_size_b = 5;
        assertEquals(b.size(), expected_size_b);
        int expected_size_b_sum_c = 9;
        assertEquals(b.plus(c).size(), expected_size_b_sum_c);
    }

    @Test
    public void testEqualsReflexivity() {
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
    }

    @Test
    public void testParseMultisetEmpty() {
        testParseMultiset(Multiset.emptySet());
    }

    public void testParseMultiset(Multiset<String> original) {
        String str = original.toString();
        Multiset<String> reconstructed = Multiset.parseMultiset(str, x -> x);
        assertEquals(reconstructed, original);
    }

    @Test
    public void testParseMultiset() {
        Multiset<String> multiset = new Multiset<>("Hans", "Jan", "Alex", "Alex", "Jan", "Alex", "Alex");
        testParseMultiset(multiset);
    }

    @Test
    public void testParseMultisetSingletons() {
        Multiset<String> multiset = new Multiset<>("Hans", "Jan", "Alex");
        testParseMultiset(multiset);
    }
}
