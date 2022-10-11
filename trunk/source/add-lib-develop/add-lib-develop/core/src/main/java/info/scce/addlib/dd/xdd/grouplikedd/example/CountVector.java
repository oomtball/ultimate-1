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

public class CountVector {

    private final int[] c;

    public CountVector(int... c) {
        this.c = c;
    }

    public static CountVector zero(int n) {
        int[] c = new int[n];
        return new CountVector(c);
    }

    public static CountVector parseCountVector(String str) {

        /* Strip brackets */
        String strCommaSeparatedInts = str.substring(1, str.length() - 1);

        int[] c;
        if (strCommaSeparatedInts.isEmpty()) {
            c = new int[0];
        } else {
            String[] strInts = strCommaSeparatedInts.split(", ");
            int n = strInts.length;
            c = new int[n];
            for (int i = 0; i < c.length; i++) {
                c[i] = Integer.parseInt(strInts[i]);
            }
        }
        return new CountVector(c);
    }

    public int n() {
        return c.length;
    }

    public int[] data() {
        return c.clone();
    }

    public CountVector add(CountVector other) {
        int[] sum = new int[n()];
        for (int i = 0; i < sum.length; i++) {
            sum[i] = c[i] + other.c[i];
        }
        return new CountVector(sum);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CountVector count = (CountVector) o;
        return Arrays.equals(c, count.c);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(c);
    }

    @Override
    public String toString() {
        return Arrays.toString(c);
    }
}
