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
package info.scce.addlib.tools;

import java.util.Map;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class NodesDepthMetricsTest extends DDDistributionMetricsTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testNodesDepthDistributionBDD(BDDBackend bddBackend) {
        setUpBDD(bddBackend);
        NodeDepthMetrics ddMetrics = new NodeDepthMetrics(bdd);
        Map<Integer, Integer> distribution = ddMetrics.getDistribution();

        assertEquals(distribution.size(), 4);
        assertMappedElementIsEqual(distribution, 0, 1);
        assertMappedElementIsEqual(distribution, 1, 2);
        assertMappedElementIsEqual(distribution, 2, 4);
        assertMappedElementIsEqual(distribution, 3, 1);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testNodesDepthMetricsBDD(BDDBackend bddBackend) {
        setUpBDD(bddBackend);
        NodeDepthMetrics ddMetrics = new NodeDepthMetrics(bdd);
        ddMetrics.getDistribution();

        assertEquals(ddMetrics.min(), 0);
        assertEquals(ddMetrics.max(), 3);
        assertEquals(ddMetrics.median(), 2);
        assertEquals(ddMetrics.mean(), 1.625, 0.00001);
        assertEquals(ddMetrics.std(), 0.85695682, 0.00001);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNodesDepthDistributionADD(ADDBackend addBackend) {
        setupADD(addBackend);
        NodeDepthMetrics ddMetrics = new NodeDepthMetrics(add);
        Map<Integer, Integer> distribution = ddMetrics.getDistribution();

        assertEquals(distribution.size(), 4);
        assertMappedElementIsEqual(distribution, 0, 1);
        assertMappedElementIsEqual(distribution, 1, 2);
        assertMappedElementIsEqual(distribution, 2, 3);
        assertMappedElementIsEqual(distribution, 3, 4);
        tearDownADD();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNodesDepthMetricsADD(ADDBackend addBackend) {
        setupADD(addBackend);
        NodeDepthMetrics ddMetrics = new NodeDepthMetrics(add);
        ddMetrics.getDistribution();

        assertEquals(ddMetrics.min(), 0);
        assertEquals(ddMetrics.max(), 3);
        assertEquals(ddMetrics.median(), 2);
        assertEquals(ddMetrics.mean(), 2.0, 0.00001);
        assertEquals(ddMetrics.std(), 1.0, 0.00001);
        tearDownADD();
    }

}
