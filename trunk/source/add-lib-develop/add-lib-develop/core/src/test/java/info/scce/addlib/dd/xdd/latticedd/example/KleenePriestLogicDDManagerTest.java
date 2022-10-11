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
import info.scce.addlib.codegenerator.DotGenerator;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue.FALSE;
import static info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue.TRUE;
import static info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue.UNKNOWN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.AvoidCatchingGenericException"})
public class KleenePriestLogicDDManagerTest extends DDManagerTest {

    private KleenePriestLogicDDManager ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testSimilarToDocumentation(ADDBackend addBackend) {
        setUp(addBackend);

        /* A weak necessary criterion  */
        XDD<KleenePriestValue> recent = ddManager.namedVar("recentlyReceived", TRUE, UNKNOWN);

        /* A strong necessary criterion */
        XDD<KleenePriestValue> addrBook = ddManager.namedVar("senderInAddressBook", TRUE, FALSE);

        /* The conjunction of all necessary criteria */
        XDD<KleenePriestValue> urgentMail = recent.and(addrBook);
        recent.recursiveDeref();
        addrBook.recursiveDeref();

        /* Assert terminal values */
        assertEquals(urgentMail.t().t().v(), TRUE);
        assertEquals(urgentMail.t().e().v(), FALSE);
        assertEquals(urgentMail.e().t().v(), UNKNOWN);
        assertEquals(urgentMail.e().e().v(), FALSE);

        /* Print for documentation */
        new DotGenerator<XDD<KleenePriestValue>>().generateToStdOut(urgentMail, "urgentMail");

        /* Release memory */
        urgentMail.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new KleenePriestLogicDDManager(addBackend);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testAsInDocumentation(ADDBackend addBackend) {
        setUp(addBackend);

        /* A weak necessary criterion  */
        XDD<KleenePriestValue> recent = ddManager.namedVar("recentlyReceived", TRUE, UNKNOWN);

        /* A strong necessary criterion */
        XDD<KleenePriestValue> addrBook = ddManager.namedVar("senderInAddressBook", TRUE, FALSE);

        /* The conjunction of all necessary criteria */
        XDD<KleenePriestValue> urgentMail = recent.and(addrBook);
        recent.recursiveDeref();
        addrBook.recursiveDeref();

        /* ... */

        /* Release memory */
        urgentMail.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends",
          expectedExceptions = {IllegalArgumentException.class})
    public void testParseElement(ADDBackend addBackend) {
        setUp(addBackend);

        /* Get sine constants */
        XDD<KleenePriestValue> yes = ddManager.constant(TRUE);
        XDD<KleenePriestValue> no = ddManager.constant(FALSE);
        XDD<KleenePriestValue> maybe = ddManager.constant(UNKNOWN);

        /* Reconstruct constants and assert equality */
        XDD<KleenePriestValue> reconstructed_yes = ddManager.constant(ddManager.parseElement(yes.toString()));
        XDD<KleenePriestValue> reconstructed_no = ddManager.constant(ddManager.parseElement(no.toString()));
        XDD<KleenePriestValue> reconstructed_maybe = ddManager.constant(ddManager.parseElement(maybe.toString()));
        assertEquals(reconstructed_yes, yes);
        assertEquals(reconstructed_no, no);
        assertEquals(reconstructed_maybe, maybe);

        /* Test malformed input */
        try {
            ddManager.parseElement("haus");
            fail("parseElement did not fail when it should");
        } catch (IllegalArgumentException e) {
            /* Release memory */
            yes.recursiveDeref();
            no.recursiveDeref();
            maybe.recursiveDeref();
            reconstructed_yes.recursiveDeref();
            reconstructed_no.recursiveDeref();
            reconstructed_maybe.recursiveDeref();

            throw e;
        }
    }
}
