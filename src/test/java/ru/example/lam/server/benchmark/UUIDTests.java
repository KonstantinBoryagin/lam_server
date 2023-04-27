package ru.example.lam.server.benchmark;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * Tests для тестирования времени генерации UUID реализаций
 */
public class UUIDTests {

    public static void main(String[] args) {

        testGetTimeUUIDByTimestampAndMACAddress();
        testGetTimeUUIDByNamespaceAndNameMD5("6ba7b811-9dad-11d1-80b4-00c04fd430c8", "123.45.678");
        testGetTimeUUIDByRandom();
        testGetTimeUUIDByNamespaceAndNameSHA_1("6ba7b811-9dad-11d1-80b4-00c04fd430c8", "123.45.678");
        testGetTimeUUIDBySHA_1AndLeastAndMostSignificantBits("123.45.678");
        testGetTimeIdByUserIdAndTimestampAndRandom();

    }

    public static final Integer NUMBER_OF_ITERATIONS = 100000;
    public static final Long LEFT_LIMIT = 99999999999999999L;
    public static final Long RIGHT_LIMIT = 1000000000000000000L;
    public static final String RESULT = "%s: среднее время в нс %d и общее время в нс %d на %d итераций";


    /**
     * Получение времени генерации UUID, основанной на текущей метке времени и связанной с MAC-адресом устройства
     * 1 версия UUID
     */
    public static void testGetTimeUUIDByTimestampAndMACAddress() {
        long totalTime = 0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            final long most64SigBits = get64MostSignificantBits();
            final long least64SigBits = get64LeastSignificantBits();
            UUID getUUIDByTimestampAndMACAddress = new UUID(most64SigBits, least64SigBits);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            totalTime = totalTime + duration;
        }

        System.out.println(String.format((RESULT), "testGetTimeUUIDByTimestampAndMACAddress", totalTime/NUMBER_OF_ITERATIONS, totalTime, NUMBER_OF_ITERATIONS));
    }

    /**
     * Получение времени генерации UUID с использованием хэша пространства имен и имени, алгоритм хэширования — MD5
     * 3 версия UUID
     *
     * @param namespace хэш пространства имен
     * @param name имя
     */
    public static void testGetTimeUUIDByNamespaceAndNameMD5(String namespace, String name) {
        long totalTime = 0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            final byte[] nameSpaceBytes = bytesFromUUID(namespace);
            final byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            final byte[] result = joinBytes(nameSpaceBytes, nameBytes);
            UUID getUUIDByNamespaceAndNameMD5 = UUID.nameUUIDFromBytes(result);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            totalTime += duration;
        }

        System.out.println(String.format((RESULT), "testGetTimeUUIDByNamespaceAndNameMD5", totalTime/NUMBER_OF_ITERATIONS, totalTime, NUMBER_OF_ITERATIONS));
    }

    /**
     * Получение времени генерации UUID с помощью метода randomUUID()
     * 4 версия UUID
     */
    public static void testGetTimeUUIDByRandom() {
        long totalTime = 0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            UUID getUUIDByRandom = UUID.randomUUID();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            totalTime += duration;
        }

        System.out.println(String.format((RESULT), "testGetTimeUUIDByRandom", totalTime/NUMBER_OF_ITERATIONS, totalTime, NUMBER_OF_ITERATIONS));
    }

    /**
     * Получение времени генерации UUID с использованием хэша пространства имен и имени, алгоритм хэширования — SHA-1
     * 5 версия UUID
     *
     * @param namespace хэш пространства имен
     * @param name имя
     */
    public static void testGetTimeUUIDByNamespaceAndNameSHA_1(String namespace, String name) {
        long totalTime = 0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            final byte[] nameSpaceBytes = bytesFromUUID(namespace);
            final byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            final byte[] result = joinBytes(nameSpaceBytes, nameBytes);
            UUID getUUIDByNamespaceAndNameSHA_1 = typeUUIDFromBytes(result);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            totalTime += duration;
        }

        System.out.println(String.format((RESULT), "testGetTimeUUIDByNamespaceAndNameSHA_1", totalTime/NUMBER_OF_ITERATIONS, totalTime, NUMBER_OF_ITERATIONS));
    }

    /**
     * Получение времени генерации UUID с использованием алгоритма хэширования — SHA-1 и наименее и наиболее значимых битов
     * 5 версия UUID
     *
     * @param name имя
     */
    public static void testGetTimeUUIDBySHA_1AndLeastAndMostSignificantBits(String name) {
        long totalTime = 0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            try {
                final byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
                final MessageDigest md = MessageDigest.getInstance("SHA-1");
                final byte[] hash = md.digest(bytes);
                long msb = getLeastAndMostSignificantBits(hash, 0);
                long lsb = getLeastAndMostSignificantBits(hash, 8);
                msb &= ~(0xfL << 12);
                msb |= 5L << 12;
                lsb &= ~(0x3L << 62);
                lsb |= 2L << 62;
                UUID getUUIDBySHA_1AndLeastAndMostSignificantBits = new UUID(msb, lsb);
            } catch (NoSuchAlgorithmException e) {
                throw new AssertionError(e);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            totalTime += duration;
        }

        System.out.println(String.format((RESULT), "testGetTimeUUIDBySHA_1AndLeastAndMostSignificantBits", totalTime/NUMBER_OF_ITERATIONS, totalTime, NUMBER_OF_ITERATIONS));
    }

    /**
     * Получение времени генерации ID
     * (userId-заранее заготовленная строка, timestamp-текущее время в наносекундах, random-число из 5ти символов)
     */
    public static void testGetTimeIdByUserIdAndTimestampAndRandom() {
        long totalTime = 0;
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            long randomId = LEFT_LIMIT + (long) (Math.random() * (RIGHT_LIMIT - LEFT_LIMIT));
            long startTime = System.nanoTime();
            String userId = String.valueOf(randomId);
            long time = System.nanoTime();
            long random = 10000 + (int) (Math.random() * 99999);
            String getIdByUserIdAndTimestampAndRandom = (userId + time + random);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            totalTime += duration;
        }

        System.out.println(String.format((RESULT), "testGetTimeIdByUserIdAndTimestampAndRandom", totalTime/NUMBER_OF_ITERATIONS, totalTime, NUMBER_OF_ITERATIONS));
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
