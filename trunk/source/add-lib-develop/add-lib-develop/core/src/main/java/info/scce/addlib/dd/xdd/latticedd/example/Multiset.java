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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Multiset<E> {

    private final Map<E, Integer> multiplicity;

    public Multiset(E... elements) {
        multiplicity = new HashMap<>();
        for (E e : elements) {
            Integer value = multiplicity.get(e);
            if (value != null) {
                multiplicity.put(e, value + 1);
            } else {
                multiplicity.put(e, 1);
            }
        }
    }

    private Multiset(Map<E, Integer> multiplicity) {
        this.multiplicity = multiplicity;
    }

    public static <E> Multiset<E> emptySet() {
        return new Multiset<>(new HashMap<>());
    }

    public static <E> Multiset<E> parseMultiset(String str, Function<String, E> parseElement) {

        /* Strip brackets */
        String commaSeparatedMults = str.substring(1, str.length() - 1);

        String[] strMults = commaSeparatedMults.split(", ");
        HashMap<E, Integer> multiplicity = new HashMap<>();
        for (String strMult : strMults) {
            if (!strMult.isEmpty()) {
                String[] strMultPair = strMult.split(": ");
                E parsed = parseElement.apply(strMultPair[0]);
                int n = Integer.parseInt(strMultPair[1]);
                multiplicity.put(parsed, n);
            }
        }
        return new Multiset<>(multiplicity);
    }

    public Map<E, Integer> multiplicity() {
        return new HashMap<>(multiplicity);
    }

    public Multiset<E> intersect(Multiset<E> other) {
        HashMap<E, Integer> intersection = new HashMap<>();
        for (Map.Entry<E, Integer> entry : multiplicity.entrySet()) {
            Integer otherValue = other.multiplicity.get(entry.getKey());
            if (otherValue != null) {
                intersection.put(entry.getKey(), Math.min(entry.getValue(), otherValue));
            }
        }
        return new Multiset<>(intersection);
    }

    public Multiset<E> union(Multiset<E> other) {
        HashMap<E, Integer> union = new HashMap<>();
        for (Map.Entry<E, Integer> entry : multiplicity.entrySet()) {
            Integer otherValue = other.multiplicity.get(entry.getKey());
            if (otherValue != null) {
                union.put(entry.getKey(), Math.max(entry.getValue(), otherValue));
            } else {
                union.put(entry.getKey(), entry.getValue());
            }
        }
        for (E e : other.multiplicity.keySet()) {
            if (!multiplicity.containsKey(e)) {
                union.put(e, other.multiplicity.get(e));
            }
        }
        return new Multiset<>(union);
    }

    public boolean includes(Multiset<E> other) {
        for (Map.Entry<E, Integer> entry : multiplicity.entrySet()) {
            Integer otherValue = other.multiplicity.get(entry.getKey());
            if (otherValue != null && otherValue > entry.getValue()) {
                return false;
            }
        }
        for (E e : other.multiplicity.keySet()) {
            if (!multiplicity.containsKey(e)) {
                return false;
            }
        }
        return true;
    }

    public Multiset<E> plus(Multiset<E> other) {
        HashMap<E, Integer> sum = new HashMap<>();
        for (Map.Entry<E, Integer> entry : multiplicity.entrySet()) {
            Integer otherValue = other.multiplicity.get(entry.getKey());
            if (otherValue != null) {
                sum.put(entry.getKey(), entry.getValue() + otherValue);
            } else {
                sum.put(entry.getKey(), entry.getValue());
            }
        }
        for (E e : other.multiplicity.keySet()) {
            if (!multiplicity.containsKey(e)) {
                sum.put(e, other.multiplicity.get(e));
            }
        }
        return new Multiset<>(sum);
    }

    public int size() {
        int size = 0;
        for (int n : multiplicity.values()) {
            size += n;
        }
        return size;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Multiset<?> multiset = (Multiset<?>) o;
        return Objects.equals(multiplicity, multiset.multiplicity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multiplicity);
    }

    @Override
    public String toString() {
        String strCommaSeparated = multiplicity.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(", "));
        return "[" + strCommaSeparated + "]";
    }
}
