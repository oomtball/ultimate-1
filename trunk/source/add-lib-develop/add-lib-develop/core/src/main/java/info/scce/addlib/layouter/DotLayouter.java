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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.scce.addlib.codegenerator.DotGenerator;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.traverser.PreorderTraverser;

public class DotLayouter<D extends RegularDD<?, D>> extends Layouter<D> {

    private static final String DOUBLE_REGEX = "[+-]?[0-9]*(\\.[0-9]+([eE][+-]?[0-9]+)?)?";
    private static final String NODE_REGEX = "node" + " n(?<ptr>[0-9]+)" + " (?<x>" + DOUBLE_REGEX + ")" + " (?<y>"
            + DOUBLE_REGEX + ")" + " (?<w>" + DOUBLE_REGEX + ")" + " (?<h>" + DOUBLE_REGEX + ")" + ".*";
    private final DotGenerator<D> dotGenerator;
    private Pattern dotOutLinePattern;
    private Layouter<D> fallbackLayouter;

    public DotLayouter() {
        dotGenerator = new DotGenerator<>();
    }

    public DotLayouter<D> withFallbackLayouter(Layouter<D> fallbackLayouter) {
        this.fallbackLayouter = fallbackLayouter;
        return this;
    }

    public static boolean isAvailable() {
        try {
            Process process = new ProcessBuilder("dot", "-V").start();
            process.waitFor();
            int exitValue = process.exitValue();
            boolean executionSuccessful = exitValue == 0;
            if (executionSuccessful) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected Map<D, BoundingBox> computeLayout(List<D> roots) {

        /* Try to compute layout with dot */
        if (isAvailable()) {
            try {

                /* Setup sub process */
                ProcessBuilder pb = new ProcessBuilder("dot", "-Tplain")
                        .redirectError(ProcessBuilder.Redirect.INHERIT);
                Process p = pb.start();

                /* Generate and feed dot */
                dotGenerator.generate(p.getOutputStream(), withRandomLabels(roots));
                p.getOutputStream().close();

                /* Read and parse output */
                try(InputStream in = p.getInputStream()) {
                    return parseDotOut(in, ddByPtr(roots));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Fall back to other layouter if dot is not available or errors occurred */
        if (fallbackLayouter == null) {
            throw new LayouterException("Could not layout with dot and fallback layouter was not set");
        }
        return fallbackLayouter.computeLayout(roots);
    }

    private List<LabelledRegularDD<D>> withRandomLabels(List<D> roots) {
        List<LabelledRegularDD<D>> labelled = new ArrayList<>();
        for (D r : roots) {
            labelled.add(new LabelledRegularDD<>(r, "f"));
        }
        return labelled;
    }

    private Map<Long, D> ddByPtr(List<D> roots) {
        Map<Long, D> ddByPtr = new HashMap<>();
        for (D f : new PreorderTraverser<>(roots)) {
            ddByPtr.put(f.ptr(), f);
        }
        return ddByPtr;
    }

    private Map<D, BoundingBox> parseDotOut(InputStream in, Map<Long, D> ddByPtr) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        Map<D, BoundingBox> layout = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher m = dotOutLinePattern().matcher(line);
            if (m.matches()) {
                long ptr = Long.parseLong(captureMatcherGroup(m, "ptr"));
                D dd = ddByPtr.get(ptr);
                if (dd == null) {
                    throw new IllegalArgumentException("The input contains pointers to unregistered DDs!");
                }
                double x = Double.parseDouble(captureMatcherGroup(m, "x"));
                double y = Double.parseDouble(captureMatcherGroup(m, "y"));
                double w = Double.parseDouble(captureMatcherGroup(m, "w"));
                double h = Double.parseDouble(captureMatcherGroup(m, "h"));
                layout.put(dd, new BoundingBox(x, y, w, h));
            }
        }
        return layout;
    }

    private String captureMatcherGroup(Matcher matcher, String groupName) {
        String capturedGroup = matcher.group(groupName);
        if (capturedGroup == null) {
            throw new NoSuchElementException(String.format("The matcher could not find the requested group '%s'!",
                                                           groupName));
        }
        return capturedGroup;
    }

    private Pattern dotOutLinePattern() {
        if (dotOutLinePattern == null) {
            dotOutLinePattern = Pattern.compile(NODE_REGEX);
        }
        return dotOutLinePattern;
    }
}
