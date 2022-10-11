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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import info.scce.addlib.backend.Backend;
import info.scce.addlib.util.Conversions;

public abstract class DDManager<D extends DD<?, ?>, B extends Backend> {

    public static final Pattern RESERVED_VAR_NAMES_REGEX = Pattern.compile("x\\d+");

    protected final long ptr;
    private final BiMap<String, Integer> knownVarNames;
    protected B backend;
    private final Map<Long, Integer> refCounts;

    public DDManager(B backend) {
        this(backend.init());
        this.backend = backend;
    }

    private DDManager(long ptr) {
        this.ptr = ptr;
        this.knownVarNames = HashBiMap.create();
        this.refCounts = new HashMap<>();
    }

    public long ptr() {
        return ptr;
    }

    public void quit() {
        backend.quit(ptr);
    }

    /* Reordering methods */

    public boolean reduceHeap(DDReorderingType heuristic, int minsize) {
        int reordered = backend.reorder(ptr, heuristic, minsize);
        return Conversions.asBoolean(reordered);
    }

    public long checkZeroRef() {
        return backend.getNumRefs(ptr);
    }

    public boolean setVariableOrder(int[] permutation) {
        return backend.setVariableOrder(ptr, permutation);
    }

    public void enableAutomaticReordering(DDReorderingType heuristic) {
        backend.enableAutomaticReordering(ptr, heuristic);
    }

    public void disableAutomaticReordering() {
        backend.disableAutomaticReordering(ptr);
    }

    public void setNextReordering(int count) {
        backend.setNextReordering(ptr, count);
    }

    public void incRefCount(long ptr) {
        int ptrRefCount = refCounts.getOrDefault(ptr, 0);
        refCounts.put(ptr, ptrRefCount + 1);
    }

    public void decRefCount(long ptr) {
        Integer refs = refCounts.get(ptr);
        if (refs == null) {
            throw new DDManagerException("Cannot dereference unreferenced DD");
        }

        if (refs > 1) {
            refCounts.put(ptr, refs - 1);
        } else {
            refCounts.remove(ptr);
        }
    }

    public int readPerm(int i) {
        return backend.readPerm(ptr, i);
    }

    /* Variable name mapping */

    public int varIdx(String varName) {
        if (RESERVED_VAR_NAMES_REGEX.matcher(varName).matches()) {
            String idxStr = varName.substring(1);
            return Integer.parseInt(idxStr);
        } else {
            return knownVarNames.computeIfAbsent(varName, k -> knownVarNames.size());
        }
    }

    public String varName(int varIdx) {
        BiMap<Integer, String> inverse = knownVarNames.inverse();
        return inverse.computeIfAbsent(varIdx, i -> "x" + i);
    }

    public BiMap<String, Integer> knownVarNames() {
        return knownVarNames;
    }

    public B getBackend() {
        return backend;
    }

    public void addVarName(String varName, int varIdx) {
        Integer mappedVarIdx = knownVarNames.get(varName);
        if (mappedVarIdx != null && !Objects.equals(mappedVarIdx, varIdx)) {
            throw new DDManagerException("Variable " + varName + " has already been mapped to another index");
        }

        BiMap<Integer, String> inverse = knownVarNames.inverse();
        String mappedVarName = inverse.get(varIdx);
        if (mappedVarName != null && !Objects.equals(mappedVarName, varName)) {
            throw new DDManagerException("Another variable has already been mapped to the index " + varIdx);
        }

        knownVarNames.put(varName, varIdx);
    }

    /* Required DDManager methods */

    public abstract D namedVar(String name);

    public abstract D ithVar(int i);

    public abstract D parse(String str);

}
