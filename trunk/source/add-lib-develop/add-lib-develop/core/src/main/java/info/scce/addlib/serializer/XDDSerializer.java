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
package info.scce.addlib.serializer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import info.scce.addlib.dd.DDManager;
import info.scce.addlib.dd.xdd.XDD;
import info.scce.addlib.dd.xdd.XDDManager;
import info.scce.addlib.traverser.PostorderTraverser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class XDDSerializer<E> extends DDSerializer<XDDManager<E>, XDD<E>> {

    @Override
    protected void serialize(PrintWriter pw, XDD<E> f) {
        for (XDD<E> g : new PostorderTraverser<>(f)) {
            if (g.isConstant()) {
                pw.print(g.ptr() + ";");
                pw.print("constant" + ";");
                pw.print(escapeString(g.v().toString()) + ";");
                pw.println();
            } else {
                pw.print(g.ptr() + ";");
                pw.print("non-constant" + ";");
                pw.print(escapeString(g.readName()) + ";");
                pw.print(g.t().ptr() + ";");
                pw.print(g.e().ptr() + ";");
                pw.print(g.readPerm() + ";");
                pw.println();
            }
        }
        pw.println(f.ptr() + ";" + "root" + ";");
        pw.flush();
    }

    @SuppressWarnings("PMD.PrematureDeclaration")
    @Override
    protected XDD<E> deserialize(XDDManager<E> ddManager, Scanner sc, DDProperty ddProperty) {

        /* Build intermediate results */
        Map<Long, @NonNull XDD<E>> cache = new HashMap<>();
        XDD<E> result = null;

        while (result == null && sc.hasNextLine()) {

            /* Parse common part of the line */
            String line = sc.nextLine();
            String[] parts = line.split(";");
            long id = Long.parseLong(parts[0]);
            String type = parts[1];

            /* Treat cases separately */
            if ("root".equals(type)) {

                /* This is the root. We're done. */
                result = cache.get(id);
                if (result == null) {
                    throw new IllegalArgumentException("The given input to deserialize has an invalid format!");
                }
                result.ref();

            } else if ("constant".equals(type)) {

                /* Constant decision diagram defined by its value */
                String strValue = unescapeString(parts[2]);
                E value = ddManager.parseElement(strValue);
                XDD<E> f = ddManager.constant(value);
                cache.put(id, f);

            } else if ("non-constant".equals(type)) {

                /* Non-constant decision diagram defined by its variable name and children */
                String varName = unescapeString(parts[2]);
                long idThen = Long.parseLong(parts[3]);
                long idElse = Long.parseLong(parts[4]);
                int varIndex = Integer.parseInt(parts[5]);

                /* Find children in cache (They have appeared earlier) */
                XDD<E> t = cache.get(idThen);
                XDD<E> e = cache.get(idElse);
                if (t == null || e == null) {
                    throw new IllegalArgumentException("The given input to deserialize has an invalid format!");
                }

                if (DDManager.RESERVED_VAR_NAMES_REGEX.matcher(varName).matches()) {
                    String strIdx = varName.replaceAll("\\D", "");
                    int i = Integer.parseInt(strIdx);
                    XDD<E> f = ddManager.ithVar(i, t, e);
                    cache.put(id, f);
                } else {
                    @Nullable XDD<E> f = null;
                    switch (ddProperty) {
                        case VARINDEX:
                            f = ddManager.ithVar(varIndex, t, e);
                            break;
                        case VARNAME:
                            f = ddManager.namedVar(varName, t, e);
                            break;
                        case VARNAMEANDVARINDEX:
                            f = ddManager.namedIthVar(varName, varIndex, t, e);
                            break;
                        default:
                            throw new IllegalArgumentException("DDProperty is invalid!");
                    }
                    cache.put(id, f);
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Could not parse a XDD with the given input!");
        }

        /* Release intermediate results */
        for (XDD<E> f : cache.values()) {
            f.recursiveDeref();
        }

        return result;
    }
}
