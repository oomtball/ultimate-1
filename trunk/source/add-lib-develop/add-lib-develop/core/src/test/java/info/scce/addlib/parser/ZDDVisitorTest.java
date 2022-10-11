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
package info.scce.addlib.parser;

import info.scce.addlib.backend.ZDDBackend;
import info.scce.addlib.dd.zdd.ZDD;
import info.scce.addlib.dd.zdd.ZDDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ZDDVisitorTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultZDDBackends")
    public void testZddParserAB(ZDDBackend zddBackend) {
        ZDDManager zddManager = new ZDDManager(zddBackend);
        ZDD A = zddManager.parse("false");
        ZDD B = zddManager.parse("true");

        ZDD C = zddManager.parse("CHANGE true 0");
        ZDD C_T = C.t();
        assertEquals(C_T, B);
        ZDD C_E = C.e();
        assertEquals(C_E, A);

        ZDD D = zddManager.parse("CHANGE true 1");
        ZDD D_T = D.t();
        assertEquals(D_T, B);
        ZDD D_E = D.e();
        assertEquals(D_E, A);

        ZDD E = zddManager.parse("(CHANGE true 0)UNION(CHANGE true 1)");
        ZDD E_T = E.t();
        assertEquals(E_T, B);
        ZDD E_E = E.e();
        ZDD E_ET = E_E.t();
        assertEquals(E_ET, B);
        ZDD E_EE = E_E.e();
        assertEquals(E_EE, A);

        ZDD F = zddManager.parse("true UNION ((CHANGE true 0) UNION (CHANGE true 1))");
        ZDD F_T = F.t();
        assertEquals(F_T, B);
        ZDD F_E = F.e();
        ZDD F_ET = F_E.t();
        assertEquals(F_ET, B);
        ZDD F_EE = F_E.e();
        assertEquals(F_EE, B);

        ZDD G = zddManager.parse("(true UNION ((CHANGE true 0) UNION (CHANGE true 1))) DIFF (CHANGE true 0)");
        ZDD G_T = G.t();
        assertEquals(G_T, B);
        ZDD G_E = G.e();
        assertEquals(G_E, B);

        A.recursiveDeref();
        B.recursiveDeref();
        C.recursiveDeref();
        D.recursiveDeref();
        E.recursiveDeref();
        F.recursiveDeref();
        G.recursiveDeref();

        assertEquals(zddManager.checkZeroRef(), 0);
        zddManager.quit();
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultZDDBackends")
    public void testZDDParserZDDOne(ZDDBackend zddBackend) {
        ZDDManager zddManager = new ZDDManager(zddBackend);
        ZDD A = zddManager.parse("false");
        ZDD B = zddManager.parse("true");
        ZDD D = zddManager.parse("CHANGE (zTrue 1) 1");
        ZDD D_T = D.t();
        assertEquals(D_T, B);
        ZDD D_E = D.e();
        assertEquals(D_E, A);
    }
}
