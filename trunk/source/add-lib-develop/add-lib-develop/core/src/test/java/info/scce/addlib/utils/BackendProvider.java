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
package info.scce.addlib.utils;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BDDBackend;
import info.scce.addlib.backend.ZDDBackend;
import org.testng.annotations.DataProvider;

public final class BackendProvider {

    private BackendProvider() {
    }

    @DataProvider(name = "defaultADDBackends")
    public static Object[] getDefaultADDBackends() {
        return new Object[] {getCuddADDBackend(), getSylvanADDBackend()};
    }

    public static ADDBackend getCuddADDBackend() {
        return info.scce.addlib.backend.BackendProvider.getADDBackend("cudd");
    }

    public static ADDBackend getSylvanADDBackend() {
        return info.scce.addlib.backend.BackendProvider.getADDBackend("sylvan");
    }

    @DataProvider(name = "defaultBDDBackends")
    public static Object[] getDefaultBDDBackends() {
        return new Object[] {getCuddBDDBackend(), getSylvanBDDBackend()};
    }

    public static BDDBackend getCuddBDDBackend() {
        return info.scce.addlib.backend.BackendProvider.getBDDBackend("cudd");
    }

    public static BDDBackend getSylvanBDDBackend() {
        return info.scce.addlib.backend.BackendProvider.getBDDBackend("sylvan");
    }

    @DataProvider(name = "defaultZDDBackends")
    public static Object[] getDefaultZDDBackends() {
        return new Object[] {getCuddZDDBackend()};
    }

    public static ZDDBackend getCuddZDDBackend() {
        return info.scce.addlib.backend.BackendProvider.getZDDBackend("cudd");
    }

    @DataProvider(name = "cuddADDBackend")
    public static Object[] getCuddADDBackendList() {
        return new Object[] {getCuddADDBackend()};
    }

    @DataProvider(name = "cuddBDDBackend")
    public static Object[] getCuddBDDBackendList() {
        return new Object[] {getCuddBDDBackend()};
    }

    @DataProvider(name = "sylvanADDBackend")
    public static Object[] getSylvanADDBackendList() {
        return new Object[] {getSylvanADDBackend()};
    }

    @DataProvider(name = "sylvanBDDBackend")
    public static Object[] getSylvanBDDBackendList() {
        return new Object[] {getSylvanBDDBackend()};
    }

}
