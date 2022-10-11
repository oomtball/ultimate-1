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
package info.scce.addlib.dd;

import info.scce.addlib.cudd.Cudd_ReorderingType;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_ANNEALING;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_EXACT;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_GENETIC;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_GROUP_SIFT;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_GROUP_SIFT_CONV;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_LAZY_SIFT;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_LINEAR;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_LINEAR_CONVERGE;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_NONE;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_RANDOM;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_RANDOM_PIVOT;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_SAME;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_SIFT;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_SIFT_CONVERGE;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_SYMM_SIFT;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_SYMM_SIFT_CONV;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_WINDOW2;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_WINDOW2_CONV;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_WINDOW3;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_WINDOW3_CONV;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_WINDOW4;
import static info.scce.addlib.cudd.Cudd_ReorderingType.CUDD_REORDER_WINDOW4_CONV;

@SuppressWarnings("PMD.TooManyStaticImports")
public enum DDReorderingType {

    SAME(CUDD_REORDER_SAME),
    NONE(CUDD_REORDER_NONE),
    RANDOM(CUDD_REORDER_RANDOM),
    RANDOM_PIVOT(CUDD_REORDER_RANDOM_PIVOT),
    SIFT(CUDD_REORDER_SIFT),
    SIFT_CONVERGE(CUDD_REORDER_SIFT_CONVERGE),
    SYMM_SIFT(CUDD_REORDER_SYMM_SIFT),
    SYMM_SIFT_CONV(CUDD_REORDER_SYMM_SIFT_CONV),
    WINDOW2(CUDD_REORDER_WINDOW2),
    WINDOW3(CUDD_REORDER_WINDOW3),
    WINDOW4(CUDD_REORDER_WINDOW4),
    WINDOW2_CONV(CUDD_REORDER_WINDOW2_CONV),
    WINDOW3_CONV(CUDD_REORDER_WINDOW3_CONV),
    WINDOW4_CONV(CUDD_REORDER_WINDOW4_CONV),
    GROUP_SIFT(CUDD_REORDER_GROUP_SIFT),
    GROUP_SIFT_CONV(CUDD_REORDER_GROUP_SIFT_CONV),
    ANNEALING(CUDD_REORDER_ANNEALING),
    GENETIC(CUDD_REORDER_GENETIC),
    LINEAR(CUDD_REORDER_LINEAR),
    LINEAR_CONVERGE(CUDD_REORDER_LINEAR_CONVERGE),
    LAZY_SIFT(CUDD_REORDER_LAZY_SIFT),
    EXACT(CUDD_REORDER_EXACT);

    private final Cudd_ReorderingType cuddReorderingType;

    DDReorderingType(Cudd_ReorderingType cuddReorderingType) {
        this.cuddReorderingType = cuddReorderingType;
    }

    public Cudd_ReorderingType cuddReorderingType() {
        return cuddReorderingType;
    }

    public boolean isZDDReorderingType() {

        /* None of the WINDOW reordering heuristics are implemented for ZDDs in the CUDD library */
        String[] invalidReordering = {"WINDOW", "ANNEALING", "GENETIC", "LAZY_SIFT", "EXACT"};
        for (String reordering : invalidReordering) {
            if (this.toString().contains(reordering)) {
                return false;
            }
        }
        return true;
    }
}
