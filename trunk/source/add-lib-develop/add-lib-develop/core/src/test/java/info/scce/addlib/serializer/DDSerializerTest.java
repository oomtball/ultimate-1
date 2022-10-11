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
package info.scce.addlib.serializer;

import java.io.PrintWriter;
import java.util.Scanner;

import info.scce.addlib.dd.DD;
import info.scce.addlib.dd.DDManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class DDSerializerTest {

    private DDSerializer ddSerializer;

    @BeforeClass
    public void setUp() throws Exception {
        ddSerializer = new DDSerializer() {

            @Override
            protected void serialize(PrintWriter pw, DD f) {
            }

            @Override
            protected DD deserialize(DDManager ddManager, Scanner sc, DDProperty ddProperty) {
                return null;
            }
        };
    }

    @Test
    public void testStringEscaping() {
        char[] criticalChars = {'a', 'b', 'c', '\\', ';', '\n', '\r'};
        for (int i = 0; i < 256; i++) {
            String expected = randomString(criticalChars, 128);

            /* Escape n times */
            int n = (int) (1 + Math.random() * 3);
            String actual = expected;
            for (int j = 0; j < n; j++) {
                actual = ddSerializer.escapeString(actual);
            }

            /* Unescape */
            for (int j = 0; j < n; j++) {
                actual = ddSerializer.unescapeString(actual);
            }

            assertEquals(actual, expected);
        }
    }

    private String randomString(char[] chars, int length) {
        char[] str = new char[length];
        for (int i = 0; i < str.length; i++) {
            str[i] = chars[(int) (Math.random() * chars.length)];
        }
        return new String(str);
    }
}