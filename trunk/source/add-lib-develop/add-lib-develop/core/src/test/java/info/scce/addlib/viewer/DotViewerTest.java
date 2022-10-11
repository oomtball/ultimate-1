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
package info.scce.addlib.viewer;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.layouter.DotLayouter;
import info.scce.addlib.utils.BackendProvider;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class DotViewerTest extends DDManagerTest {

    private final ADDBackend addBackend;
    private ADDManager ddManager;

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public DotViewerTest(ADDBackend addBackend) {
        this.addBackend = addBackend;
    }

    @BeforeClass
    public void setUp() {
        ddManager = new ADDManager(addBackend);
    }

    @AfterClass
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test
    public void testDotViewer() {
        /* Run test only if dot is installed */
        if (!DotLayouter.isAvailable()) {
            throw new SkipException("DOT not available");
        }

        /* Get some predicates */
        ADD predA = ddManager.namedVar("predA");
        ADD predB = ddManager.namedVar("predB");
        ADD predC = ddManager.namedVar("predC");

        /* Build a + b */
        ADD sumOfPredicatesAB = predA.plus(predB);
        predA.recursiveDeref();
        predB.recursiveDeref();

        /* Build a + b + c */
        ADD sumOfPredicatesABC = sumOfPredicatesAB.plus(predC);
        sumOfPredicatesAB.recursiveDeref();
        predC.recursiveDeref();

        /* Open and close viewer */
        DotViewer<ADD> viewer = new DotViewer<>();
        viewer.view(sumOfPredicatesABC, "ABC");
        viewer.closeAll();
        sumOfPredicatesABC.recursiveDeref();
    }
}
