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
package info.scce.addlib.dd.xdd.grouplikedd.example;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.DDManagerTest;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.utils.BackendProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class StringMonoidDDManagerTest extends DDManagerTest {

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testHelloWorldConstant(ADDBackend addBackend) {
        StringMonoidDDManager ddManager = new StringMonoidDDManager(addBackend);

        /* Assert neutral element */
        XDD<String> hello = ddManager.constant("Hello");
        XDD<String> e = ddManager.neutral();
        XDD<String> hello_join_e = hello.join(e);
        XDD<String> e_join_hello = e.join(hello);
        assertEquals(hello_join_e, hello);
        assertEquals(e_join_hello, hello);
        assertEquals(hello_join_e.v(), hello.v());
        assertEquals(e_join_hello.v(), hello.v());
        e.recursiveDeref();
        hello_join_e.recursiveDeref();
        e_join_hello.recursiveDeref();

        /* Assert concatenation */
        XDD<String> expected_hello_join_world = ddManager.constant("Hello World!");
        XDD<String> world = ddManager.constant(" World!");
        XDD<String> actual_hello_join_world = hello.join(world);
        assertEquals(actual_hello_join_world, expected_hello_join_world);
        assertEquals(actual_hello_join_world.v(), expected_hello_join_world.v());
        assertEquals(actual_hello_join_world.v(), "Hello World!");
        hello.recursiveDeref();
        expected_hello_join_world.recursiveDeref();
        world.recursiveDeref();
        actual_hello_join_world.recursiveDeref();

        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testHelloWorldTree(ADDBackend addBackend) {
        StringMonoidDDManager ddManager = new StringMonoidDDManager(addBackend);

        /* Build the decision diagram */
        XDD<String> hello = ddManager.namedVar("sayHello", "Hello ", "***** ");
        XDD<String> world = ddManager.namedVar("sayWorld", "World!", "*****!");
        XDD<String> hello_join_world = hello.join(world);
        hello.recursiveDeref();
        world.recursiveDeref();

        /* Assert decision diagram */
        XDD<String> expected_hello_join_world_then_then = ddManager.constant("Hello World!");
        XDD<String> expected_hello_join_world_then_else = ddManager.constant("Hello *****!");
        XDD<String> expected_hello_join_world_else_then = ddManager.constant("***** World!");
        XDD<String> expected_hello_join_world_else_else = ddManager.constant("***** *****!");
        XDD<String> actual_hello_join_world_then_then = hello_join_world.t().t();
        XDD<String> actual_hello_join_world_then_else = hello_join_world.t().e();
        XDD<String> actual_hello_join_world_else_then = hello_join_world.e().t();
        XDD<String> actual_hello_join_world_else_else = hello_join_world.e().e();
        assertEquals(actual_hello_join_world_then_then, expected_hello_join_world_then_then);
        assertEquals(actual_hello_join_world_then_then.v(), expected_hello_join_world_then_then.v());
        assertEquals(actual_hello_join_world_then_else, expected_hello_join_world_then_else);
        assertEquals(actual_hello_join_world_then_else.v(), expected_hello_join_world_then_else.v());
        assertEquals(actual_hello_join_world_else_then, expected_hello_join_world_else_then);
        assertEquals(actual_hello_join_world_else_then.v(), expected_hello_join_world_else_then.v());
        assertEquals(actual_hello_join_world_else_else, expected_hello_join_world_else_else);
        assertEquals(actual_hello_join_world_else_else.v(), expected_hello_join_world_else_else.v());
        expected_hello_join_world_then_then.recursiveDeref();
        expected_hello_join_world_then_else.recursiveDeref();
        expected_hello_join_world_else_then.recursiveDeref();
        expected_hello_join_world_else_else.recursiveDeref();
        hello_join_world.recursiveDeref();

        assertRefCountZeroAndQuit(ddManager);
    }

    @Test(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public void testParseElement(ADDBackend addBackend) {
        StringMonoidDDManager ddManager = new StringMonoidDDManager(addBackend);

        /* Assert parseElement is inverse of toString */
        XDD<String> helloWorld = ddManager.constant("Hello World!");
        String str = helloWorld.toString();
        XDD<String> helloWorldReproduced = ddManager.constant(ddManager.parseElement(str));
        assertEquals(helloWorldReproduced, helloWorld);

        /* Release memory */
        helloWorld.recursiveDeref();
        helloWorldReproduced.recursiveDeref();

        assertRefCountZeroAndQuit(ddManager);
    }
}
