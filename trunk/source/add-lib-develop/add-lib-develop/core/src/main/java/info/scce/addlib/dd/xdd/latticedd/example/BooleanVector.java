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

import org.checkerframework.checker.nullness.qual.Nullable;

public class BooleanVector {

    private final boolean[] b;

    public BooleanVector(boolean... b) {
        this.b = b;
    }

    public static BooleanVector zero(int n) {
        boolean[] b = new boolean[n];
        return new BooleanVector(b);
    }

    public static BooleanVector one(int n) {
        boolean[] b = new boolean[n];
        Arrays.fill(b, true);
        return new BooleanVector(b);
    }

    public static BooleanVector parseBooleanVector(String str) {

        /* Strip brackets */
        String strCommaSeparatedBools = str.substring(1, str.length() - 1);

        boolean[] b;
        if (strCommaSeparatedBools.isEmpty()) {
            b = new boolean[0];
        } else {
            String[] strBools = strCommaSeparatedBools.split(", ");
            b = new boolean[strBools.length];
            for (int i = 0; i < b.length; i++) {
                b[i] = Boolean.parseBoolean(strBools[i]);
            }
        }
        return new BooleanVector(b);
    }

    public int n() {
        return b.length;
    }

    public boolean[] data() {
        return b.clone();
    }

    public BooleanVector not() {
        boolean[] negation = new boolean[b.length];
        for (int i = 0; i < b.length; i++) {
            negation[i] = !b[i];
        }
        return new BooleanVector(negation);
    }

    public BooleanVector and(BooleanVector other) {
        boolean[] conjunction = new boolean[b.length];
        for (int i = 0; i < b.length; i++) {
            conjunction[i] = b[i] && other.b[i];
        }
        return new BooleanVector(conjunction);
    }

    public BooleanVector or(BooleanVector other) {
        boolean[] disjunction = new boolean[b.length];
        for (int i = 0; i < b.length; i++) {
            disjunction[i] = b[i] || other.b[i];
        }
        return new BooleanVector(disjunction);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BooleanVector bnLogic = (BooleanVector) o;
        return Arrays.equals(b, bnLogic.b);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(b);
    }

    @Override
    public String toString() {
        return Arrays.toString(b);
    }
}
