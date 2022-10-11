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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import info.scce.addlib.dd.RegularDD;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("PMD.RedundantFieldInitializer")
public abstract class Layouter<D extends RegularDD<?, D>> {

    /* Subject DDs */
    private List<D> roots;

    /* Computed layout */
    private @Nullable Map<D, BoundingBox> layout;
    private @Nullable BoundingBox bbox;

    /* Transformation */
    private double transformationFactorX = 1.0;
    private double transformationOffsetX = 0.0;
    private double transformationFactorY = 1.0;
    private double transformationOffsetY = 0.0;

    public void layout(D root) {
        layout(Collections.singletonList(root));
    }

    public void layout(List<D> roots) {
        this.roots = new ArrayList<>(roots);
        layout = null;
        bbox = null;
    }

    public List<D> roots() {
        return new ArrayList<>(roots);
    }

    public void setTransformationX(double factor, double offset) {
        transformationFactorX = factor;
        transformationOffsetX = offset;
    }

    public void setTransformationY(double factor, double offset) {
        transformationFactorY = factor;
        transformationOffsetY = offset;
    }

    public void setTransformation(double factorX, double offsetX, double factorY, double offsetY) {
        transformationFactorX = factorX;
        transformationOffsetX = offsetX;
        transformationFactorY = factorY;
        transformationOffsetY = offsetY;
    }

    public void setViewport(double w, double h) {
        setViewport(0, 0, w, h);
    }

    public void setViewport(double x, double y, double w, double h) {
        BoundingBox bbox = internalBBox();
        double x0 = bbox.x();
        double y0 = bbox.y();
        double w0 = bbox.w();
        double h0 = bbox.h();
        transformationFactorX = w / w0;
        transformationOffsetX = x - x0 * w / w0;
        transformationFactorY = h / h0;
        transformationOffsetY = y - y0 * h / h0;
    }

    public double transformationFactorX() {
        return transformationFactorX;
    }

    public double transformationOffsetX() {
        return transformationOffsetX;
    }

    public double transformationFactorY() {
        return transformationFactorY;
    }

    public double transformationOffsetY() {
        return transformationOffsetY;
    }

    private double transformedX(double x, double w) {
        if (transformationFactorX >= 0) {
            return transformationFactorX * x + transformationOffsetX;
        } else {
            return transformationFactorX * x + transformationOffsetX - transformedW(w);
        }
    }

    private double transformedY(double y, double h) {
        if (transformationFactorY >= 0) {
            return transformationFactorY * y + transformationOffsetY;
        } else {
            return transformationFactorY * y + transformationOffsetY - transformedH(h);
        }
    }

    private double transformedW(double w) {
        return Math.abs(transformationFactorX) * w;
    }

    private double transformedH(double h) {
        return Math.abs(transformationFactorY) * h;
    }

    public double x(D f) {
        return transformedX(internalBBox(f).x(), internalBBox(f).w());
    }

    public double y(D f) {
        return transformedY(internalBBox(f).y(), internalBBox(f).h());
    }

    public double w(D f) {
        return transformedW(internalBBox(f).w());
    }

    public double h(D f) {
        return transformedH(internalBBox(f).h());
    }

    public BoundingBox bbox(D f) {
        return new BoundingBox(x(f), y(f), w(f), h(f));
    }

    private BoundingBox internalBBox(D f) {
        BoundingBox bbox = layout().get(f);
        if (bbox == null) {
            throw new LayouterException(
                    "Missing layout information for " + f.getClass().getSimpleName() + " (Invoke layout first)");
        }
        return bbox;
    }

    public double x() {
        return transformedX(internalBBox().x(), internalBBox().w());
    }

    public double y() {
        return transformedY(internalBBox().y(), internalBBox().h());
    }

    public double w() {
        return transformedW(internalBBox().w());
    }

    public double h() {
        return transformedH(internalBBox().h());
    }

    public BoundingBox bbox() {
        return new BoundingBox(x(), y(), w(), h());
    }

    private BoundingBox internalBBox() {
        if (bbox == null) {
            Map<D, BoundingBox> layout = layout();
            if (!layout.isEmpty()) {
                double minX = Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double maxX = Double.MIN_VALUE;
                double maxY = Double.MIN_VALUE;
                for (BoundingBox bbox : layout.values()) {
                    minX = Math.min(minX, bbox.x());
                    minY = Math.min(minY, bbox.y());
                    maxX = Math.max(maxX, bbox.x2());
                    maxY = Math.max(maxY, bbox.y2());
                }
                double x = minX;
                double y = minY;
                double w = maxX - minX;
                double h = maxY - minY;
                bbox = new BoundingBox(x, y, w, h);
            } else {
                bbox = new BoundingBox(0, 0, 0, 0);
            }
        }
        return bbox;
    }

    private Map<D, BoundingBox> layout() {
        if (layout == null) {
            layout = computeLayout(roots);
        }
        return layout;
    }

    protected abstract Map<D, BoundingBox> computeLayout(List<D> roots);
}
