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
package info.scce.addlib.profiling.jmh.add.benchmarks;

import java.util.concurrent.TimeUnit;

import info.scce.addlib.dd.add.ADD;
import info.scce.addlib.dd.add.ADDManager;
import info.scce.addlib.profiling.jmh.add.states.ADDBenchmarkState;
import info.scce.addlib.profiling.jmh.add.states.CUDDADDBenchmarkState;
import info.scce.addlib.profiling.jmh.add.states.SylvanADDBenchmarkState;
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
public class ADDSingleBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(ADDSingleBenchmark.class.getSimpleName())
                                          .param("size", "20")
                                          .param("initData", "V,ADD")
                                          .param("numWorkers", "1", "4")
                                          .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void addTimesBenchmarkCudd(CUDDADDBenchmarkState cuddADDBenchmarkState, Blackhole blackhole) {
        addTimesBenchmark(cuddADDBenchmarkState, blackhole);
    }

    @Benchmark
    public void addTimesBenchmarkSylvan(SylvanADDBenchmarkState sylvanADDBenchmarkState, Blackhole blackhole) {
        addTimesBenchmark(sylvanADDBenchmarkState, blackhole);
    }

    private void addTimesBenchmark(ADDBenchmarkState addBenchmarkState, Blackhole blackhole) {
        ADDManager addManager = addBenchmarkState.getADDManager();
        ADD add = addBenchmarkState.getSingleADD();

        ADD two = addManager.constant(2);
        ADD mult = add.times(two);

        two.recursiveDeref();
        mult.recursiveDeref();
        blackhole.consume(mult);
    }

    @Benchmark
    public void addMonadicApplyBenchmarkCudd(CUDDADDBenchmarkState cuddADDBenchmarkState, Blackhole blackhole) {
        addMonadicApplyBenchmark(cuddADDBenchmarkState, blackhole);
    }

    @Benchmark
    public void addMonadicApplyBenchmarkSylvan(SylvanADDBenchmarkState sylvanADDBenchmarkState, Blackhole blackhole) {
        addMonadicApplyBenchmark(sylvanADDBenchmarkState, blackhole);
    }

    private void addMonadicApplyBenchmark(ADDBenchmarkState addBenchmarkState, Blackhole blackhole) {
        ADD add = addBenchmarkState.getSingleADD();

        ADD doubledADD = add.monadicApply(x -> 2.0 * x);

        doubledADD.recursiveDeref();
        blackhole.consume(doubledADD);
    }

    @Benchmark
    public void addApplyBenchmarkCudd(CUDDADDBenchmarkState cuddADDBenchmarkState, Blackhole blackhole) {
        addApplyBenchmark(cuddADDBenchmarkState, blackhole);
    }

    @Benchmark
    public void addApplyBenchmarkSylvan(SylvanADDBenchmarkState sylvanADDBenchmarkState, Blackhole blackhole) {
        addApplyBenchmark(sylvanADDBenchmarkState, blackhole);
    }

    private void addApplyBenchmark(ADDBenchmarkState addBenchmarkState, Blackhole blackhole) {
        ADDManager addManager = addBenchmarkState.getADDManager();
        ADD add = addBenchmarkState.getSingleADD();

        ADD two = addManager.constant(2);
        ADD doubledADD = add.apply((x, y) -> x * y, two);

        two.recursiveDeref();
        doubledADD.recursiveDeref();
        blackhole.consume(doubledADD);
    }

    @Benchmark
    public void addComposeBenchmarkCudd(CUDDADDBenchmarkState cuddADDBenchmarkState, Blackhole blackhole) {
        addComposeBenchmark(cuddADDBenchmarkState, blackhole);
    }

    @Benchmark
    public void addComposeBenchmarkSylvan(SylvanADDBenchmarkState sylvanADDBenchmarkState, Blackhole blackhole) {
        addComposeBenchmark(sylvanADDBenchmarkState, blackhole);
    }

    private void addComposeBenchmark(ADDBenchmarkState addBenchmarkState, Blackhole blackhole) {
        ADDManager addManager = addBenchmarkState.getADDManager();
        ADD add = addBenchmarkState.getSingleADD();

        ADD anotherVar = addManager.namedVar("test");
        ADD composedADD = add.compose(anotherVar, 0);

        anotherVar.recursiveDeref();
        composedADD.recursiveDeref();
        blackhole.consume(anotherVar);
    }

    @Benchmark
    public void addDivideBenchmarkCudd(CUDDADDBenchmarkState cuddADDBenchmarkState, Blackhole blackhole) {
        addDivideBenchmark(cuddADDBenchmarkState, blackhole);
    }

    @Benchmark
    public void addDivideBenchmarkSylvan(SylvanADDBenchmarkState sylvanADDBenchmarkState, Blackhole blackhole) {
        addDivideBenchmark(sylvanADDBenchmarkState, blackhole);
    }

    private void addDivideBenchmark(ADDBenchmarkState addBenchmarkState, Blackhole blackhole) {
        ADDManager addManager = addBenchmarkState.getADDManager();
        ADD add = addBenchmarkState.getSingleADD();

        ADD two = addManager.constant(2.0);
        ADD dividedByTwoADD = add.divide(two);

        two.recursiveDeref();
        dividedByTwoADD.recursiveDeref();
        blackhole.consume(two);
        blackhole.consume(dividedByTwoADD);
    }

    @Benchmark
    public void addThresholdBenchmarkCudd(CUDDADDBenchmarkState cuddADDBenchmarkState, Blackhole blackhole) {
        addThresholdBenchmark(cuddADDBenchmarkState, blackhole);
    }

    @Benchmark
    public void addThresholdBenchmarkSylvan(SylvanADDBenchmarkState sylvanADDBenchmarkState, Blackhole blackhole) {
        addThresholdBenchmark(sylvanADDBenchmarkState, blackhole);
    }

    private void addThresholdBenchmark(ADDBenchmarkState addBenchmarkState, Blackhole blackhole) {
        ADDManager addManager = addBenchmarkState.getADDManager();
        ADD add = addBenchmarkState.getSingleADD();

        ADD zero = addManager.readZero();
        ADD zeroThresholdADD = add.threshold(zero);

        zero.recursiveDeref();
        zeroThresholdADD.recursiveDeref();
        blackhole.consume(zero);
        blackhole.consume(zeroThresholdADD);
    }
}
