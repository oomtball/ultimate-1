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

public class ComplementableSetLatticeDDManagerTest extends DDManagerTest {

    private ComplementableSetLatticeDDManager<String> ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        setUp(addBackend);
        XDD<ComplementableSet<String>> people =
                ddManager.constant(new ComplementableSet<>(new String[] {"Hans", "Peter", "Meier", "Alex"}));
        XDD<ComplementableSet<String>> morePeople =
                ddManager.constant(new ComplementableSet<>(new String[] {"Hans", "Bernd", "Alex", "Jan", "Hermann"}));
        XDD<ComplementableSet<String>> peopleParsed = ddManager.constant(ddManager.parseElement(people.toString()));
        XDD<ComplementableSet<String>> morePeopleParsed =
                ddManager.constant(ddManager.parseElement(morePeople.toString()));
        assertEquals(peopleParsed, people);
        assertEquals(morePeopleParsed, morePeople);
        people.recursiveDeref();
        peopleParsed.recursiveDeref();
        morePeople.recursiveDeref();
        morePeopleParsed.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new ComplementableSetLatticeDDManager<String>(addBackend) {

            @Override
            protected String parseComplementableSetElement(String str) {
                return str;
            }
        };
    }
}