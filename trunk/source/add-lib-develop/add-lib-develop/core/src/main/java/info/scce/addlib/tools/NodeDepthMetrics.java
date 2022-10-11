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
package info.scce.addlib.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import info.scce.addlib.dd.DD;

public class NodeDepthMetrics extends DDDistributionMetrics {

    private final DD<?, ?> dd;

    public NodeDepthMetrics(DD<?, ?> dd) {
        this.dd = dd;
    }

    @Override
    protected Map<Integer, Integer> generateDistribution() {

        /* Determines the number of nodes on a certain depth */
        Map<Integer, Integer> nodeDepthDistribution = new HashMap<>();
        Set<Long> explored = new HashSet<>();
        Set<DD<?, ?>> currentDepth = new HashSet<>();

        /* Breadth-First Structure */
        currentDepth.add(dd);
        int depth = 0;
        while (!currentDepth.isEmpty()) {
            nodeDepthDistribution.put(depth, currentDepth.size());
            explored.addAll(currentDepth.stream().map(DD::regularPtr).collect(Collectors.toList()));

            Set<DD<?, ?>> nextDepth = new HashSet<>();
            for (DD<?, ?> ddAtCurrentDepth : currentDepth) {
                if (!ddAtCurrentDepth.isConstant()) {
                    if (!explored.contains(ddAtCurrentDepth.t().regularPtr()) ||
                        ddAtCurrentDepth.t().isConstant() && !explored.contains(ddAtCurrentDepth.t().ptr())) {
                        nextDepth.add(ddAtCurrentDepth.t());
                    }
                    if (!explored.contains(ddAtCurrentDepth.e().regularPtr()) ||
                        ddAtCurrentDepth.e().isConstant() && !explored.contains(ddAtCurrentDepth.e().ptr())) {
                        nextDepth.add(ddAtCurrentDepth.e());
                    }
                }
            }

            currentDepth = nextDepth;
            depth++;
        }

        return nodeDepthDistribution;
    }

}
