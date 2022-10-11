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
package info.scce.addlib.serializer;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerException;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.XDDManager;
import info.scce.addlib.dd.xdd.grouplikedd.example.StringMonoidDDManager;
import info.scce.addlib.dd.xdd.latticedd.example.BooleanLogicDDManager;
import info.scce.addlib.dd.xdd.latticedd.example.KleenePriestLogicDDManager;
import info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue.FALSE;
import static info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue.TRUE;
import static info.scce.addlib.dd.xdd.latticedd.example.KleenePriestValue.UNKNOWN;
import static info.scce.addlib.serializer.DDProperty.VARINDEX;
import static info.scce.addlib.serializer.DDProperty.VARNAME;
import static info.scce.addlib.serializer.DDProperty.VARNAMEANDVARINDEX;
import static org.testng.Assert.assertEquals;

@SuppressWarnings("PMD.TooManyStaticImports")
public class XDDSerializerTest extends DDManagerTest {

    private final ADDBackend addBackend;

    private XDDSerializer<KleenePriestValue> kleenePriestSerializer;
    private KleenePriestLogicDDManager kleenePriestDDManager;
    private XDD<KleenePriestValue> kleenePriestExample;

    private XDDSerializer<String> stringMonoidSerializer;
    private StringMonoidDDManager stringMonoidDDManager;
    private XDD<String> stringMonoidExample;

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public XDDSerializerTest(ADDBackend addBackend) {
        this.addBackend = addBackend;
    }

    @BeforeMethod
    public void setUp() {

        /* Set up Kleene Priest logic example */
        kleenePriestSerializer = new XDDSerializer<>();
        kleenePriestDDManager = new KleenePriestLogicDDManager(addBackend);
        XDD<KleenePriestValue> recent = kleenePriestDDManager.namedVar("recentlyReceived", TRUE, UNKNOWN);
        XDD<KleenePriestValue> addrBook = kleenePriestDDManager.namedVar("senderInAddressBook", TRUE, FALSE);
        kleenePriestExample = recent.and(addrBook);
        recent.recursiveDeref();
        addrBook.recursiveDeref();

        /* Set up string monoid example */
        stringMonoidSerializer = new XDDSerializer<>();
        stringMonoidDDManager = new StringMonoidDDManager(addBackend);
        XDD<String> a = stringMonoidDDManager.ithVar(0, "\\;\n", "\\c");
        XDD<String> b = stringMonoidDDManager.ithVar(0, "\\;\n", "c\\c;\\\\;\\\\\\c\\;c;;c");
        stringMonoidExample = stringMonoidDDManager.ithVar(1, a, b);
        a.recursiveDeref();
        b.recursiveDeref();
    }

    @AfterMethod
    public void tearDown() {

        /* Tear down Kleene Priest logic example */
        kleenePriestExample.recursiveDeref();
        assertRefCountZeroAndQuit(kleenePriestDDManager);

        /* Tear down string monoid example */
        stringMonoidExample.recursiveDeref();
        assertRefCountZeroAndQuit(stringMonoidDDManager);
    }

    @Test
    public void testSerializationToFile() throws IOException {
        /* Serialize DDs */
        File targetFile = File.createTempFile("some", ".dd");
        targetFile.deleteOnExit();
        kleenePriestSerializer.serialize(targetFile, kleenePriestExample);

        /* Reconstruct DDs */
        XDD<KleenePriestValue> reconstruction =
                kleenePriestSerializer.deserialize(kleenePriestDDManager, targetFile, VARNAME);

        /* Assert equality */
        assertEquals(reconstruction, kleenePriestExample);
        reconstruction.recursiveDeref();
    }

    @Test
    public void testSerializationToStream() throws IOException {
        XDD<KleenePriestValue> reconstruction;

        /* Serialize DDs */
        try (PipedOutputStream out = new PipedOutputStream();
             PipedInputStream in = new PipedInputStream()) {
            in.connect(out);
            kleenePriestSerializer.serialize(out, kleenePriestExample);

            /* Reconstruct DDs */
            reconstruction = kleenePriestSerializer.deserialize(kleenePriestDDManager, in, VARNAME);
        }

        /* Assert equality */
        assertEquals(reconstruction, kleenePriestExample);
        reconstruction.recursiveDeref();
    }

    @Test
    public void testSerializationToString() {
        /* Serialize DDs */
        String str = stringMonoidSerializer.serialize(stringMonoidExample);

        /* Reconstruct DDs */
        XDD<String> reconstruction = stringMonoidSerializer.deserialize(stringMonoidDDManager, str, VARNAME);

        /* Assert equality */
        assertEquals(reconstruction, stringMonoidExample);
        reconstruction.recursiveDeref();
    }

    @Test
    public void testSerializationVariableOrder() {
        /* Serialize DDs */
        String str = kleenePriestSerializer.serialize(kleenePriestExample);

        /* Reconstruct DDs */
        KleenePriestLogicDDManager freshDDManager = new KleenePriestLogicDDManager(addBackend);
        XDD<KleenePriestValue> reconstructionVarOrder =
                kleenePriestSerializer.deserialize(freshDDManager, str, VARNAMEANDVARINDEX);
        assertEquals(reconstructionVarOrder.readName(), kleenePriestExample.readName());

        assertEquals(reconstructionVarOrder.t().readName(), kleenePriestExample.t().readName());
        assertEquals(reconstructionVarOrder.e().readName(), kleenePriestExample.e().readName());

        assertEquals(reconstructionVarOrder.t().t().v(), kleenePriestExample.t().t().v());
        assertEquals(reconstructionVarOrder.t().e().v(), kleenePriestExample.t().e().v());
        assertEquals(reconstructionVarOrder.e().t().v(), kleenePriestExample.e().t().v());
        assertEquals(reconstructionVarOrder.e().e().v(), kleenePriestExample.e().e().v());

        /* Assert equality */
        reconstructionVarOrder.recursiveDeref();
        assertRefCountZeroAndQuit(freshDDManager);
    }

    @Test(expectedExceptions = DDManagerException.class)
    public void testSerializationVariableOrderException() {
        /* Serialize DDs */
        /* We remember that:
         *  "recentlyReceived" is mapped to variable with index 0 and
         *  "senderInAddressBook" is mapped to variable with index 1*/
        String str = kleenePriestSerializer.serialize(kleenePriestExample);

        /* Reconstruct DDs */
        KleenePriestLogicDDManager freshDDManager = new KleenePriestLogicDDManager(addBackend);

        /* MakeDeserializationWithVarOrderImpossible is mapped to variable with index 0 */
        freshDDManager.namedVar("MakeDeserializationWithVarOrderImpossible", TRUE, FALSE);

        /* We cannot map "recentlyReceived" to variable with index 0 as another label has
         *  already been to the variable with this index. */
        kleenePriestSerializer.deserialize(freshDDManager, str, VARNAMEANDVARINDEX);
    }

    @Test
    public void testSerializationCreateFromVariableIndex() {
        /* Serialize DDs */
        String str = kleenePriestSerializer.serialize(kleenePriestExample);

        /* Reconstruct DDs */
        KleenePriestLogicDDManager freshDDManager = new KleenePriestLogicDDManager(addBackend);
        XDD<KleenePriestValue> reconstructionVarOrder =
                kleenePriestSerializer.deserialize(freshDDManager, str, VARINDEX);

        assertEquals(reconstructionVarOrder.readName(), "x0");

        assertEquals(reconstructionVarOrder.t().readName(), "x1");
        assertEquals(reconstructionVarOrder.e().readName(), "x1");

        assertEquals(reconstructionVarOrder.t().t().v(), kleenePriestExample.t().t().v());
        assertEquals(reconstructionVarOrder.t().e().v(), kleenePriestExample.t().e().v());
        assertEquals(reconstructionVarOrder.e().t().v(), kleenePriestExample.e().t().v());
        assertEquals(reconstructionVarOrder.e().e().v(), kleenePriestExample.e().e().v());

        /* Assert equality */
        reconstructionVarOrder.recursiveDeref();
        assertRefCountZeroAndQuit(freshDDManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testSerializationWithReordering(ADDBackend addBackend) {

        /*
         *       result
         *          \
         *           a
         *          /  \
         *         :    \
         *         |    |
         *         :    |
         * x1x3 - b     b - x1A2
         *      : |    /|
         *     /  |   : |
         *     :  d  :  c
         *     |  : \: /|
         *     \  : / \ |
         *        0     1
         *
         */

        /* Build XDD */
        XDDManager<Boolean> xddManager = new BooleanLogicDDManager(addBackend);
        XDD<Boolean> x0 = xddManager.namedVar("a");
        XDD<Boolean> x1 = xddManager.namedVar("b");
        XDD<Boolean> x2 = xddManager.namedVar("c");
        XDD<Boolean> x3 = xddManager.namedVar("d");
        XDD<Boolean> x0Not = x0.not();
        XDD<Boolean> x1A2 = x1.and(x2);
        XDD<Boolean> conj1 = x0.and(x1A2);
        XDD<Boolean> x1A3 = x1.and(x3);
        XDD<Boolean> conj2 = x0Not.and(x1A3);
        XDD<Boolean> result = conj1.or(conj2);

        /* Release space for reordering to take place */
        x0.recursiveDeref();
        x1.recursiveDeref();
        x2.recursiveDeref();
        x3.recursiveDeref();
        x0Not.recursiveDeref();
        x1A2.recursiveDeref();
        conj1.recursiveDeref();
        x1A3.recursiveDeref();
        conj2.recursiveDeref();

        /* after reordering:
         *
         *       result
         *          \
         *           a
         *          /  \
         *         :    \
         *         d    c
         *         :\  :
         *         | \/
         *         : :\
         *         |/  b
         *         :  : \
         *         | /  |
         *         0    1
         *
         */

        /* Reorder and serialize XDD */
        xddManager.reduceHeap(DDReorderingType.EXACT, 0);
        long sizeExpected = result.dagSize();
        XDDSerializer<Boolean> bddSerializer = new XDDSerializer<>();
        String test = bddSerializer.serialize(result);

        /* Deserialize and release space */
        BooleanLogicDDManager booleanLogicDDManager = new BooleanLogicDDManager(addBackend);
        XDD<Boolean> deserialized = bddSerializer.deserialize(booleanLogicDDManager, test, VARNAMEANDVARINDEX);
        long sizeActual = deserialized.dagSize();

        result.recursiveDeref();
        deserialized.recursiveDeref();

        /* Assert that the size of the XDD before and after deserialization is equal */
        assertEquals(sizeActual, sizeExpected);
        assertRefCountZeroAndQuit(xddManager);
        assertRefCountZeroAndQuit(booleanLogicDDManager);
    }
}
