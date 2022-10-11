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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.scce.addlib.dd.RegularDD;

@SuppressWarnings("PMD.RedundantFieldInitializer")
public class SimpleLayouter<D extends RegularDD<?, D>> extends Layouter<D> {

    private double anchorX = 0.0;
    private double anchorY = 0.0;
    private double nodeMarginX = 1.0;
    private double nodeMarginY = 1.0;
    private double nodeWidth = 0.5;
    private double nodeHeight = 0.5;
    private double nodeWidthPerCharacter = 0.0;

    public SimpleLayouter<D> withAnchorPosition(double anchorX, double anchorY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        return this;
    }

    public SimpleLayouter<D> withNodeMargin(double nodeMarginX, double nodeMarginY) {
        this.nodeMarginX = nodeMarginX;
        this.nodeMarginY = nodeMarginY;
        return this;
    }

    public SimpleLayouter<D> withNodeDimension(double nodeWidth, double nodeHeight, double widthPerCharacter) {
        this.nodeWidth = nodeWidth;
        this.nodeHeight = nodeHeight;
        nodeWidthPerCharacter = widthPerCharacter;
        return this;
    }

    @Override
    protected Map<D, BoundingBox> computeLayout(List<D> roots) {
        List<List<D>> sortedLayers = sortedLayers(roots);
        return computeLayoutFromSortedList(sortedLayers);
    }

    private List<List<D>> sortedLayers(List<D> roots) {
        List<List<D>> sortedLayers = new ArrayList<>();
        List<D> constants = new ArrayList<>();
        Set<D> seen = new HashSet<>();
        for (D dd : roots) {
            sortedLayersRecur(dd, sortedLayers, constants, seen);
        }
        sortedLayers.add(constants);
        return sortedLayers;
    }

    private void sortedLayersRecur(D dd, List<List<D>> sortedLayers, List<D> constants, Set<D> seen) {
        if (!seen.contains(dd)) {
            seen.add(dd);
            if (dd.isConstant()) {
                constants.add(dd);
            } else {
                int i = dd.readIndex();
                while (sortedLayers.size() <= i) {
                    sortedLayers.add(new ArrayList<>());
                }
                sortedLayers.get(i).add(dd);
                sortedLayersRecur(dd.t(), sortedLayers, constants, seen);
                sortedLayersRecur(dd.e(), sortedLayers, constants, seen);
            }
        }
    }

    private Map<D, BoundingBox> computeLayoutFromSortedList(List<List<D>> sortedLayers) {
        HashMap<D, BoundingBox> layout = new HashMap<>();
        double maxLayerWidth = maxLayerWidth(sortedLayers);
        double y = anchorY;
        for (List<D> layer : sortedLayers) {
            double x = layerX(layer, maxLayerWidth);
            for (D node : layer) {
                double nodeWidth = nodeWidth(node);
                layout.put(node, new BoundingBox(x, y, nodeWidth, nodeHeight));
                x += nodeWidth + nodeMarginX;
            }
            if (!layer.isEmpty()) {
                y += nodeHeight + nodeMarginY;
            }
        }
        return layout;
    }

    private double maxLayerWidth(List<List<D>> sortedLayers) {
        double maxLayerWidth = 0.0;
        for (List<D> layer : sortedLayers) {
            maxLayerWidth = Math.max(maxLayerWidth, layerWidth(layer));
        }
        return maxLayerWidth;
    }

    private double layerWidth(List<D> layer) {
        double width = 0.0;
        Iterator<D> it = layer.iterator();
        if (it.hasNext()) {
            width += nodeWidth(it.next());
        }
        while (it.hasNext()) {
            width += nodeMarginX + nodeWidth(it.next());
        }
        return width;
    }

    private double layerX(List<D> layer, double maxLayerWidth) {
        double smallestLayerX = anchorX - maxLayerWidth / 2;
        return smallestLayerX + (maxLayerWidth - layerWidth(layer)) / 2;
    }

    private double nodeWidth(D dd) {
        return nodeWidth + dd.toString().length() * nodeWidthPerCharacter;
    }
}
