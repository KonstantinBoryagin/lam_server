package ru.example.lam.server.benchmark;

import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * UUIDBenchmarkTest для тестирования времени генерации UUID реализаций
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class UUIDBenchmarkTest extends AbstractBenchmarkTest {

    public static final Long LEFT_LIMIT = 99999999999999999L;
    public static final Long RIGHT_LIMIT = 1000000000000000000L;

    /**
     * MyState переменные состояния
     */
    @State(Scope.Thread)
    public static class MyState {
        public String namespace = "6ba7b811-9dad-11d1-80b4-00c04fd430c8";
        public String name = "123.45.678";
        public long randomId = LEFT_LIMIT + (long) (Math.random() * (RIGHT_LIMIT - LEFT_LIMIT));
    }

    /**
     * Получение среднего времени генерации UUID, основанной на текущей метке времени и связанной с MAC-адресом устройства
     * 1 версия UUID
     */
    // запуск и прогрев можно настроить над каждым методом (только закомментировать эти значения в AbstractBenchmarkTest)
    // @Fork(value = 2, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testGetTimeUUIDByTimestampAndMACAddress(Blackhole blackhole) {
        final long most64SigBits = get64MostSignificantBits();
        final long least64SigBits = get64LeastSignificantBits();
        blackhole.consume(new UUID(most64SigBits, least64SigBits));
    }

    /**
     * Получение среднего времени генерации UUID с использованием хэша пространства имен и имени, алгоритм хэширования — MD5
     * 3 версия UUID
     */
    // запуск и прогрев можно настроить над каждым методом (только закомментировать эти значения в AbstractBenchmarkTest)
    // @Fork(value = 2, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testGetTimeUUIDByNamespaceAndNameMD5(MyState myState, Blackhole blackhole) {
        final byte[] nameSpaceBytes = bytesFromUUID(myState.namespace);
        final byte[] nameBytes = myState.name.getBytes(StandardCharsets.UTF_8);
        final byte[] result = joinBytes(nameSpaceBytes, nameBytes);
        blackhole.consume(UUID.nameUUIDFromBytes(result));
    }

    /**
     * Получение среднего времени генерации UUID с помощью метода randomUUID()
     * 4 версия UUID
     */
    // запуск и прогрев можно настроить над каждым методом (только закомментировать эти значения в AbstractBenchmarkTest)
    // @Fork(value = 2, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testGetTimeUUIDByRandom(Blackhole blackhole) {
        blackhole.consume(UUID.randomUUID());
    }

    /**
     * Получение среднего времени генерации UUID с использованием хэша пространства имен и имени, алгоритм хэширования — SHA-1
     * 5 версия UUID
     */
    // запуск и прогрев можно настроить над каждым методом (только закомментировать эти значения в AbstractBenchmarkTest)
    // @Fork(value = 2, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testGetTimeUUIDByNamespaceAndNameSHA_1(MyState myState, Blackhole blackhole) {
        final byte[] nameSpaceBytes = bytesFromUUID(myState.namespace);
        final byte[] nameBytes = myState.name.getBytes(StandardCharsets.UTF_8);
        final byte[] result = joinBytes(nameSpaceBytes, nameBytes);
        blackhole.consume(typeUUIDFromBytes(result));
    }

    /**
     * Получение среднего времени генерации UUID с использованием алгоритма хэширования — SHA-1 и наименее и наиболее значимых битов
     * 5 версия UUID
     */
    // запуск и прогрев можно настроить над каждым методом (только закомментировать эти значения в AbstractBenchmarkTest)
    // @Fork(value = 2, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testGetTimeUUIDBySHA_1AndLeastAndMostSignificantBits(MyState myState, Blackhole blackhole) {
        try {
            final byte[] bytes = myState.name.getBytes(StandardCharsets.UTF_8);
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] hash = md.digest(bytes);
            long msb = getLeastAndMostSignificantBits(hash, 0);
            long lsb = getLeastAndMostSignificantBits(hash, 8);
            msb &= ~(0xfL << 12);
            msb |= 5L << 12;
            lsb &= ~(0x3L << 62);
            lsb |= 2L << 62;
            blackhole.consume(new UUID(msb, lsb));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Получение среднего времени генерации ID
     * (userId-заранее заготовленная строка, timestamp-текущее время в наносекундах, random-число из 5ти символов)
     */
    // запуск и прогрев можно настроить над каждым методом (только закомментировать эти значения в AbstractBenchmarkTest)
    // @Fork(value = 2, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testGetTimeIdByUserIdAndTimestampAndRandom(MyState myState, Blackhole blackhole) {
        String userId = String.valueOf(myState.randomId);
        long time = System.nanoTime();
        long random = 10000 + (int) (Math.random() * 99999);
        blackhole.consume((userId + time + random));
    }

    /**
     * Генерация 64 наименьших значимых бита в виде длинных значений
     */
    private static long get64LeastSignificantBits() {
        final long random63BitLong = new Random().nextLong() & 0x3FFFFFFFFFFFFFFFL;
        final long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong + variant3BitFlag;
    }

    /**
     * Генерация 64 наиболее значимых бита в виде длинных значений
     */
    private static long get64MostSignificantBits() {
        final LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
        final Duration duration = Duration.between(start, LocalDateTime.now());
        final long seconds = duration.getSeconds();
        final long nanos = duration.getNano();
        final long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
        final long least12SignificantBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
        final long version = 1 << 12;
        return (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificantBitOfTime;
    }

    /**
     * Преобразование из массива байтов в UUID
     *
     * @param name массив имен
     */
    public static UUID typeUUIDFromBytes(byte[] name) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException exception) {
            throw new InternalError("SHA-1 not supported", exception);
        }
        final byte[] bytes = Arrays.copyOfRange(md.digest(name), 0, 16);
        bytes[6] &= 0x0f;
        bytes[6] |= 0x50;
        bytes[8] &= 0x3f;
        bytes[8] |= 0x80;

        return constructUUID(bytes);
    }

    /**
     * Преобразование из массива байтов в UUID
     *
     * @param data массив данных
     */
    private static UUID constructUUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";

        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (data[i] & 0xff);
        }

        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (data[i] & 0xff);
        }
        return new UUID(msb, lsb);
    }

    /**
     * Преобразование в массив байтов
     *
     * @param uuidHexString строка для пространства имен
     */
    private static byte[] bytesFromUUID(String uuidHexString) {
        final String normalizedUUIDHexString = uuidHexString.replace("-", "");

        assert normalizedUUIDHexString.length() == 32;

        final byte[] bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            final byte b = hexToByte(normalizedUUIDHexString.substring(i * 2, i * 2 + 2));
            bytes[i] = b;
        }
        return bytes;
    }

    /**
     * Преобразование шестнадцатеричной строки в байты
     *
     * @param hexString шестнадцатеричная строка
     */
    public static byte hexToByte(String hexString) {
        final int firstDigit = Character.digit(hexString.charAt(0), 16);
        final int secondDigit = Character.digit(hexString.charAt(1), 16);
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    /**
     * Объединение массивов байтов
     *
     * @param byteArray1 массив байтов
     * @param byteArray2 массив байтов
     */
    public static byte[] joinBytes(byte[] byteArray1, byte[] byteArray2) {
        final int finalLength = byteArray1.length + byteArray2.length;
        final byte[] result = new byte[finalLength];

        System.arraycopy(byteArray1, 0, result, 0, byteArray1.length);
        System.arraycopy(byteArray2, 0, result, byteArray1.length, byteArray2.length);
        return result;
    }

    /**
     * Получение наименее и наиболее значимых битов
     *
     * @param offset смещение
     */
    private static long getLeastAndMostSignificantBits(final byte[] src, final int offset) {
        long ans = 0;
        for (int i = offset + 7; i >= offset; i -= 1) {
            ans <<= 8;
            ans |= src[i] & 0xffL;
        }
        return ans;
    }
}
