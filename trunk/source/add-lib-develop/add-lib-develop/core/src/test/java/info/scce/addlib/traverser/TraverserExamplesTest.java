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
package info.scce.addlib.traverser;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

public class TraverserExamplesTest extends TraverserTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public TraverserExamplesTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testXorExample() {
        /* Assert non-constants */
        assertFalse(xor().isConstant());
        assertFalse(xor().t().isConstant());
        assertFalse(xor().e().isConstant());
        assertFalse(xor0().isConstant());
        assertFalse(xor1().isConstant());

        /* Assert constants */
        assertTrue(xor().e().e().isConstant());
        assertTrue(xor().e().t().isConstant());
        assertTrue(xor().t().e().isConstant());
        assertTrue(xor().t().t().isConstant());
        assertTrue(const0().isConstant());
        assertTrue(const1().isConstant());

        /* Assert variable names */
        assertEquals(xor().readName(), "x0");
        assertEquals(xor().t().readName(), "x1");
        assertEquals(xor().e().readName(), "x1");
        assertEquals(xor0().readName(), "x1");
        assertEquals(xor1().readName(), "x1");

        /* Assert constant values */
        double eps = 0;
        assertEquals(xor().e().e().v(), 0, eps);
        assertEquals(xor().e().t().v(), 1, eps);
        assertEquals(xor().t().e().v(), 1, eps);
        assertEquals(xor().t().t().v(), 0, eps);
        assertEquals(const0().v(), 0, eps);
        assertEquals(const1().v(), 1, eps);
    }

    @Test
    public void testOrExample() {
        /* Assert non-constants */
        assertFalse(or().isConstant());
        assertFalse(or().e().isConstant());
        assertFalse(or0().isConstant());

        /* Assert constants */
        assertTrue(or().e().e().isConstant());
        assertTrue(or().e().t().isConstant());
        assertTrue(or().t().isConstant());
        assertTrue(const0().isConstant());
        assertTrue(const1().isConstant());

        /* Assert variable names */
        assertEquals(or().readName(), "x0");
        assertEquals(or().e().readName(), "x1");
        assertEquals(or0().readName(), "x1");

        /* Assert constant values */
        double eps = 0;
        assertEquals(or().e().e().v(), 0, eps);
        assertEquals(or().e().t().v(), 1, eps);
        assertEquals(or().t().v(), 1, eps);
        assertEquals(const0().v(), 0, eps);
        assertEquals(const1().v(), 1, eps);
    }

    @Test
    public void testExamplesShareTerminals() {
        /* Assert shared 1 terminal */
        ADD orConst1 = or().t();
        ADD xorConst1 = xor().t().e();
        assertNotSame(xorConst1, orConst1);
        assertEquals(xorConst1, orConst1);
        assertEquals(const1(), xorConst1);

        /* Assert shared 0 terminal */
        ADD orConst0 = or().e().e();
        ADD xorConst0 = xor().t().t();
        assertNotSame(xorConst0, orConst0);
        assertEquals(xorConst0, orConst0);
        assertEquals(const0(), xorConst0);
    }
}
