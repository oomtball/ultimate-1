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
package info.scce.addlib.layouter;

import org.checkerframework.checker.nullness.qual.Nullable;

public class BoundingBox {

    private final double x, y, w, h;

    public BoundingBox(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double x2() {
        return x() + w();
    }

    public double y2() {
        return y() + h();
    }

    public double w() {
        return w;
    }

    public double h() {
        return h;
    }

    public double area() {
        return w * h;
    }

    public double overlap(BoundingBox other) {
        double overlapW = Math.max(0, Math.min(x2(), other.x2()) - Math.max(x, other.x));
        double overlapH = Math.max(0, Math.min(y2(), other.y2()) - Math.max(y, other.y));
        return overlapW * overlapH;
    }

    public boolean overlaps(BoundingBox other) {
        return overlap(other) > 0;
    }

    public boolean contains(BoundingBox other) {
        return x <= other.x && y <= other.y && other.x2() <= x2() && other.y2() <= y2();
    }

    @Override
    public boolean equals(@Nullable Object otherObj) {
        if (otherObj instanceof BoundingBox) {
            BoundingBox other = (BoundingBox) otherObj;
            return x == other.x && y == other.y && w == other.w && h == other.h;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) + Double.hashCode(y) + Double.hashCode(w) + Double.hashCode(h);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + w + ", " + h + ")";
    }
}
