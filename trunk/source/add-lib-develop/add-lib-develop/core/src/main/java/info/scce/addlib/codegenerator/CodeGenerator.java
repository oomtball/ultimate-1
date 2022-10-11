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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import info.scce.addlib.dd.DDException;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.util.IOUtils;

public abstract class CodeGenerator<D extends RegularDD<?, D>> {

    private static final String STANDARD_VAR_NAMES_REGEX = "[a-zA-Z][a-zA-Z0-9_]*";
    private Map<Integer, String> cache;
    private Set<String> labelsSet;
    private LabelTransformMode labelTransformMode = LabelTransformMode.FORBID_SPECIAL;

    /* Handle label transformations */
    public void setLabelTransformMode(LabelTransformMode labelTransformMode) {
        this.labelTransformMode = labelTransformMode;
    }

    /* Generate to string */

    public String generateToString(D root, String label) {
        return generateToString(new LabelledRegularDD<>(root, label));
    }

    public String generateToString(LabelledRegularDD<D> root) {
        return generateToString(Collections.singletonList(root));
    }

    public String generateToString(List<LabelledRegularDD<D>> roots) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generate(baos, roots);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /* Generate to file system */

    public void generateToFileSystem(File target, D root, String label) throws IOException {
        generateToFileSystem(target, new LabelledRegularDD<>(root, label));
    }

    public void generateToFileSystem(File target, String cs, D root, String label) throws IOException {

        generateToFileSystem(target, cs, new LabelledRegularDD<>(root, label));
    }

    public void generateToFileSystem(File target, LabelledRegularDD<D> root) throws IOException {
        generateToFileSystem(target, Collections.singletonList(root));
    }

    public void generateToFileSystem(File target, String cs, LabelledRegularDD<D> root) throws IOException {

        generateToFileSystem(target, cs, Collections.singletonList(root));
    }

    public void generateToFileSystem(File target, List<LabelledRegularDD<D>> roots) throws IOException {
        try (PrintWriter pw = IOUtils.getBufferedPrintWriterUTF8(target)) {
            generate(pw, roots);
        }
    }

    public void generateToFileSystem(File target, String cs, List<LabelledRegularDD<D>> roots) throws IOException {
        try(PrintWriter ps = IOUtils.getBufferedPrintWriterCS(target, cs)) {
            generate(ps, roots);
        }
    }

    /* Generate to standard output stream */

    public void generateToStdOut(D root, String label) {
        generateToStdOut(new LabelledRegularDD<>(root, label));
    }

    public void generateToStdOut(String cs, D root, String label) throws UnsupportedEncodingException {
        generateToStdOut(cs, new LabelledRegularDD<>(root, label));
    }

    public void generateToStdOut(LabelledRegularDD<D> root) {
        generateToStdOut(Collections.singletonList(root));
    }

    public void generateToStdOut(String cs, LabelledRegularDD<D> root) throws UnsupportedEncodingException {
        generateToStdOut(cs, Collections.singletonList(root));
    }

    public void generateToStdOut(List<LabelledRegularDD<D>> roots) {
        generate(System.out, roots);
        System.out.flush();
    }

    public void generateToStdOut(String cs, List<LabelledRegularDD<D>> roots) throws UnsupportedEncodingException {
        generate(System.out, cs, roots);
        System.out.flush();
    }

    /* Generate to output stream */

    public void generate(OutputStream out, D root, String label) {
        generate(out, new LabelledRegularDD<>(root, label));
    }

    public void generate(OutputStream out, String cs, D root, String label) throws UnsupportedEncodingException {
        generate(out, cs, new LabelledRegularDD<>(root, label));
    }

    public void generate(OutputStream out, LabelledRegularDD<D> root) {
        generate(out, Collections.singletonList(root));
    }

    public void generate(OutputStream out, String cs, LabelledRegularDD<D> root) throws UnsupportedEncodingException {
        generate(out, cs, Collections.singletonList(root));
    }

    @SuppressWarnings("PMD.CloseResource")
    public void generate(OutputStream out, List<LabelledRegularDD<D>> roots) {
        PrintWriter pw = IOUtils.getOutputStreamPrintWriterUTF8(out);
        generate(pw, roots);
        pw.flush();
    }

    @SuppressWarnings("PMD.CloseResource")
    public void generate(OutputStream out, String cs, List<LabelledRegularDD<D>> roots)
            throws UnsupportedEncodingException {
        PrintWriter ps = IOUtils.getOutputStreamPrintWriterCS(out, cs);
        generate(ps, roots);
        ps.flush();
    }

    /* Required generate method */

    public abstract void generate(PrintWriter out, List<LabelledRegularDD<D>> roots);

    /* Helper methods for subclasses */

    protected List<D> unlabelledRoots(List<LabelledRegularDD<D>> roots) {
        return roots.stream().map(LabelledRegularDD::dd).collect(Collectors.toList());
    }

    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    protected String transformToValidLabel(String label, int index) {
        if (cache == null || labelsSet == null) {
            cache = new HashMap<>();
            labelsSet = new HashSet<>();
        }

        String cachedTransformedLabel = cache.get(index);
        if (cachedTransformedLabel != null) {
            return cachedTransformedLabel;
        }

        if (labelTransformMode == LabelTransformMode.FORBID_SPECIAL && !label.matches(STANDARD_VAR_NAMES_REGEX)) {
            throw new DDException(
                    "Variable names must be of the form " + STANDARD_VAR_NAMES_REGEX + " for the generator but " +
                    label + " is not");
        }

        String validLabel = label.replace(" ", "");

        /* Convert non-standard characters to UTF-16 if demanded, else discard them */
        Matcher matcher = Pattern.compile("[^a-zA-Z0-9_]").matcher(validLabel);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            StringBuilder unicode = new StringBuilder();
            if (labelTransformMode != LabelTransformMode.DISCARD_SPECIAL) {
                for (int i = matcher.start(); i < matcher.end(); i++) {
                    unicode.append(String.format("%#06x", (int) validLabel.charAt(i)));
                }
            }
            matcher.appendReplacement(stringBuffer, unicode.toString());
        }
        matcher.appendTail(stringBuffer);
        validLabel = stringBuffer.toString();

        if (validLabel.length() == 0 || !String.valueOf(validLabel.charAt(0)).matches("[a-zA-Z]")) {
            validLabel = "var" + validLabel;
        }

        if (labelsSet.contains(validLabel)) {
            int id = 1;
            while (labelsSet.contains(validLabel + "v" + id)) {
                id++;
            }
            validLabel += index;
        }
        cache.put(index, validLabel);
        labelsSet.add(validLabel);
        return validLabel;
    }
}
