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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.checkerframework.checker.nullness.qual.KeyFor;

public abstract class DDDistributionMetrics {

    private Map<Integer, Integer> distribution;

    public int min() {
        getDistribution();
        int min = Integer.MAX_VALUE;
        for (Integer depth : distribution.keySet()) {
            if (min > depth) {
                min = depth;
            }
        }
        return min;
    }

    public int max() {
        getDistribution();
        int max = 0;
        for (Integer depth : distribution.keySet()) {
            if (depth > max) {
                max = depth;
            }
        }
        return max;
    }

    public int median() {
        getDistribution();
        Set<@KeyFor("distribution") Integer> keys = new TreeSet<>(distribution.keySet());

        int numElems = 0;
        for (Integer depth : keys) {
            numElems += distribution.get(depth);
        }

        int currElems = 0;
        for (Map.Entry<Integer, Integer> depthNumElems : distribution.entrySet()) {
            currElems += depthNumElems.getValue();
            if (currElems * 2 >= numElems) {
                return depthNumElems.getKey();
            }
        }
        return -1;
    }

    public double mean() {
        getDistribution();
        int mean = 0;
        int numElems = 0;
        for (Map.Entry<Integer, Integer> depthNumElems : distribution.entrySet()) {
            mean += depthNumElems.getKey() * depthNumElems.getValue();
            numElems += depthNumElems.getValue();
        }
        return 1.0 * mean / numElems;
    }

    public double std() {
        getDistribution();
        double std = 0.0;
        int numElems = 0;
        double mean = mean();
        for (Map.Entry<Integer, Integer> depthNumElems : distribution.entrySet()) {
            std += depthNumElems.getValue() * Math.pow(depthNumElems.getKey() - mean, 2);
            numElems += depthNumElems.getValue();
        }
        return Math.sqrt(std / numElems);
    }

    protected abstract Map<Integer, Integer> generateDistribution();

    public Map<Integer, Integer> getDistribution() {
        if (distribution == null) {
            distribution = generateDistribution();
        }
        return new HashMap<>(distribution);
    }

}
