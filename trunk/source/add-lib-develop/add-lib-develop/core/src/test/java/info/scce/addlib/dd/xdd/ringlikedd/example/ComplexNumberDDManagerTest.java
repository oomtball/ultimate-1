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
package info.scce.addlib.dd.xdd.ringlikedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ComplexNumberDDManagerTest extends DDManagerTest {

    private static final double EPS = 0.00000002;
    private final ADDBackend addBackend;
    private ComplexNumberDDManager ddManager;
    private XDD<ComplexNumber> constA;
    private XDD<ComplexNumber> constB;

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public ComplexNumberDDManagerTest(ADDBackend addBackend) {
        this.addBackend = addBackend;
    }

    @BeforeClass
    public void setUp() {
        ddManager = new ComplexNumberDDManager(addBackend);
        constA = ddManager.constant(new ComplexNumber(5, 10));
        constB = ddManager.constant(new ComplexNumber(2, -5.3));
    }

    @Test
    public void testMultInverse() {
        XDD<ComplexNumber> aInv = constA.multInverse();
        XDD<ComplexNumber> bInv = constB.multInverse();

        XDD<ComplexNumber> aMultAInv = constA.mult(aInv);
        aInv.recursiveDeref();

        XDD<ComplexNumber> bMultBInv = constB.mult(bInv);
        bInv.recursiveDeref();

        XDD<ComplexNumber> constOne = ddManager.one();
        assertTrue(bMultBInv.v().equals(aMultAInv.v(), EPS));
        assertTrue(constOne.v().equals(bMultBInv.v(), EPS));
        assertTrue(constOne.v().equals(aMultAInv.v(), EPS));

        aMultAInv.recursiveDeref();
        bMultBInv.recursiveDeref();
        constOne.recursiveDeref();
    }

    @Test
    public void testAddInverse() {
        XDD<ComplexNumber> aInv = constA.addInverse();
        XDD<ComplexNumber> bInv = constB.addInverse();

        XDD<ComplexNumber> aAddAInv = constA.add(aInv);
        aInv.recursiveDeref();

        XDD<ComplexNumber> bAddBInv = constB.add(bInv);
        bInv.recursiveDeref();

        XDD<ComplexNumber> constZero = ddManager.zero();
        assertTrue(bAddBInv.v().equals(aAddAInv.v(), EPS));
        assertTrue(constZero.v().equals(bAddBInv.v(), EPS));
        assertTrue(constZero.v().equals(aAddAInv.v(), EPS));

        constZero.recursiveDeref();
        aAddAInv.recursiveDeref();
        bAddBInv.recursiveDeref();
    }

    @Test
    public void testMisc() {
        assertEquals(ddManager.oneElement().abs(), 1, 0.0);
        assertEquals(ddManager.zeroElement().abs(), 0, 0.0);
        ComplexNumber aVal = constA.v();

        double num = aVal.abs() * Math.cos(aVal.theta());
        double denom = aVal.abs() * Math.sin(aVal.theta());
        ComplexNumber target = new ComplexNumber(num, denom);
        assertTrue(aVal.equals(target, EPS));
    }

    @Test
    public void testMultAdd() {
        XDD<ComplexNumber> constC = ddManager.constant(new ComplexNumber(7, 4.7));
        XDD<ComplexNumber> aAddB = constA.add(constB);
        assertEquals(constC.v(), aAddB.v());
        constC.recursiveDeref();

        XDD<ComplexNumber> aInv = constA.multInverse();
        XDD<ComplexNumber> aMultB = constA.mult(constB);

        XDD<ComplexNumber> aMultBMultAInv = aMultB.mult(aInv);
        aMultB.recursiveDeref();
        aInv.recursiveDeref();
        assertTrue(aMultBMultAInv.v().equals(constB.v(), EPS));

        aAddB.recursiveDeref();
        aMultBMultAInv.recursiveDeref();
    }

    @AfterClass
    public void tearDown() {
        constA.recursiveDeref();
        constB.recursiveDeref();
        assertRefCountZeroAndQuit(ddManager);
    }
}
