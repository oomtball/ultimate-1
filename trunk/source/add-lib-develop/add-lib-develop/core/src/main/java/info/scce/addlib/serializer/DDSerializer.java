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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

import info.scce.addlib.dd.DD;
import info.scce.addlib.dd.DDManager;
import info.scce.addlib.util.IOUtils;

public abstract class DDSerializer<M extends DDManager<D, ?>, D extends DD<M, D>> {

    /* Serialisation */

    public String serialize(D f) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        serialize(pw, f);
        return sw.toString();
    }

    public void serialize(File file, D f) throws IOException {
        try(PrintWriter pw = IOUtils.getBufferedPrintWriterUTF8(file)) {
            serialize(pw, f);
        }
    }

    public void serialize(OutputStream out, D f) {
        try(PrintWriter pw = IOUtils.getOutputStreamPrintWriterUTF8(out)) {
            serialize(pw, f);
        }
    }

    protected abstract void serialize(PrintWriter pw, D f);

    /* Deserialisation */

    public D deserialize(M ddManager, String str, DDProperty ddProperty) {
        try (StringReader sr = new StringReader(str);
            Scanner sc = new Scanner(sr)) {

            return deserialize(ddManager, sc, ddProperty);
        }
    }

    public D deserialize(M ddManager, File file, DDProperty ddProperty) throws FileNotFoundException {
        return deserialize(ddManager, file, ddProperty, "UTF-8");
    }

    public D deserialize(M ddManager, File file, DDProperty ddProperty, String charset) throws FileNotFoundException {
        try (Scanner sc = new Scanner(file, charset)) {
            return deserialize(ddManager, sc, ddProperty);
        }
    }

    public D deserialize(M ddManager, InputStream in, DDProperty ddProperty) {
        return deserialize(ddManager, in, ddProperty, "UTF-8");
    }

    public D deserialize(M ddManager, InputStream in, DDProperty ddProperty, String charset) {
        Scanner sc = new Scanner(in, charset);
        return deserialize(ddManager, sc, ddProperty);
    }

    protected abstract D deserialize(M ddManager, Scanner sc, DDProperty ddProperty);

    /* String escaping */

    protected String escapeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            /* Replace NL, CR, colon, and backslash with escaped two-char sequence */
            if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == ';') {
                sb.append("\\c");
            } else if (c == '\\') {
                sb.append("\\b");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("PMD.AvoidReassigningLoopVariables")
    protected String unescapeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\') {

                /* Escaped sequence detected: next char determines NL, CR, colon, or backslash */
                char c2 = str.charAt(++i);
                if (c2 == 'n') {
                    sb.append('\n');
                }
                if (c2 == 'r') {
                    sb.append('\r');
                }
                if (c2 == 'c') {
                    sb.append(';');
                }
                if (c2 == 'b') {
                    sb.append('\\');
                }

            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
