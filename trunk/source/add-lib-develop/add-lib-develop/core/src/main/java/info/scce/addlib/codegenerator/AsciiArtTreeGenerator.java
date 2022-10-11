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

import com.google.common.base.Strings;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.traverser.InorderTraverser;

public class AsciiArtTreeGenerator<D extends RegularDD<?, D>> extends CodeGenerator<D> {

    private String space;
    private String econnect;
    private String tconnect;
    private List<List<String>> table;

    public static String center(String text, int len) {
        if (len <= text.length()) {
            return text.substring(0, len);
        }
        int before = (len - text.length()) / 2;
        if (before == 0) {
            return String.format("%-" + len + "s", text);
        }
        int rest = len - before;
        return String.format("%" + before + "s%-" + rest + "s", "", text);
    }

    @Override
    public void generate(PrintWriter out, List<LabelledRegularDD<D>> roots) {

        D root = roots.get(0).dd();
        table = new ArrayList<>();

        int stringLength = findMaxLabelLength(root);

        //spaceholders proportional to boxLength
        space = Strings.repeat(" ", stringLength);
        tconnect = Strings.repeat("-", stringLength);
        econnect = Strings.repeat(" ", stringLength / 2) + ":" +
                Strings.repeat(" ", (stringLength + 1) / 2 - 1);
        fillTable(0, 0, root, stringLength);
        printTable(out);
    }

    //return value is the amount of space between parent and true child
    public int fillTable(int row, int col, D root, int stringLength) {
        int spaceRight = 1;
        while (table.size() <= row) {
            table.add(new ArrayList<>());
        }
        while (table.get(row).size() <= col) {
            table.get(row).add(space);
        }
        if (!root.isConstant()) {
            table.get(row).set(col, center(root.readName(), stringLength));
            while (table.size() <= row + 1) {
                table.add(new ArrayList<>());
            }
            while (table.get(row + 1).size() <= col) {
                table.get(row + 1).add(space);
            }
            table.get(row + 1).set(col, econnect);
            spaceRight = fillTable(row + 2, col, root.e(), stringLength);
            for (int i = 0; i < spaceRight; i++) {
                table.get(row).add(tconnect);
            }
            spaceRight += fillTable(row, col + spaceRight + 1, root.t(), stringLength) + 1;
        } else {
            table.get(row).set(col, center(root.toString(), stringLength));
        }
        return spaceRight;
    }

    public void printTable(PrintWriter out) {
        for (List<String> row : table) {
            for (String s : row) {
                out.print(s);
            }
            out.println();
        }
    }

    private int findMaxLabelLength(D f) {
        int length = 0;
        for (D g : new InorderTraverser<>(f)) {
            if (g.isConstant()) {
                length = Math.max(length, g.toString().length());
            } else {
                length = Math.max(length, g.readName().length());
            }
        }
        return length;
    }
}
