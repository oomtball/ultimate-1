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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Partition<T> {

    private final Set<Set<T>> blocks;

    public Partition() {
        blocks = new HashSet<>();
    }

    public Partition(T... block0) {
        blocks = new HashSet<>();
        blocks.add(new HashSet<>(Arrays.asList(block0)));
    }

    private Partition(Set<Set<T>> blocks) {
        this.blocks = blocks;
    }

    public static <T> Partition<T> emptyPartition() {
        return new Partition<>();
    }

    public int size() {
        return blocks.size();
    }

    public Set<Set<T>> blocks() {
        return new HashSet<>(blocks);
    }

    public Partition<T> meet(Partition<T> other) {
        Set<Set<T>> meetBlocks = new HashSet<>();
        LinkedList<@NonNull Set<T>> q0 = new LinkedList<>(blocks);
        LinkedList<Set<T>> q1 = new LinkedList<>(other.blocks);

        /* For every block in q0 we find the blocks in q1 to intersect them */
        while (!q0.isEmpty()) {
            Set<T> b0 = new HashSet<>(q0.remove());
            ListIterator<Set<T>> it = q1.listIterator();
            while (it.hasNext()) {
                Set<T> b1 = new HashSet<>(it.next());
                if (b0.equals(b1)) {
                    it.remove();
                } else if (intersectionNotEmpty(b0, b1)) {
                    Set<T> intersection = intersect(b0, b1);
                    b0.removeAll(intersection);
                    b1.removeAll(intersection);
                    it.remove();
                    if (!b1.isEmpty()) {
                        it.add(b1);
                    }
                    it.add(intersection);
                }
            }
            if (!b0.isEmpty()) {
                meetBlocks.add(b0);
            }
        }

        /* Remaining blocks in q1 share no elements with processed blocks */
        meetBlocks.addAll(q1);

        return new Partition<>(meetBlocks);
    }

    public static <T> Partition<T> parsePartition(String str, Function<String, T> parseElement) {

        /* Special case: empty partition */
        if ("[]".equals(str)) {
            return new Partition<>();
        }

        /* Strip outer brackets */
        String strCommaSeparatedBlocks = str.substring(2, str.length() - 2);

        /* Strip inner brackets */
        String[] strBlocks = strCommaSeparatedBlocks.split("], \\[");

        Set<Set<T>> blocks = new HashSet<>();
        for (String strCommaSeparatedElems : strBlocks) {
            String[] strElems = strCommaSeparatedElems.split(", ");
            Set<T> b = new HashSet<>();
            for (String strElem : strElems) {
                T elem = parseElement.apply(strElem);
                b.add(elem);
            }
            blocks.add(b);
        }
        return new Partition<>(blocks);
    }

    public Partition<T> join(Partition<T> other) {
        Set<Set<T>> joinBlocks = new HashSet<>();
        LinkedList<@NonNull Set<T>> q0 = new LinkedList<>(blocks);
        LinkedList<Set<T>> q1 = new LinkedList<>(other.blocks);

        /* For every block in q0 we find the blocks in q1 to union them */
        while (!q0.isEmpty()) {
            Set<T> b0 = new HashSet<>(q0.remove());
            ListIterator<Set<T>> it = q1.listIterator();
            while (it.hasNext()) {
                Set<T> b1 = it.next();
                if (b0.equals(b1)) {
                    it.remove();
                } else if (intersectionNotEmpty(b0, b1)) {
                    b0.addAll(b1);
                    it.remove();
                }
            }
            joinBlocks.add(b0);
        }

        /* Remaining blocks in q1 share no elements with processed blocks */
        joinBlocks.addAll(q1);

        return new Partition<>(joinBlocks);
    }

    private boolean intersectionNotEmpty(Set<T> a, Set<T> b) {
        for (T x : a) {
            if (b.contains(x)) {
                return true;
            }
        }
        return false;
    }

    private Set<T> intersect(Set<T> a, Set<T> b) {
        Set<T> s = new HashSet<>(a);
        s.retainAll(b);
        return s;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Partition<?> partition = (Partition<?>) o;
        return Objects.equals(blocks, partition.blocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blocks);
    }

    @Override
    public String toString() {
        return "[" + blocks.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
}
