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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class IOUtils {

    private IOUtils() {
    }

    public static PrintWriter getBufferedPrintWriterUTF8(File file) throws IOException {
        BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
        return new PrintWriter(bw);
    }

    public static PrintWriter getBufferedPrintWriterCS(File file, String charset) throws IOException {
        Charset cs = Charset.forName(charset);
        BufferedWriter bw = Files.newBufferedWriter(file.toPath(), cs);
        return new PrintWriter(bw);
    }

    public static PrintWriter getOutputStreamPrintWriterUTF8(OutputStream out) {
        Writer bw = asBufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        return new PrintWriter(bw);
    }

    public static PrintWriter getOutputStreamPrintWriterCS(OutputStream out, String charset) {
        Charset cs = Charset.forName(charset);
        Writer bw = asBufferedWriter(new OutputStreamWriter(out, cs));
        return new PrintWriter(bw);
    }

    public static Writer asBufferedWriter(Writer out) {
        if (isBufferedWriter(out)) {
            return out;
        }
        return new BufferedWriter(out);
    }

    private static boolean isBufferedWriter(Writer writer) {
        return writer instanceof BufferedWriter;
    }
}
