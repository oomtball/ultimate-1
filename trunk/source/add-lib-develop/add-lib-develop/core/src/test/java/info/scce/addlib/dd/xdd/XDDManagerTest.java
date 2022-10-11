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
package info.scce.addlib.dd.xdd;

import java.util.HashMap;
import java.util.Map;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
public class XDDManagerTest extends DDManagerTest {

    private XDDManager<String> ddManager;

    @AfterMethod
    public void tearDown() {
        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testConstant(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test constant creation */
        XDD<String> c = ddManager.constant("Some string");
        String expected = "Some string";
        String actual = c.v();
        assertEquals(actual, expected);
        c.recursiveDeref();
    }

    public void setUp(ADDBackend addBackend) {
        ddManager = new XDDManager<>(addBackend);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testNewVarFromElements(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test variable creation from elements */
        XDD<String> f = ddManager.newVar("yes", "no");
        String expected_f_then = "yes";
        String expected_f_else = "no";
        String actual_f_then = f.t().v();
        String actual_f_else = f.e().v();
        assertEquals(actual_f_then, expected_f_then);
        assertEquals(actual_f_else, expected_f_else);
        f.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "cuddADDBackend")
    public void testNewVarFromConstants(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test variable creation from constants */
        XDD<String> c1 = ddManager.constant("yes");
        XDD<String> c0 = ddManager.constant("no");
        XDD<String> f = ddManager.newVar(c1, c0);
        String expected_f_then = "yes";
        String expected_f_else = "no";
        String actual_f_then = f.t().v();
        String actual_f_else = f.e().v();
        assertEquals(actual_f_then, expected_f_then);
        assertEquals(actual_f_else, expected_f_else);
        f.recursiveDeref();
        c0.recursiveDeref();
        c1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testIthVarFromElements(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test variable creation from elements */
        XDD<String> f = ddManager.ithVar(12, "yes", "no");
        String expected_f_then = "yes";
        String expected_f_else = "no";
        String actual_f_then = f.t().v();
        String actual_f_else = f.e().v();
        assertEquals(actual_f_then, expected_f_then);
        assertEquals(actual_f_else, expected_f_else);
        f.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testIthVarFromConstants(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test variable creation from constants */
        XDD<String> c1 = ddManager.constant("yes");
        XDD<String> c0 = ddManager.constant("no");
        XDD<String> f = ddManager.ithVar(3, c1, c0);
        String expected_f_then = "yes";
        String expected_f_else = "no";
        String actual_f_then = f.t().v();
        String actual_f_else = f.e().v();
        assertEquals(actual_f_then, expected_f_then);
        assertEquals(actual_f_else, expected_f_else);
        f.recursiveDeref();
        c0.recursiveDeref();
        c1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNamedVarFromElements(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test variable creation from elements */
        XDD<String> f = ddManager.namedVar("foo", "yes", "no");
        String expected_f_then = "yes";
        String expected_f_else = "no";
        String actual_f_then = f.t().v();
        String actual_f_else = f.e().v();
        assertEquals(actual_f_then, expected_f_then);
        assertEquals(actual_f_else, expected_f_else);
        f.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testNamedVarFromConstants(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test variable creation from constants */
        XDD<String> c1 = ddManager.constant("yes");
        XDD<String> c0 = ddManager.constant("no");
        XDD<String> f = ddManager.namedVar("bar", c1, c0);
        String expected_f_then = "yes";
        String expected_f_else = "no";
        String actual_f_then = f.t().v();
        String actual_f_else = f.e().v();
        assertEquals(actual_f_then, expected_f_then);
        assertEquals(actual_f_else, expected_f_else);
        f.recursiveDeref();
        c0.recursiveDeref();
        c1.recursiveDeref();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testSecureDeref(ADDBackend addBackend) {
        setUp(addBackend);

        XDD<String> c1 = ddManager.constant("yes");
        XDD<String> c0 = ddManager.constant("no");
        XDD<String> f = ddManager.namedVar("bar", c1, c0);
        f.recursiveDeref();
        c0.recursiveDeref();
        c1.recursiveDeref();
        try {
            c0.recursiveDeref();
            fail("Reference count should be 0");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Cannot dereference unreferenced DD");
        }
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testMapEval(ADDBackend addBackend) {
        setUp(addBackend);

        /* Test evaluation with values map */
        XDD<String> f = ddManager.namedVar("foo", "yes", "no");
        XDD<String> b = ddManager.namedVar("bar", "maybe", "no");
        XDD<String> c = ddManager.namedVar("c", b, f);
        f.recursiveDeref();
        b.recursiveDeref();
        Map<String, Boolean> map = new HashMap<>();
        map.put("c", true);
        map.put("bar", false);
        map.put("foo", true);
        assertEquals(c.eval(map), "no");
        map.put("c", true);
        map.put("bar", false);
        map.put("foo", false);
        assertEquals(c.eval(map), "no");
        map.put("c", false);
        map.put("bar", false);
        map.put("foo", true);
        assertEquals(c.eval(map), "yes");

        map.remove("c");
        try {
            c.eval(map);
            fail();
        } catch (NullPointerException ignored) {
        }
        c.recursiveDeref();
    }

}
