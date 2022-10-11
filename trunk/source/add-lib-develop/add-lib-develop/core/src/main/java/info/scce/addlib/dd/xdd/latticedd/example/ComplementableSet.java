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
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ComplementableSet<E> {

    private final boolean compl;
    private final Set<E> explRepr;

    public ComplementableSet(E[] elements) {
        compl = false;
        explRepr = new HashSet<>();
        explRepr.addAll(Arrays.asList(elements));
    }

    private ComplementableSet(Set<E> explRepr, boolean compl) {
        this.compl = compl;
        this.explRepr = explRepr;
    }

    public static <T> ComplementableSet<T> emptySet() {
        return new ComplementableSet<>(new HashSet<>(), false);
    }

    public static <T> ComplementableSet<T> completeSet() {
        return new ComplementableSet<>(new HashSet<>(), true);
    }

    public static <T> ComplementableSet<T> parseComplementableSet(String str, Function<String, T> parseElement) {
        boolean compl = str.endsWith("^C");

        /* Strip brackets and complement */
        int trimEnd = compl ? 3 : 1;
        String strCommaSeparatedExplReprs = str.substring(1, str.length() - trimEnd);

        String[] strExplReprs = strCommaSeparatedExplReprs.split(", ");
        Set<T> explRepr = new HashSet<>();
        for (String strExplRepr : strExplReprs) {
            if (!strExplRepr.isEmpty()) {
                T parsed = parseElement.apply(strExplRepr);
                explRepr.add(parsed);
            }
        }
        return new ComplementableSet<>(explRepr, compl);
    }

    public boolean contains(E x) {
        boolean explReprContains = explRepr.contains(x);
        return compl != explReprContains;
    }

    public Set<E> explRepr() {
        return new HashSet<>(explRepr);
    }

    public boolean compl() {
        return compl;
    }

    public ComplementableSet<E> intersect(ComplementableSet<E> other) {
        if (compl) {
            if (other.compl) {
                return new ComplementableSet<>(explUnion(explRepr, other.explRepr), true);
            } else {
                return new ComplementableSet<>(explDiff(other.explRepr, explRepr), false);
            }
        } else {
            if (other.compl) {
                return new ComplementableSet<>(explDiff(explRepr, other.explRepr), false);
            } else {
                return new ComplementableSet<>(explIntersect(explRepr, other.explRepr), false);
            }
        }
    }

    public ComplementableSet<E> union(ComplementableSet<E> other) {
        if (compl) {
            if (other.compl) {
                return new ComplementableSet<>(explIntersect(explRepr, other.explRepr), true);
            } else {
                return new ComplementableSet<>(explDiff(explRepr, other.explRepr), true);
            }
        } else {
            if (other.compl) {
                return new ComplementableSet<>(explDiff(other.explRepr, explRepr), true);
            } else {
                return new ComplementableSet<>(explUnion(explRepr, other.explRepr), false);
            }
        }
    }

    private Set<E> explIntersect(Set<E> a, Set<E> b) {
        HashSet<E> s = new HashSet<>(a);
        s.retainAll(b);
        return s;
    }

    private Set<E> explUnion(Set<E> a, Set<E> b) {
        HashSet<E> s = new HashSet<>();
        s.addAll(a);
        s.addAll(b);
        return s;
    }

    private Set<E> explDiff(Set<E> a, Set<E> b) {
        HashSet<E> s = new HashSet<>(a);
        s.removeAll(b);
        return s;
    }

    public ComplementableSet<E> complement() {
        return new ComplementableSet<>(explRepr, !compl);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComplementableSet<?> that = (ComplementableSet<?>) o;
        return compl == that.compl && Objects.equals(explRepr, that.explRepr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compl, explRepr);
    }

    @Override
    public String toString() {
        String strExplRepr = explRepr.toString();
        String strExplReprStripped = strExplRepr.substring(1, strExplRepr.length() - 1);
        return "[" + strExplReprStripped + "]" + (compl ? "^C" : "");
    }
}
