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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.traverser.PreorderTraverser;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public abstract class LayouterTest extends DDManagerTest {

    private final ADDBackend addBackend;
    protected ADDManager ddManager;
    private ADD sumOfPredicatesABC;

    public LayouterTest(ADDBackend addBackend) {
        this.addBackend = addBackend;
    }

    @BeforeMethod
    protected void setUp() {
        ddManager = new ADDManager(addBackend);
    }

    @AfterMethod
    public void tearDown() {
        if (sumOfPredicatesABC != null) {
            sumOfPredicatesABC.recursiveDeref();
        }
        assertRefCountZeroAndQuit(ddManager);
    }

    protected ADD sumOfPredicatesABC() {
        if (sumOfPredicatesABC == null) {

            /* Get variables */
            ADD predA = ddManager.namedVar("predA");
            ADD predB = ddManager.namedVar("predB");
            ADD predC = ddManager.namedVar("predC");

            /* a + b */
            ADD sumOfPredicatesAB = predA.plus(predB);
            predA.recursiveDeref();
            predB.recursiveDeref();

            /* a + b + c */
            sumOfPredicatesABC = sumOfPredicatesAB.plus(predC);
            sumOfPredicatesAB.recursiveDeref();
            predC.recursiveDeref();
        }
        return sumOfPredicatesABC;
    }

    /* Additional assertions */

    protected void assertNonOverlappingBoundingBoxes(Layouter<ADD> layouter) {
        Set<BoundingBox> bboxes = new HashSet<>();
        for (ADD f : new PreorderTraverser<>(layouter.roots())) {
            BoundingBox b = layouter.bbox(f);
            for (BoundingBox b2 : bboxes) {
                assertFalse(b.overlaps(b2));
            }
            bboxes.add(b);
        }
        assertFalse(bboxes.isEmpty());
    }

    protected void assertSizeGreaterThanZero(Layouter<ADD> layouter) {
        assertTrue(layouter.w() > 0);
        assertTrue(layouter.h() > 0);
        for (ADD f : new PreorderTraverser<>(layouter.roots())) {
            assertTrue(layouter.w(f) > 0);
            assertTrue(layouter.h(f) > 0);
        }
    }

    protected void assertInBoundingBox(Layouter<ADD> layouter) {
        for (ADD f : new PreorderTraverser<>(layouter.roots())) {

            /* Check coordinates */
            assertTrue(layouter.x() <= layouter.x(f));
            assertTrue(layouter.y() <= layouter.y(f));
            assertTrue(layouter.x(f) + layouter.w(f) <= layouter.x() + layouter.w());
            assertTrue(layouter.y(f) + layouter.h(f) <= layouter.y() + layouter.h());

            /* Check bounding boxes */
            assertTrue(layouter.bbox().contains(layouter.bbox(f)));
        }
    }

    protected void assertMirroringYieldsPositiveSizes(Layouter<ADD> layouter) {

        /* Derive expectations */
        double expectedMirroredX = -layouter.x() - layouter.w();
        double expectedMirroredY = -layouter.y() - layouter.h();
        ArrayList<Double> expectedMirroredXs = new ArrayList<>();
        ArrayList<Double> expectedMirroredYs = new ArrayList<>();
        for (ADD f : new PreorderTraverser<>(layouter.roots())) {
            expectedMirroredXs.add(-layouter.x(f) - layouter.w(f));
            expectedMirroredYs.add(-layouter.y(f) - layouter.h(f));
        }

        /* Mirrored coordinates */
        double oldFactorX = layouter.transformationFactorX();
        double oldOffsetX = layouter.transformationOffsetX();
        double oldFactorY = layouter.transformationFactorY();
        double oldOffsetY = layouter.transformationOffsetY();
        layouter.setTransformationX(-1, 0);
        layouter.setTransformationY(-1, 0);

        /* Assert mirrored coordinates */
        double actualMirroredX = layouter.x();
        double actualMirroredY = layouter.y();
        ArrayList<Double> actualMirroredXs = new ArrayList<>();
        ArrayList<Double> actualMirroredYs = new ArrayList<>();
        for (ADD f : new PreorderTraverser<>(layouter.roots())) {
            actualMirroredXs.add(layouter.x(f));
            actualMirroredYs.add(layouter.y(f));
            assertTrue(layouter.w(f) > 0);
            assertTrue(layouter.h(f) > 0);
        }
        assertEquals(actualMirroredX, expectedMirroredX, 0);
        assertEquals(actualMirroredY, expectedMirroredY, 0);
        assertEquals(actualMirroredXs, expectedMirroredXs);
        assertEquals(actualMirroredYs, expectedMirroredYs);

        /* Restore transformation */
        layouter.setTransformation(oldFactorX, oldOffsetX, oldFactorY, oldOffsetY);
    }
}
