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
package info.scce.addlib.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.dd.bdd.BDDManager;

public class CnfParser {

    public BDD parseCnf(BDDManager manager, String str) {
        try (StringReader sr = new StringReader(str);
            Scanner sc = new Scanner(sr)) {

            return parseCnf(manager, sc);
        }
    }

    public BDD parseCnf(BDDManager manager, File file) throws FileNotFoundException {
        return parseCnf(manager, file, StandardCharsets.UTF_8);
    }

    public BDD parseCnf(BDDManager manager, File file, Charset cs) throws FileNotFoundException {
        try(Scanner sc = new Scanner(file, cs.name())) {
            return parseCnf(manager, sc);
        }
    }

    public BDD parseCnf(BDDManager manager, InputStream in) {
        return parseCnf(manager, in, StandardCharsets.UTF_8);
    }

    public BDD parseCnf(BDDManager manager, InputStream in, Charset cs) {
        Scanner sc = new Scanner(in, cs.name());
        return parseCnf(manager, sc);
    }

    private BDD parseCnf(BDDManager manager, Scanner sc) {
        BDD newret;
        BDD ret = manager.readOne();
        while (sc.hasNextLine()) {
            String current = sc.nextLine();
            if (!current.startsWith("c") && !current.startsWith("p")) {
                if (current.startsWith("%")) {
                    break;
                }
                BDD clause = manager.readLogicZero();
                BDD newclause;
                String[] vars = current.split(" ");
                for (String var : vars) {
                    if ("0".equals(var)) {
                        break;
                    }
                    if ("".equals(var)) {
                        continue;
                    }
                    BDD lit;
                    if (var.startsWith("-")) {
                        lit = manager.ithVar(Integer.parseInt(var.substring(1)));
                        BDD newlit = lit.not();
                        lit.recursiveDeref();
                        lit = newlit;
                    } else {
                        lit = manager.ithVar(Integer.parseInt(var));
                    }

                    newclause = clause.or(lit);
                    clause.recursiveDeref();
                    clause = newclause;
                    lit.recursiveDeref();
                }

                newret = ret.and(clause);
                ret.recursiveDeref();
                ret = newret;
                clause.recursiveDeref();
            }
        }
        return ret;
    }
}
