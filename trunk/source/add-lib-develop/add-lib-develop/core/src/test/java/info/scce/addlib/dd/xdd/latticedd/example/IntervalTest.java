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

import java.util.Locale;

import org.testng.annotations.Test;
import static info.scce.addlib.dd.xdd.latticedd.example.Interval.parseInterval;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.AvoidCatchingGenericException"})
public class IntervalTest {

    @Test
    public void testEmpty() {
        assertEquals(Interval.empty(), parseInterval("(0, 0)"));
        assertEquals(Interval.empty(), parseInterval("(15, 1)"));
        assertEquals(Interval.empty(), parseInterval("[5, -1]"));
        assertEquals(Interval.empty(), parseInterval("[51, 1)"));
        assertEquals(Interval.empty(), parseInterval("(5, -1]"));
        assertEquals(parseInterval("[51, 1)"), parseInterval("(5, -1]"));
        assertEquals(parseInterval("[51, 1)"), parseInterval("(15, 1)"));
    }

    @Test
    public void testComplete() {

        /* We consider the (-Infinity, Infinity) the complete interval */
        assertEquals(Interval.complete(), parseInterval("(-Infinity, Infinity)"));

        /* Therefore, [-Infinity, Infinity] is invalid */
        try {
            parseInterval("[-Infinity, Infinity]");
            fail("Expected exception was not thrown");
        } catch (RuntimeException e) {
            String message = e.getMessage();
            assertNotNull(message);
            assertTrue(message.toLowerCase(Locale.ROOT).contains("infinity"));
        }
    }

    @Test
    public void testContains() {

        /* Positive examples */
        assertTrue(parseInterval("[3, 5]").contains(Interval.empty()));
        assertTrue(parseInterval("[3, Infinity)").contains(parseInterval("[3, 5]")));
        assertTrue(parseInterval("[3, Infinity)").contains(parseInterval("[3, Infinity)")));
        assertTrue(parseInterval("[3, Infinity)").contains(parseInterval("(3, Infinity)")));
        assertTrue(parseInterval("(-Infinity, Infinity)").contains(parseInterval("(5, Infinity)")));
        assertTrue(parseInterval("[3, 5]").contains(parseInterval("[3, -3]")));
        assertTrue(parseInterval("[1, 2]").contains(parseInterval("[1, 2)")));
        assertTrue(parseInterval("[1, 2]").contains(parseInterval("(1, 2]")));
        assertTrue(parseInterval("[1, 2]").contains(parseInterval("(1, 2)")));

        /* Negative examples */
        assertFalse(parseInterval("[3, Infinity)").contains(parseInterval("[1, 2]")));
        assertFalse(parseInterval("[1, 2]").contains(parseInterval("[1, 3]")));
        assertFalse(parseInterval("[1, 2]").contains(parseInterval("[4, 6]")));
        assertFalse(parseInterval("[1, 2]").contains(parseInterval("[0, 0.5]")));
        assertFalse(parseInterval("[1, 2)").contains(parseInterval("[1, 2]")));
        assertFalse(parseInterval("(1, 2]").contains(parseInterval("[1, 2]")));
        assertFalse(parseInterval("(1, 2)").contains(parseInterval("[1, 2]")));
        assertFalse(parseInterval("(1, 2]").contains(parseInterval("[1, 2]")));
        assertFalse(parseInterval("(1, 2]").contains(parseInterval("[1, 2)")));
    }

    @Test
    public void testUnion() {
        assertEquals(parseInterval("[2, 5]"), parseInterval("[2, 3]").union(parseInterval("(3, 5]")));
        assertEquals(parseInterval("[2, 6]"), parseInterval("[2, 5]").union(parseInterval("[3, 6]")));
        assertEquals(parseInterval("[2, 6]"), parseInterval("[2, 5)").union(parseInterval("(5, 6]")));
        assertEquals(parseInterval("[2, 6]"), parseInterval("[2, 2]").union(parseInterval("(5, 6]")));
        assertEquals(parseInterval("[2, 6]"), parseInterval("[2, 3]").union(parseInterval("[3, 6]")));
        assertEquals(parseInterval("(2, 6]"), parseInterval("(2, 3]").union(parseInterval("[3, 6]")));
        assertEquals(parseInterval("[2, 6)"), parseInterval("[2, 3]").union(parseInterval("[3, 6)")));
        assertEquals(parseInterval("(2, 6)"), parseInterval("(2, 3]").union(parseInterval("[3, 6)")));
        assertEquals(parseInterval("(-Infinity, 6)"), parseInterval("(-Infinity, 3]").union(parseInterval("[3, 6)")));
        assertEquals(parseInterval("(2, Infinity)"), parseInterval("(2, 3]").union(parseInterval("[3, Infinity)")));
    }

    @Test
    public void testUnionCommutative() {
        for (int i = 0; i < 64; i++) {
            Interval a = randomInterval();
            Interval b = randomInterval();
            assertEquals(b.union(a), a.union(b));
        }
    }

    private Interval randomInterval() {
        boolean lbIncluded = Math.random() < 0.6;
        boolean ubIncluded = Math.random() < 0.3;
        double lb = Math.random() * 12 - 34;
        double ub = Math.random() * 567 - 89;
        return new Interval(lb, lbIncluded, ub, ubIncluded);
    }

    @Test
    public void testEmptyNeutralToUnion() {
        for (int i = 0; i < 64; i++) {
            Interval a = randomInterval();
            assertEquals(a.union(Interval.empty()), a);
            assertEquals(Interval.empty().union(a), a);
        }
    }

    @Test
    public void testUnionIdempotent() {
        for (int i = 0; i < 64; i++) {
            Interval a = randomInterval();
            assertEquals(a.union(a), a);
        }
    }

    @Test
    public void testIntersection() {
        assertEquals(Interval.empty(), parseInterval("[2, 3]").intersect(parseInterval("(3, 5]")));
        assertEquals(parseInterval("[3, 5]"), parseInterval("[2, 5]").intersect(parseInterval("[3, 6]")));
        assertEquals(Interval.empty(), parseInterval("[2, 5)").intersect(parseInterval("(5, 6]")));
        assertEquals(Interval.empty(), parseInterval("[2, 2]").intersect(parseInterval("(5, 6]")));
        assertEquals(parseInterval("[3, 3]"), parseInterval("[2, 3]").intersect(parseInterval("[3, 6]")));
        assertEquals(parseInterval("[3, 3]"), parseInterval("(2, 3]").intersect(parseInterval("[3, 6]")));
        assertEquals(parseInterval("[3, 3]"), parseInterval("[2, 3]").intersect(parseInterval("[3, 6)")));
        assertEquals(parseInterval("[3, 3]"), parseInterval("(2, 3]").intersect(parseInterval("[3, 6)")));
        assertEquals(parseInterval("[1, 2)"), parseInterval("[1, 2)").intersect(parseInterval("[1, 2)")));
        assertEquals(parseInterval("(0, 3)"), parseInterval("(-10, 3)").intersect(parseInterval("(0, 6)")));
        assertEquals(parseInterval("(0, 3)"),
                     parseInterval("(-Infinity, 3)").intersect(parseInterval("(0, Infinity)")));
    }

    @Test
    public void testIntersectionCommutative() {
        for (int i = 0; i < 64; i++) {
            Interval a = randomInterval();
            Interval b = randomInterval();
            assertEquals(b.intersect(a), a.intersect(b));
        }
    }

    @Test
    public void testCompleteNeutralToIntersection() {
        for (int i = 0; i < 64; i++) {
            Interval a = randomInterval();
            assertEquals(a.intersect(Interval.complete()), a);
            assertEquals(Interval.complete().intersect(a), a);
        }
    }

    @Test
    public void testIntersectionIdempotent() {
        for (int i = 0; i < 64; i++) {
            Interval a = randomInterval();
            assertEquals(a.intersect(a), a);
        }
    }
}