package cpdt.common.utils;

import cpdt.common.enums.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class DeviceIdGeneratorTest{

    private static final int THREAD_COUNT = 1000;

    @BeforeEach
    void resetGenerator() {
        // Makes sure each test starts with a clean slate.
        DeviceIdGenerator.resetCounters();
    }

    @Test
    void shouldGenerateCorrectlyFormattedDeviceId() {
        String id = DeviceIdGenerator.generateDeviceId(DeviceType.TEMPERATURE_SENSOR);
        assertTrue(id.matches("TEMP-\\d{3}"));
    }

    @Test
    void shouldMaintainSeparateCountersForEachDeviceType() {
        String tempId = DeviceIdGenerator.generateDeviceId(DeviceType.TEMPERATURE_SENSOR);
        String pressureId = DeviceIdGenerator.generateDeviceId(DeviceType.PRESSURE_SENSOR);
        assertTrue(tempId.startsWith("TEMP-"));
        assertTrue(pressureId.startsWith("PRESS-"));
    }

    @Test
    void shouldThrowExceptionForNullDeviceType() {
        assertThrows(NullPointerException.class, () -> DeviceIdGenerator.generateDeviceId(null));
    }

    @Test
    void shouldGenerateUniqueIdsConcurrently() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Set<String> ids = ConcurrentHashMap.newKeySet();
        List<Callable<Void>> tasks = IntStream.range(0, THREAD_COUNT)
                .mapToObj(i -> (Callable<Void>) () -> {
                    ids.add(DeviceIdGenerator.generateDeviceId(DeviceType.GAS_SENSOR));
                    return null;
                }).toList();
        executor.invokeAll(tasks);
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(THREAD_COUNT, ids.size());
    }
}