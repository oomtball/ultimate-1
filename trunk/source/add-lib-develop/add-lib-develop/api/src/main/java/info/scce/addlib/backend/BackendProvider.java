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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public final class BackendProvider {

    private static final Set<String> BACKENDS;
    private static final Map<String, ADDBackend> ADDBACKENDS;
    private static final Map<String, BDDBackend> BDDBACKENDS;
    private static final Map<String, ZDDBackend> ZDDBACKENDS;


    static {
        BACKENDS = new HashSet<>();
        ADDBACKENDS = new HashMap<>();
        BDDBACKENDS = new HashMap<>();
        ZDDBACKENDS = new HashMap<>();

        final ServiceLoader<ADDBackend> addLoader = ServiceLoader.load(ADDBackend.class);
        final ServiceLoader<BDDBackend> bddLoader = ServiceLoader.load(BDDBackend.class);
        final ServiceLoader<ZDDBackend> zddLoader = ServiceLoader.load(ZDDBackend.class);

        for (ADDBackend addBackend : addLoader) {
            BACKENDS.add(addBackend.getId());
            ADDBACKENDS.put(addBackend.getId(), addBackend);
        }
        for (BDDBackend bddBackend : bddLoader) {
            BACKENDS.add(bddBackend.getId());
            BDDBACKENDS.put(bddBackend.getId(), bddBackend);
        }
        for (ZDDBackend zddBackend : zddLoader) {
            BACKENDS.add(zddBackend.getId());
            ZDDBACKENDS.put(zddBackend.getId(), zddBackend);
        }
    }

    private BackendProvider() {
    }

    private static <B extends Backend> B getBackend(Map<String, B> backends, String id) {
        final B backend = backends.get(id);

        if (backend == null) {
            if (BACKENDS.contains(id)) {
                throw new IllegalArgumentException("Backend '" + id + "' is not available.");
            } else {
                throw new IllegalArgumentException("Unknown backend '" + id + '\'');
            }
        } else if (!backend.isAvailable()) {
            throw new IllegalStateException("Backend '" + id + "' is not available on this platform");
        }

        return backend;
    }

    private static <B extends Backend> B getBackend(Map<String, B> backends) {
        return backends.values()
                .stream()
                .filter(Backend::isAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No backend available on this platform"));
    }

    public static ADDBackend getADDBackend(String id) {
        return getBackend(ADDBACKENDS, id);
    }

    public static ADDBackend getADDBackend() {
        return getBackend(ADDBACKENDS);
    }

    public static BDDBackend getBDDBackend(String id) {
        return getBackend(BDDBACKENDS, id);
    }

    public static BDDBackend getBDDBackend() {
        return getBackend(BDDBACKENDS);
    }

    public static ZDDBackend getZDDBackend(String id) {
        return getBackend(ZDDBACKENDS, id);
    }

    public static ZDDBackend getZDDBackend() {
        return getBackend(ZDDBACKENDS);
    }

}
