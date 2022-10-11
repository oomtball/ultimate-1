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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class PartitionTest {

    private final Partition<String> people = new Partition<>("Tiziana", "Bernhard", "Alnis", "Frederik");
    private final Partition<String> profs = new Partition<>("Tiziana", "Bernhard");
    private final Partition<String> limerick = new Partition<>("Tiziana", "Frederik");

    @Test
    public void testMeet() {
        HashSet<HashSet<String>> expected_profs_meet_limerick = new HashSet<>();
        expected_profs_meet_limerick.add(new HashSet<>(Collections.singletonList("Frederik")));
        expected_profs_meet_limerick.add(new HashSet<>(Collections.singletonList("Tiziana")));
        expected_profs_meet_limerick.add(new HashSet<>(Collections.singletonList("Bernhard")));
        assertEquals(profs.meet(limerick).blocks(), expected_profs_meet_limerick);
        assertEquals(limerick.meet(profs).blocks(), expected_profs_meet_limerick);
    }

    @Test
    public void testJoin() {
        HashSet<HashSet<String>> expected_profs_join_limerick = new HashSet<>();
        expected_profs_join_limerick.add(new HashSet<>(Arrays.asList("Frederik", "Tiziana", "Bernhard")));
        assertEquals(profs.join(limerick).blocks(), expected_profs_join_limerick);
        assertEquals(limerick.join(profs).blocks(), expected_profs_join_limerick);
    }

    @Test
    public void testJoin2() {
        Partition<String> expected_profs_join_people = people;
        assertEquals(profs.join(people), expected_profs_join_people);
        assertEquals(people.join(profs), expected_profs_join_people);
    }

    @Test
    public void testBlocks() {
        Set<Set<String>> expected_profs_meet_people_blocks = new HashSet<>();
        expected_profs_meet_people_blocks.add(new HashSet<>(Arrays.asList("Frederik", "Alnis")));
        expected_profs_meet_people_blocks.add(new HashSet<>(Arrays.asList("Tiziana", "Bernhard")));
        assertEquals(profs.meet(people).blocks(), expected_profs_meet_people_blocks);
        assertEquals(people.meet(profs).blocks(), expected_profs_meet_people_blocks);
    }

    @Test
    public void testParsePartitionEmpty() {
        testParsePartition(Partition.emptyPartition());
    }

    public void testParsePartition(Partition<String> original) {
        String str = original.toString();
        Partition<String> reconstructed = Partition.parsePartition(str, x -> x);
        assertEquals(reconstructed, original);
    }

    @Test
    public void testParsePartitionSingleBlock() {
        testParsePartition(profs);
        testParsePartition(people);
        testParsePartition(limerick);
    }

    @Test
    public void testParsePartitionMultipleBlocks() {
        testParsePartition(people.meet(limerick).meet(profs));
        testParsePartition(people.meet(limerick));
        testParsePartition(limerick.meet(profs));
    }
}
