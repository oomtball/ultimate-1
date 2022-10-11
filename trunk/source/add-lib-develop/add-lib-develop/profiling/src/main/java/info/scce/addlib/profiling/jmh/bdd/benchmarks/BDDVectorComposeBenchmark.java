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
package info.scce.addlib.profiling.jmh.bdd.benchmarks;

import java.util.concurrent.TimeUnit;

import info.scce.addlib.dd.bdd.BDD;
import info.scce.addlib.profiling.jmh.bdd.states.BDDBenchmarkState;
import info.scce.addlib.profiling.jmh.bdd.states.CUDDBDDBenchmarkState;
import info.scce.addlib.profiling.jmh.bdd.states.SylvanBDDBenchmarkState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
@Fork(1)
public class BDDVectorComposeBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(BDDVectorComposeBenchmark.class.getSimpleName())
                                          .param("size", "12")
                                          .param("initData", "BDD,VC")
                                          .param("numWorkers", "1", "4")
                                          .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void bddVectorComposeBenchmarkCudd(CUDDBDDBenchmarkState cuddBDDBenchmarkState, Blackhole blackhole) {
        bddVectorComposeBenchmark(cuddBDDBenchmarkState, blackhole);
    }

    @Benchmark
    public void bddVectorComposeBenchmarkSylvan(SylvanBDDBenchmarkState sylvanBDDBenchmarkState, Blackhole blackhole) {
        bddVectorComposeBenchmark(sylvanBDDBenchmarkState, blackhole);
    }

    private void bddVectorComposeBenchmark(BDDBenchmarkState bddBenchmarkState, Blackhole blackhole) {
        BDD bdd = bddBenchmarkState.getSingleBDD();
        BDD[] composeArr = bddBenchmarkState.getComposeVector();

        BDD vectorComposed = bdd.vectorCompose(composeArr);
        vectorComposed.recursiveDeref();
        blackhole.consume(vectorComposed);
    }
}
