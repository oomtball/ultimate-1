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
package info.scce.addlib.dd.xdd.latticedd.example;

import java.util.Locale;

public enum KleenePriestValue {

    TRUE, UNKNOWN, FALSE;

    public static KleenePriestValue parseKleenePriestValue(String str) {
        if ("true".equalsIgnoreCase(str)) {
            return TRUE;
        } else if ("false".equalsIgnoreCase(str)) {
            return FALSE;
        } else if ("unknown".equalsIgnoreCase(str)) {
            return UNKNOWN;
        }
        throw new IllegalArgumentException("Could not parse '" + str + "'");
    }

    public KleenePriestValue and(KleenePriestValue other) {
        if (this == FALSE || other == FALSE) {
            return FALSE;
        } else if (this == TRUE && other == TRUE) {
            return TRUE;
        }
        return UNKNOWN;
    }

    public KleenePriestValue or(KleenePriestValue other) {
        if (this == TRUE || other == TRUE) {
            return TRUE;
        } else if (this == FALSE && other == FALSE) {
            return FALSE;
        }
        return UNKNOWN;
    }

    public KleenePriestValue not() {
        if (this == TRUE) {
            return FALSE;
        } else if (this == FALSE) {
            return TRUE;
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ROOT);
    }
}
