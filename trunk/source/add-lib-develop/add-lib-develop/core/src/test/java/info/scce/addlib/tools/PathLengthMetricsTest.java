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
import static org.testng.Assert.assertNotNull;

public class PathLengthMetricsTest extends DDDistributionMetricsTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testDepthDistributionBDD(BDDBackend bddBackend) {
        setUpBDD(bddBackend);
        PathLengthMetrics ddMetrics = new PathLengthMetrics(bdd);
        ddMetrics.generateDistribution();
        Map<Integer, Integer> distribution = ddMetrics.getDistribution();

        assertEquals(distribution.size(), 3);

        assertMappedElementIsEqual(distribution, 2, 1);
        assertMappedElementIsEqual(distribution, 3, 4);
        assertMappedElementIsEqual(distribution, 4, 4);
        tearDownBDD();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultBDDBackends")
    public void testMetricsOutputBDD(BDDBackend bddBackend) {
        setUpBDD(bddBackend);
        PathLengthMetrics ddMetrics = new PathLengthMetrics(bdd);
        ddMetrics.getDistribution();

        assertEquals(ddMetrics.min(), 2);
        assertEquals(ddMetrics.max(), 4);
        assertEquals(ddMetrics.median(), 3);
        assertEquals(ddMetrics.mean(), 10.0 / 3, 0.00001);
        assertEquals(ddMetrics.std(), Math.sqrt(4.0 / 9), 0.00001);
        tearDownBDD();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testDepthDistributionADD(ADDBackend addBackend) {
        setupADD(addBackend);
        PathLengthMetrics ddMetrics = new PathLengthMetrics(add);
        ddMetrics.generateDistribution();
        Map<Integer, Integer> distribution = ddMetrics.getDistribution();

        assertEquals(distribution.size(), 1);

        Integer depth3Nodes = distribution.get(3);
        assertNotNull(depth3Nodes);
        assertEquals(depth3Nodes, Integer.valueOf(8));

        tearDownADD();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMetricsOutputADD(ADDBackend addBackend) {
        setupADD(addBackend);
        PathLengthMetrics ddMetrics = new PathLengthMetrics(add);
        ddMetrics.getDistribution();

        assertEquals(ddMetrics.min(), 3);
        assertEquals(ddMetrics.max(), 3);
        assertEquals(ddMetrics.median(), 3);
        assertEquals(ddMetrics.mean(), 3.0, 0.00001);
        assertEquals(ddMetrics.std(), 0.0, 0.00001);
        tearDownADD();
    }

}
