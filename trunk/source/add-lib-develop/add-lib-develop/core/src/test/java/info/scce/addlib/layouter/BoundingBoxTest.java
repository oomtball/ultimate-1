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
package info.scce.addlib.layouter;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BoundingBoxTest {

    @Test
    public void testContainment() {
        BoundingBox outer = new BoundingBox(-12, 34, 253, 8374);
        BoundingBox inner = new BoundingBox(-3, 123, 123, 6374);

        /* Contained */
        assertTrue(outer.contains(outer));
        assertTrue(inner.contains(inner));
        assertTrue(outer.contains(inner));

        /* Not contained */
        assertFalse(inner.contains(outer));
    }

    @Test
    public void testContainment2() {
        BoundingBox upper = new BoundingBox(5, -1, 2, 4);
        BoundingBox lower = new BoundingBox(1, 2, 9, 3);

        /* Contained */
        assertTrue(upper.contains(upper));
        assertTrue(lower.contains(lower));

        /* Not contained */
        assertFalse(lower.contains(upper));
        assertFalse(upper.contains(lower));
    }

    @Test
    public void testContainment3() {
        BoundingBox upper = new BoundingBox(-2.75, -0.25, 5.0, 5.0);
        BoundingBox lower = new BoundingBox(2.0, 4.5, 0.5, 0.5);

        /* Contained */
        assertTrue(upper.contains(upper));
        assertTrue(lower.contains(lower));

        /* Not contained */
        assertFalse(lower.contains(upper));
        assertFalse(upper.contains(lower));
    }

    @Test
    public void testContainment4() {
        BoundingBox upper = new BoundingBox(1, 2, 3, 4);
        BoundingBox lower = new BoundingBox(4, 6, 8, 10);

        /* Contained */
        assertTrue(upper.contains(upper));
        assertTrue(lower.contains(lower));

        /* Not contained */
        assertFalse(lower.contains(upper));
        assertFalse(upper.contains(lower));
    }

    @Test
    public void testCompleteOverlap() {
        BoundingBox outer = new BoundingBox(-12, 34, 253, 8374);
        BoundingBox inner = new BoundingBox(-3, 123, 123, 6374);

        /* Compare overlap */
        double eps = 0.000001;
        double expectedOverlap = inner.area();
        assertEquals(outer.overlap(inner), expectedOverlap, eps);
        assertEquals(inner.overlap(outer), expectedOverlap, eps);

        /* Assert overlap */
        assertTrue(inner.overlaps(outer));
        assertTrue(outer.overlaps(inner));
    }

    @Test
    public void testPartialOverlap2() {
        BoundingBox upper = new BoundingBox(5, -1, 2, 4);
        BoundingBox lower = new BoundingBox(1, 2, 9, 3);

        /* Compare overlap */
        double eps = 0.000001;
        double expectedOverlap = 2.0 * 1.0;
        assertEquals(upper.overlap(lower), expectedOverlap, eps);
        assertEquals(lower.overlap(upper), expectedOverlap, eps);

        /* Assert overlap */
        assertTrue(upper.overlaps(lower));
        assertTrue(lower.overlaps(upper));
    }

    @Test
    public void testPartialOverlap3() {
        BoundingBox upper = new BoundingBox(-2.75, -0.25, 5.0, 5.0);
        BoundingBox lower = new BoundingBox(2.0, 4.5, 0.5, 0.5);

        /* Compare overlap */
        double eps = 0.000001;
        double expectedOverlap = 0.25 * 0.25;
        assertEquals(upper.overlap(lower), expectedOverlap, eps);
        assertEquals(lower.overlap(upper), expectedOverlap, eps);

        /* Assert overlap */
        assertTrue(upper.overlaps(lower));
        assertTrue(lower.overlaps(upper));
    }

    @Test
    public void testNoOverlap4() {
        BoundingBox upper = new BoundingBox(1, 2, 3, 4);
        BoundingBox lower = new BoundingBox(4, 6, 8, 10);

        /* Compare overlap */
        double eps = 0.000001;
        double expectedOverlap = 0.0;
        assertEquals(upper.overlap(lower), expectedOverlap, eps);
        assertEquals(lower.overlap(upper), expectedOverlap, eps);

        /* Assert no overlap */
        assertFalse(upper.overlaps(lower));
        assertFalse(lower.overlaps(upper));
    }
}
