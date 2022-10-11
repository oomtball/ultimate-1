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
package info.scce.addlib.codegenerator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.layouter.DotLayouter;
import info.scce.addlib.layouter.Layouter;
import info.scce.addlib.traverser.PreorderIterator;

public class AsciiArtGenerator<D extends RegularDD<?, D>> extends CodeGenerator<D> {

    public static final int TRANSFORMATION_FACTOR_Y = 10;
    public static final int TRANSFORMATION_FACTOR_X = 20;

    private final Layouter<D> layouter;
    private List<List<Character>> table;

    public AsciiArtGenerator() {
        layouter = new DotLayouter<>();
        layouter.setTransformationY(TRANSFORMATION_FACTOR_Y, 0);
        layouter.setTransformationX(TRANSFORMATION_FACTOR_X, 0);
    }

    @Override
    public void generate(PrintWriter out, List<LabelledRegularDD<D>> roots) {
        D root = roots.get(0).dd();
        layouter.layout(root);
        table = new ArrayList<>();
        addNodesToList(root);
        calculateEdges(root);
        drawTable(out);
    }

    private void addNodesToList(D root) {
        PreorderIterator<D> iterator = new PreorderIterator<>(root);
        while (iterator.hasNext()) {
            D current = iterator.next();
            int y = (int) layouter.y(current);
            int x = (int) layouter.x(current);

            String label;
            if (current.isConstant()) {
                label = current.toString();
            } else {
                label = current.readName();
            }

            while (table.size() <= y) {
                table.add(new ArrayList<>());
            }
            while (table.get(y).size() < x + label.length()) {
                table.get(y).add(' ');
            }
            int i = 0;
            for (char c : label.toCharArray()) {
                table.get(y).set(x + i, c);
                i++;
            }
        }
    }

    private void calculateEdges(D root) {
        PreorderIterator<D> iterator = new PreorderIterator<>(root);
        while (iterator.hasNext()) {
            D current = iterator.next();
            if (!current.isConstant()) {
                addEdgeToTable(newEdge(current, current.t(), true));
                addEdgeToTable(newEdge(current, current.e(), false));
                calculateEdges(current.t());
                calculateEdges(current.e());
            }
        }
    }

    private Edge newEdge(D from, D to, boolean t) {
        int startX = (int) layouter.x(from);
        int startY = (int) layouter.y(from);
        int endX = (int) layouter.x(to);
        int endY = (int) layouter.y(to);
        return new Edge(startX, startY, endX, endY, t);
    }

    private void addEdgeToTable(Edge edge) {
        double slope = 10000;
        if (edge.x1 - edge.x0 != 0) {
            slope = Math.abs(edge.y1 - edge.y0) / (double) (edge.x1 - edge.x0);
        }
        char line = calculateSlopeChar(slope, edge.type);

        int diffY = edge.y1 - edge.y0;
        if (Math.abs(diffY) > 2) {
            int startY = edge.y0 - 1;
            int endY = edge.y1;
            diffY = endY - startY;
            double j = edge.x0;
            for (int i = 0; i > diffY; i--) {
                j += 1 / slope;
                while (table.get(startY + i).size() <= j) {
                    table.get(startY + i).add(' ');
                }
                table.get(startY + i).set((int) j, line);
            }
        }
    }

    private char calculateSlopeChar(double slope, boolean type) {
        char line;
        if (type) {
            line = (slope == 0) ? '-' : (slope > 0) ? '\\' : '/';
            if (Math.abs(slope) >= 100) {
                line = '|';
            }
        } else {
            line = (slope == 0) ? '~' : (slope > 0) ? '`' : '´';
            if (Math.abs(slope) >= 100) {
                line = ':';
            }
        }
        return line;
    }

    private void drawTable(PrintWriter out) {
        for (List<Character> row : Lists.reverse(table)) {
            for (char c : row) {
                out.print(c);
            }
            out.println();
        }
        out.println();
    }


    private class Edge {

        private final int x0;
        private final int y0;
        private final int x1;
        private final int y1;
        private final boolean type;

        Edge(int x0, int y0, int x1, int y1, boolean type) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.type = type;
        }
    }
}
