package com.dnu.ffeks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Fork(value = 1)
@State(Scope.Thread)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class ParallelStreamTaskImpl {

    private List<Long> collection;

    @Setup(Level.Trial)
    public void setup() {
        collection = new Random().longs(10_000_000, 1, 101)
                                 .boxed()
                                 .collect(Collectors.toList());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public long findSum(Blackhole blackhole) {
        long sum = collection.parallelStream()
                             .mapToLong(Long::longValue)
                             .sum();

        blackhole.consume(sum);  // Применяем Blackhole для предотвращения оптимизации
        return sum;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double findAverage(Blackhole blackhole) {
        double average = collection.parallelStream()
                                   .mapToLong(Long::longValue)
                                   .average()
                                   .orElseThrow(RuntimeException::new);

        blackhole.consume(average);
        return average;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public double calculateStandardDeviation(Blackhole blackhole) {
        double mean = findAverage(blackhole);

        double standardDeviation = Math.sqrt(collection.parallelStream()
                                   .mapToDouble(num -> Math.pow(num - mean, 2))
                                   .average()
                                   .orElseThrow(RuntimeException::new));

        blackhole.consume(standardDeviation);
        return standardDeviation;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<Long> multiplyCollectionByTwo(Blackhole blackhole) {
        List<Long> multipliedValues = collection.parallelStream()
                         .map(num -> num * 2)
                         .collect(Collectors.toList());

        blackhole.consume(multipliedValues);
        return multipliedValues;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<Long> filterCollection(Blackhole blackhole) {
        List<Long> filteredValues = collection.parallelStream()
                         .filter(num -> num % 2 == 0 && num % 3 == 0)
                         .collect(Collectors.toList());

        blackhole.consume(filteredValues);
        return filteredValues;
    }
}