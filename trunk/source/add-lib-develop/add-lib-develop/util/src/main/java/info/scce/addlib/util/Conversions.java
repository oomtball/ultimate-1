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

package info.scce.addlib.util;

public final class Conversions {

    public static final long NULL = 0;

    private Conversions() {
    }

    public static int[] asInts(boolean... booleans) {
        int[] ints = new int[booleans.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = asInt(booleans[i]);
        }
        return ints;
    }

    public static int asInt(boolean b) {
        return b ? 1 : 0;
    }

    public static double[] asDoubles(boolean... booleans) {
        double[] doubles = new double[booleans.length];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = asDouble(booleans[i]);
        }
        return doubles;
    }

    public static double asDouble(boolean b) {
        return b ? 1.0 : 0.0;
    }

    public static boolean[] asBooleans(int... ints) {
        boolean[] booleans = new boolean[ints.length];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = asBoolean(ints[i]);
        }
        return booleans;
    }

    public static boolean[] asBooleans(double... doubles) {
        boolean[] booleans = new boolean[doubles.length];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = asBoolean(doubles[i]);
        }
        return booleans;
    }

    public static boolean asBoolean(int i) {
        return i > 0;
    }

    public static boolean asBoolean(double d) {
        return d > 0;
    }
}