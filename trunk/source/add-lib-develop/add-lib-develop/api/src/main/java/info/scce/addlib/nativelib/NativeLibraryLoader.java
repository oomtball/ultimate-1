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
package info.scce.addlib.nativelib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
public final class NativeLibraryLoader {

    private NativeLibraryLoader() {
    }

    public static <T> void loadNativeLibrary(Class<T> clazz, String name) throws NativeDependencyError {

        /* Find native library in the resources */
        String nativeLibResourceName = nativeLibResourceName(name);
        File nativeLibTmpFile;
        try(InputStream nativeLibIStream = clazz.getResourceAsStream(nativeLibResourceName)) {
            if (nativeLibIStream == null) {
                throw new NativeDependencyError("Missing resource '" + nativeLibResourceName + "'");
            }

            /* Copy native library to temporary file */
            nativeLibTmpFile = nativeLibTmpFile(name);
            try {
                Files.copy(nativeLibIStream, nativeLibTmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                nativeLibIStream.close();
            } catch (IOException e) {
                throw new NativeDependencyError(
                        "Failed to copy native library from resource '" + nativeLibResourceName +
                        "' to temporary file '" + nativeLibTmpFile.toPath() + "'", e);
            }
        } catch (IOException e) {
            throw new NativeDependencyError("Failed to open resource.", e);
        }

        /* Load native library from temporary file */
        try {
            System.load(nativeLibTmpFile.getAbsolutePath());
        } catch (SecurityException | UnsatisfiedLinkError | NullPointerException e) {
            throw new NativeDependencyError("Failed to load library", e);
        }
    }

    private static String nativeLibResourceName(String nativeLibName) {

        /* Derive native library's resource filename from OS family */
        OSFamily osFamily = nativeLibOSFamily();
        OSArch osArch = nativeLibOSArch();

        String filename = nativeLibName + osFamily.getFileExtension();
        String osPkg = osFamily.toString().toLowerCase(Locale.ROOT);
        String archPkg = osArch.toString().toLowerCase(Locale.ROOT);
        return "/nativelib/" + osPkg + "/" + archPkg + "/" + filename;
    }

    private static File nativeLibTmpFile(String nativeLibName) {

        OSFamily osFamily = nativeLibOSFamily();

        /* This will fail on Windows because the DLL is still load when the attempt of deletion is made. Unfortunately,
         * there isn't an easy fix. */
        try {
            File tempFile = File.createTempFile(nativeLibName, osFamily.getFileExtension());
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new NativeDependencyError("Failed to create temporary file", e);
        }
    }

    private static OSFamily nativeLibOSFamily() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        /* Recognize OS family by keywords */
        if (osName.contains("linux")) {
            return OSFamily.LINUX;
        } else if (osName.contains("mac")) {
            return OSFamily.MAC;
        } else if (osName.contains("windows")) {
            return OSFamily.WINDOWS;
        }

        throw new NativeDependencyError("Unknown or unsupported OS family");
    }

    private static OSArch nativeLibOSArch() {
        String osArch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        /* Recognize OS architecture by keywords */
        if (osArch.contains("x86_64") || osArch.contains("amd64")) {
            return OSArch.X64;
        }
        throw new NativeDependencyError("Unknown or unsupported OS architecture");
    }

    private enum OSFamily {
        LINUX(".so"),
        MAC(".dylib"),
        WINDOWS(".dll");

        final String fileExt;

        OSFamily(String fileExt) {
            this.fileExt = fileExt;
        }

        String getFileExtension() {
            return this.fileExt;
        }
    }

    private enum OSArch {
        X64
    }
}
