package ru.example.lam.server.benchmark;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * AbstractBenchmarkTest для настройки работы benchmark
 */
abstract public class AbstractBenchmarkTest {

    private final static Integer MEASUREMENT_ITERATIONS = 1;
    private final static Integer WARMUP_ITERATIONS = 0;
    private final static Integer FORKS = 1;

    @Test
    @Disabled
    public void executeJmhRunner() throws RunnerException {
        Options opt = new OptionsBuilder()
                // включение бенчмарка в прогон
                .include("\\." + this.getClass().getSimpleName() + "\\.")
                // количество итераций
                .measurementIterations(MEASUREMENT_ITERATIONS)
                // количество потоков для запуска бенчмарка
                .threads(1)
                // количество запусков
                .forks(FORKS)
                // количество повторений прогрева
                .warmupIterations(WARMUP_ITERATIONS)
                // должен ли принудительно выполняться сборщик мусора между итерациями
                .shouldDoGC(false)
                // должен остановиться при первой ошибке теста
                .shouldFailOnError(true)
                // тип формата результата
                .resultFormat(ResultFormatType.JSON)
                // имя файла для записи результата
                .result("target/" + this.getClass().getSimpleName() + ".json")
                // аргументы JVM
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }
}
