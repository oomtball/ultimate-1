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
package info.scce.addlib.codegenerator;

import java.io.File;
import java.io.IOException;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.layouter.DotLayouter;
import info.scce.addlib.utils.BackendProvider;
import org.testng.SkipException;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class DotGeneratorTest extends CodeGeneratorTest {

    @Factory(dataProviderClass = BackendProvider.class, dataProvider = "defaultADDBackends")
    public DotGeneratorTest(ADDBackend addBackend) {
        super(addBackend);
    }

    @Test
    public void testGeneratedDotSyntax() throws IOException, InterruptedException {
        /* Run test only if dot is installed */
        if (!DotLayouter.isAvailable()) {
            throw new SkipException("DOT not available");
        }

        /* Generate code */
        String graphName = "SumOfPredicatesABC";
        File dotFile = new File(workdir, graphName + ".dot");
        DotGenerator<ADD> generator = new DotGenerator<ADD>().withGraphName(graphName);
        generator.generateToStdOut(sumOfPredicatesABC);
        generator.generateToFileSystem(dotFile, sumOfPredicatesABC);

        /* Compile test program */
        File pngFile = new File(workdir, graphName + ".png");
        assertSuccessfulExecution("dot", dotFile.getAbsolutePath(), "-Tsvg");
        assertSuccessfulExecution("dot", dotFile.getAbsolutePath(), "-Tpng", "-o", pngFile.getAbsolutePath());
    }
}
