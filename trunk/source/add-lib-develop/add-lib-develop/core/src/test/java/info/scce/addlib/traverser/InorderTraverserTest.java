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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class InorderTraverserTest extends TraverserTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public InorderTraverserTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testInorder() {
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
        List<ADD> roots = Arrays.asList(xor(), xor0());
        List<ADD> actualSequence = new ArrayList<>();
        for (ADD f : new InorderTraverser<>(roots)) {
            actualSequence.add(f);
        }
        List<ADD> expectedSequence = Arrays.asList(const0(), xor0(), const1(), xor(), xor1());
        assertEquals(actualSequence, expectedSequence);
    }

    @Test
    public void testInorder2() {
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
        List<ADD> roots = Arrays.asList(or(), or0());
        List<ADD> actualSequence = new ArrayList<>();
        for (ADD f : new InorderTraverser<>(roots)) {
            actualSequence.add(f);
        }
        List<ADD> expectedSequence = Arrays.asList(const0(), or0(), const1(), or());
        assertEquals(actualSequence, expectedSequence);
    }
}
