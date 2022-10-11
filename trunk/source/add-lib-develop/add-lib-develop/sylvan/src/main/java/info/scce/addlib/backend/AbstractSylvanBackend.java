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
package info.scce.addlib.backend;

import java.util.concurrent.atomic.AtomicInteger;

import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.sylvan.Sylvan;
import static info.scce.addlib.sylvan.Sylvan.lace_start;
import static info.scce.addlib.sylvan.Sylvan.lace_stop;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_count_refs;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_eval;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_gethigh;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_getlow;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_getvar;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_isleaf;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_nodecount;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_protect;
import static info.scce.addlib.sylvan.Sylvan.mtbdd_unprotect;
import static info.scce.addlib.sylvan.Sylvan.sylvan_init_mtbdd;
import static info.scce.addlib.sylvan.Sylvan.sylvan_init_package;
import static info.scce.addlib.sylvan.Sylvan.sylvan_quit;
import static info.scce.addlib.sylvan.Sylvan.sylvan_set_limits;

@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class AbstractSylvanBackend implements Backend {

    private static final long DEFAULT_MEMORY_CAP = 2048L * 1024L * 1024L;
    private static final int DEFAULT_INITIAL_RATIO = 5;
    private static final AtomicInteger NUM_OPEN_MANAGERS = new AtomicInteger(0);

    private final int numWorkers;
    private final long memoryCap;
    private final int tableRatio;
    private final int initialRatio;

    public AbstractSylvanBackend() {
        this(1, DEFAULT_MEMORY_CAP, 1, DEFAULT_INITIAL_RATIO);
    }

    public AbstractSylvanBackend(int numWorkers, long memoryCap, int tableRatio, int initialRatio) {
        this.numWorkers = numWorkers;
        this.memoryCap = memoryCap;
        this.tableRatio = tableRatio;
        this.initialRatio = initialRatio;
    }

    @Override
    public String getId() {
        return "sylvan";
    }

    @Override
    public boolean isAvailable() {
        return Sylvan.AVAILABLE;
    }

    @Override
    public long init() {
        if (NUM_OPEN_MANAGERS.intValue() == 0) {
            lace_start(numWorkers, 0);
            sylvan_set_limits(memoryCap, tableRatio, initialRatio);
            sylvan_init_package();
            sylvan_init_mtbdd();
        }
        /* As init is supposed to return the pointer of the DDManager and there are no "DDManagers" in Sylvan,
         *  we return an arbitrary number */
        NUM_OPEN_MANAGERS.incrementAndGet();
        return 0;
    }

    @Override
    public void quit(long ddManager) {
        if (NUM_OPEN_MANAGERS.decrementAndGet() == 0) {
            sylvan_quit();
            lace_stop();
        }
    }

    @Override
    public int reorder(long ddMangerPtr, DDReorderingType heuristic, int minsize) {
        throw new UnsupportedOperationException("Reordering is not supported by Sylvan.");
    }

    @Override
    public long getNumRefs(long ddManager) {
        return mtbdd_count_refs();
    }

    @Override
    public int readPerm(long ddManager, int idx) {
        /* TOOD: Check whether this makes sense */
        return idx;
    }

    @Override
    public void ref(long dd) {
        mtbdd_protect(dd);
    }

    @Override
    public void deref(long ddManager, long dd) {
        mtbdd_unprotect(ddManager);
    }

    @Override
    public long regularPtr(long dd) {
        return dd;
    }

    @Override
    public int readIndex(long dd) {
        return mtbdd_getvar(dd);
    }

    @Override
    public int isConstant(long dd) {
        return mtbdd_isleaf(dd);
    }

    @Override
    public long dagSize(long dd) {
        return mtbdd_nodecount(dd);
    }

    @Override
    public long t(long dd) {
        return mtbdd_gethigh(dd);
    }

    @Override
    public long e(long dd) {
        return mtbdd_getlow(dd);
    }

    @Override
    public long eval(long ddManager, long dd, int... input) {
        return mtbdd_eval(dd, input);
    }

    @Override
    public boolean setVariableOrder(long ddManager, int[] permutation) {
        throw new UnsupportedOperationException("Reordering is not supported by Sylvan.");
    }

    @Override
    public void enableAutomaticReordering(long ddManager, DDReorderingType heuristic) {
        throw new UnsupportedOperationException("Reordering is not supported by Sylvan.");
    }

    @Override
    public void disableAutomaticReordering(long ddManager) {
        throw new UnsupportedOperationException("Reordering is not supported by Sylvan.");
    }

    @Override
    public void setNextReordering(long ddManager, int count) {
        throw new UnsupportedOperationException("Reordering is not supported by Sylvan.");
    }

}
