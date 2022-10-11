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

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class PartitionDDManagerTest extends DDManagerTest {

    private PartitionLatticeDDManager<Double> ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get some constants */
        XDD<Partition<Double>> a = ddManager.constant(new Partition(5.0, 7.3, 5.2));
        XDD<Partition<Double>> b = ddManager.constant(new Partition(4.0, 2.4, 5.2));
        XDD<Partition<Double>> a_meet_b = a.meet(b);

        /* Reconstruct the constants */
        XDD<Partition<Double>> reconstructed_a = ddManager.constant(ddManager.parseElement(a.toString()));
        XDD<Partition<Double>> reconstructed_b = ddManager.constant(ddManager.parseElement(b.toString()));
        XDD<Partition<Double>> reconstructed_a_meet_b = ddManager.constant(ddManager.parseElement(a_meet_b.toString()));
        assertEquals(reconstructed_a, a);
        assertEquals(reconstructed_b, b);
        assertEquals(reconstructed_a_meet_b, a_meet_b);

        /* Release memory */
        a.recursiveDeref();
        b.recursiveDeref();
        a_meet_b.recursiveDeref();
        reconstructed_a.recursiveDeref();
        reconstructed_b.recursiveDeref();
        reconstructed_a_meet_b.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new PartitionLatticeDDManager<Double>(addBackend) {

            @Override
            protected Double parsePartitionElement(String str) {
                return Double.parseDouble(str);
            }
        };
    }
}
