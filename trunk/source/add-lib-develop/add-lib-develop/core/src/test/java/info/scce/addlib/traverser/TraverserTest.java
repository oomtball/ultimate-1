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
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;

public class TraverserTest extends DDManagerTest {

    private final ADDBackend addBackend;
    private ADDManager ddManager;
    private ADD xor;
    private ADD or;
    private ADD const0;
    private ADD const1;

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public TraverserTest(ADDBackend addBackend) {
        this.addBackend = addBackend;
    }

    @BeforeClass
    public void setUp() {
        ddManager = new ADDManager(addBackend);
    }

    @AfterClass
    public void tearDown() {
        if (xor != null) {
            xor.recursiveDeref();
        }
        if (or != null) {
            or.recursiveDeref();
        }
        if (const0 != null) {
            const0.recursiveDeref();
        }
        if (const1 != null) {
            const1.recursiveDeref();
        }
        assertRefCountZeroAndQuit(ddManager);
    }

    protected ADD const0() {
        if (const0 == null) {
            const0 = ddManager.constant(0);
        }
        return const0;
    }

    protected ADD const1() {
        if (const1 == null) {
            const1 = ddManager.readOne();
        }
        return const1;
    }

    protected ADD xor0() {
        return xor().e();
    }

    protected ADD xor() {
        if (xor == null) {

            /*
             *       xor
             *          \
             *           x0
             *          /  \
             *         :    \
             *   xor0  |    |  xor1
             *       \ :    | /
             *        x1    x1
             *        | \  / |
             *        :  \/  :
             * const0 |  /\  | const1
             *       \: /  \ :/
             *        c0    c1
             */
            ADD x0 = ddManager.ithVar(0);
            ADD x1 = ddManager.ithVar(1);
            xor = x0.xor(x1);
            x0.recursiveDeref();
            x1.recursiveDeref();
        }
        return xor;
    }

    protected ADD xor1() {
        return xor().t();
    }

    protected ADD or0() {
        return or().e();
    }

    protected ADD or() {
        if (or == null) {

            /*
             *        or
             *          \
             *           x0
             *          /  \
             *         :    \
             *    or0  |     \
             *       \ :     |
             *        x1     |
             *        | \    |
             *        :  \   |
             * const0 |   \  | const1
             *       \:    \ |/
             *        c0    c1
             */
            ADD x0 = ddManager.ithVar(0);
            ADD x1 = ddManager.ithVar(1);
            or = x0.or(x1);
            x0.recursiveDeref();
            x1.recursiveDeref();
        }
        return or;
    }
}
