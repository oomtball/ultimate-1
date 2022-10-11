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

import info.scce.addlib.dd.DDReorderingType;
import info.scce.addlib.nativelib.NativeLibrary;

public interface Backend extends NativeLibrary {

    /* DDManager methods */
    long init();

    void quit(long ddManager);

    int reorder(long ddManager, DDReorderingType heuristic, int minsize);

    long getNumRefs(long ddManager);

    int readPerm(long ddManager, int idx);

    /* DD methods */
    void ref(long dd);

    void deref(long ddManager, long dd);

    long regularPtr(long dd);

    int readIndex(long dd);

    int isConstant(long dd);

    long dagSize(long dd);

    long t(long dd);

    long e(long dd);

    long eval(long ddManager, long dd, int... input);

    boolean setVariableOrder(long ddManager, int[] permutation);

    void enableAutomaticReordering(long ddManager, DDReorderingType heuristic);

    void disableAutomaticReordering(long ddManager);

    void setNextReordering(long ddManager, int count);

}
