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

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.SkipException;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class DotLayouterTest extends LayouterTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public DotLayouterTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testDotLayouter() {
        /* Run test only if dot is installed */
        if (!DotLayouter.isAvailable()) {
            throw new SkipException("DOT not available");
        }
        /* Layout */
        DotLayouter<ADD> dl = new DotLayouter<>();
        dl.layout(sumOfPredicatesABC());

        /* Assert result */
        assertNonOverlappingBoundingBoxes(dl);
        assertSizeGreaterThanZero(dl);
        assertInBoundingBox(dl);
        assertMirroringYieldsPositiveSizes(dl);
    }
}
