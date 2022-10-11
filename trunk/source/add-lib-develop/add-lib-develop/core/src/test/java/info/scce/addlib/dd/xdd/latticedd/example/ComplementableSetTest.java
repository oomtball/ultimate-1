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

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class ComplementableSetTest {

    @Test
    public void testComplementableSet() {

        /* Some test sets */
        ComplementableSet<String> people = new ComplementableSet<>(new String[] {"Hans", "Peter", "Meier", "Alex"});
        ComplementableSet<String> morePeople =
                new ComplementableSet<>(new String[] {"Hans", "Bernd", "Alex", "Jan", "Hermann"});

        /* Assert union */
        ComplementableSet<String> actualUnion = people.union(morePeople);
        ComplementableSet<String> expectedUnion =
                new ComplementableSet<>(new String[] {"Hans", "Bernd", "Jan", "Peter", "Meier", "Alex", "Hermann"});
        assertEquals(actualUnion, expectedUnion);

        /* Assert intersect */
        ComplementableSet<String> actualIntersection = people.intersect(morePeople);
        ComplementableSet<String> expectedIntersection = new ComplementableSet<>(new String[] {"Hans", "Alex"});
        assertEquals(actualIntersection, expectedIntersection);

        /* Assert complement using De Morgan's law */
        ComplementableSet<String> actualUnionDeMorgan =
                people.complement().intersect(morePeople.complement()).complement();
        assertEquals(actualUnionDeMorgan, expectedUnion);
        ComplementableSet<String> actualIntersectionDeMorgan =
                people.complement().union(morePeople.complement()).complement();
        assertEquals(actualIntersectionDeMorgan, expectedIntersection);
    }

    private void testParseComplementableSet(ComplementableSet<String> complSet) {
        String str = complSet.toString();
        ComplementableSet<String> reproduced = ComplementableSet.parseComplementableSet(str, x -> x);
        assertEquals(reproduced, complSet);
    }

    @Test
    public void testParseComplementableSet() {
        String[] names = {"Hans", "Ulf", "Jan"};
        ComplementableSet<String> namesSet = new ComplementableSet<>(names);
        testParseComplementableSet(namesSet);
        testParseComplementableSet(namesSet.complement());
    }

    @Test
    public void testParseComplementableSetComplete() {
        testParseComplementableSet(ComplementableSet.completeSet());
    }

    @Test
    public void testParseComplementableSetEmpty() {
        testParseComplementableSet(ComplementableSet.emptySet());
    }

}
