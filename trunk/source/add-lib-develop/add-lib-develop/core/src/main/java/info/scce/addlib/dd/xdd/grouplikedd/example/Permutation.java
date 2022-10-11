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

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Permutation {

    private final int[] p;

    public Permutation(int... p) {
        this.p = p;
    }

    public static Permutation identity(int n) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = i;
        }
        return new Permutation(p);
    }

    public static Permutation parsePermutation(String str) {
        int[] p;
        if (str.isEmpty()) {
            p = new int[0];
        } else {
            String[] strMaps = str.split(", ");
            p = new int[strMaps.length];
            for (int i = 0; i < strMaps.length; i++) {
                String strTo = strMaps[i].split(" -> ")[1];
                p[i] = Integer.parseInt(strTo);
            }
        }
        return new Permutation(p);
    }

    public int size() {
        return p.length;
    }

    public int[] data() {
        return p.clone();
    }

    public Permutation compose(Permutation g) {
        int[] pComposed = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            pComposed[i] = p[g.p[i]];
        }
        return new Permutation(pComposed);
    }

    public Permutation inverse() {
        int[] pInverse = new int[p.length];
        for (int i = 0; i < p.length; i++) {
            pInverse[p[i]] = i;
        }
        return new Permutation(pInverse);
    }

    @Override
    public boolean equals(@Nullable Object otherObj) {
        if (otherObj instanceof Permutation) {
            Permutation other = (Permutation) otherObj;
            return Arrays.equals(p, other.p);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(p);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        if (i < p.length) {
            sb.append(i + " -> " + p[i++]);
        }
        while (i < p.length) {
            sb.append(", " + i + " -> " + p[i++]);
        }
        return sb.toString();
    }
}
