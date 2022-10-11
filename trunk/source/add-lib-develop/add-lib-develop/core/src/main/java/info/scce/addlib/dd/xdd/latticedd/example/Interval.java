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

import java.util.Objects;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Interval {

    private double lb;
    private double ub;
    private boolean lbIncluded;
    private boolean ubIncluded;

    public Interval(double lb, boolean lbIncluded, double ub, boolean ubIncluded) {
        this.lb = lb;
        this.ub = ub;
        this.lbIncluded = lbIncluded;
        this.ubIncluded = ubIncluded;

        /* Check misuse of positive or negative infinity */
        if (lb == Double.NEGATIVE_INFINITY && this.lbIncluded) {
            throw new IllegalArgumentException("Negative infinity is not a real number");
        }
        if (ub == Double.POSITIVE_INFINITY && this.ubIncluded) {
            throw new IllegalArgumentException("Positive infinity is not a real number");
        }

        /* Ensure canonical representation of empty intervals as (0, 0) */
        if (isEmpty()) {
            this.lb = 0;
            this.ub = 0;
            this.lbIncluded = false;
            this.ubIncluded = false;
        }
    }

    public static Interval empty() {
        return new Interval(0, false, 0, false);
    }

    public static Interval complete() {
        return new Interval(Double.NEGATIVE_INFINITY, false, Double.POSITIVE_INFINITY, false);
    }

    public static Interval parseInterval(String str) {

        /* Parse lbIncluded */
        boolean lbIncluded;
        if (str.charAt(0) == '[') {
            lbIncluded = true;
        } else if (str.charAt(0) == '(') {
            lbIncluded = false;
        } else {
            throw new IllegalArgumentException("Malformed interval: " + str);
        }

        /* Parse ubIncluded */
        boolean ubIncluded;
        if (str.charAt(str.length() - 1) == ']') {
            ubIncluded = true;
        } else if (str.charAt(str.length() - 1) == ')') {
            ubIncluded = false;
        } else {
            throw new IllegalArgumentException("Malformed interval: " + str);
        }

        /* Parse bounds */
        String[] parts = str.substring(1, str.length() - 1).split(", *");
        double lb = Double.parseDouble(parts[0]);
        double ub = Double.parseDouble(parts[1]);

        return new Interval(lb, lbIncluded, ub, ubIncluded);
    }

    public boolean contains(Interval other) {
        return lbSmallerEqThan(other) && ubGreaterEqThan(other)
                || other.equals(empty());
    }

    public Interval union(Interval other) {

        /* Treat empty intervals special */
        if (empty().equals(this)) {
            return other;
        } else if (empty().equals(other)) {
            return this;
        }

        /* Find lower bound */
        double lb;
        boolean lbIncluded;
        if (lbSmallerEqThan(other)) {
            lb = this.lb;
            lbIncluded = this.lbIncluded;
        } else {
            lb = other.lb;
            lbIncluded = other.lbIncluded;
        }

        /* Find upper bound */
        double ub;
        boolean ubIncluded;
        if (ubGreaterEqThan(other)) {
            ub = this.ub;
            ubIncluded = this.ubIncluded;
        } else {
            ub = other.ub;
            ubIncluded = other.ubIncluded;
        }

        return new Interval(lb, lbIncluded, ub, ubIncluded);
    }

    public Interval intersect(Interval other) {

        /* Treat empty intervals special */
        if (empty().equals(this)) {
            return this;
        } else if (empty().equals(other)) {
            return other;
        }

        /* Find lower bound */
        double lb;
        boolean lbIncluded;
        if (lbSmallerEqThan(other)) {
            lb = other.lb;
            lbIncluded = other.lbIncluded;
        } else {
            lb = this.lb;
            lbIncluded = this.lbIncluded;
        }

        /* Find upper bound */
        double ub;
        boolean ubIncluded;
        if (ubGreaterEqThan(other)) {
            ub = other.ub;
            ubIncluded = other.ubIncluded;
        } else {
            ub = this.ub;
            ubIncluded = this.ubIncluded;
        }

        return new Interval(lb, lbIncluded, ub, ubIncluded);
    }

    private boolean ubGreaterEqThan(Interval other) {
        if (ubIncluded || !other.ubIncluded) {
            return ub >= other.ub;
        } else {
            return ub > other.ub;
        }
    }

    private boolean lbSmallerEqThan(Interval other) {
        if (lbIncluded || !other.lbIncluded) {
            return lb <= other.lb;
        } else {
            return lb < other.lb;
        }
    }

    private boolean isEmpty(@UnderInitialization Interval this) {
        return lb > ub ||
                lb == ub && !(lbIncluded && ubIncluded);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Interval interval = (Interval) o;
        return Double.compare(interval.lb, lb) == 0 &&
                Double.compare(interval.ub, ub) == 0 &&
                lbIncluded == interval.lbIncluded &&
                ubIncluded == interval.ubIncluded;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lb, ub, lbIncluded, ubIncluded);
    }

    @Override
    public String toString() {
        return (lbIncluded ? "[" : "(") + lb + ", " + ub + (ubIncluded ? "]" : ")");
    }
}
