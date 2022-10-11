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
package info.scce.addlib.dd.xdd.ringlikedd.example;

import java.util.Objects;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SquareMatrix {

    private final RealMatrix m;

    public SquareMatrix(double[][] data) {
        this(MatrixUtils.createRealMatrix(data));
    }

    private SquareMatrix(RealMatrix m) {
        this.m = m;
    }

    public static SquareMatrix zero(int n) {
        return new SquareMatrix(MatrixUtils.createRealMatrix(n, n));
    }

    public static SquareMatrix identity(int n) {
        return new SquareMatrix(MatrixUtils.createRealIdentityMatrix(n));
    }

    public static SquareMatrix parseSquareMatrix(String str) {

        /* Strip outer brackets */
        String strCommaSeparatedRows = str.substring(2, str.length() - 2);

        /* Strip inner brackets */
        String[] strRows = strCommaSeparatedRows.split("},\\{");

        double[][] data = new double[strRows.length][];
        for (int i = 0; i < strRows.length; i++) {
            String strCommaSeparatedValues = strRows[i];
            String[] strValues = strCommaSeparatedValues.split(",");
            double[] row = new double[strValues.length];
            for (int j = 0; j < strValues.length; j++) {
                double value = Double.parseDouble(strValues[j]);
                row[j] = value;
            }
            data[i] = row;
        }
        return new SquareMatrix(data);
    }

    public int size() {
        return m.getColumnDimension();
    }

    public double[][] data() {
        return m.getData();
    }

    public SquareMatrix mult(SquareMatrix other) {
        return new SquareMatrix(m.multiply(other.m));
    }

    public SquareMatrix multInverse() {
        return new SquareMatrix(new LUDecomposition(m).getSolver().getInverse());
    }

    public SquareMatrix add(SquareMatrix other) {
        return new SquareMatrix(m.add(other.m));
    }

    public SquareMatrix addInverse() {
        return new SquareMatrix(m.scalarMultiply(-1));
    }

    @Override
    public boolean equals(@Nullable Object o) { //needs to accept epsilon differences
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SquareMatrix that = (SquareMatrix) o;
        return m.equals(that.m);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m);
    }

    @Override
    public String toString() {
        return m.toString().replace("Array2DRowRealMatrix", "");
    }
}
