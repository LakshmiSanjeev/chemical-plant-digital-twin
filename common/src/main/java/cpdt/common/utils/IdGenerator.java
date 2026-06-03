package cpdt.common.utils;

import cpdt.common.enums.DeviceType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {

    private static final Map<DeviceType, AtomicInteger> DEVICE_COUNTERS = new ConcurrentHashMap<>();

    private IdGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generateDeviceId(DeviceType deviceType) {
        AtomicInteger counter = DEVICE_COUNTERS.computeIfAbsent(deviceType,
                                                     key -> new AtomicInteger(0));
        int sequenceNumber = counter.incrementAndGet();
        return String.format("%s-%03d", deviceType.getPrefix(), sequenceNumber);
    }
}