package cpdt.common.utils;

import cpdt.common.enums.DeviceType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class responsible for generating unique device identifiers for
 * simulated plant devices.
 *
 * <p>A separate sequence number is maintained for each {@link DeviceType},
 * allowing every device category to have its own independent numbering
 * scheme. Generated identifiers follow the format:
 *
 * <pre>
 * PREFIX-001
 * PREFIX-002
 * PREFIX-003
 * </pre>
 *
 * <p>The class is thread-safe through the use of
 * {@link ConcurrentHashMap} and {@link AtomicInteger}, ensuring
 * correct identifier generation when accessed concurrently by multiple
 * simulator threads.
 *
 * <p>This is a utility class and cannot be instantiated.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public final class DeviceIdGenerator {

    private static final Map<DeviceType, AtomicInteger> DEVICE_COUNTERS = new ConcurrentHashMap<>();

    private DeviceIdGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates a unique identifier for the specified device type.
     *
     * <p>Each {@link DeviceType} maintains its own independent sequence counter.
     * If the device type is encountered for the first time, a new counter is
     * created and initialized. The counter is then incremented atomically and
     * combined with the device type prefix to produce a formatted identifier.
     *
     * @param deviceType the type of device for which a unique identifier is to be generated.
     * @return a unique identifier consisting of the device type prefix followed by a zero-padded three-digit sequence number
     * @throws NullPointerException if {@code deviceType} is {@code null}
     */
    public static String generateDeviceId(DeviceType deviceType) {
        AtomicInteger counter =
                DEVICE_COUNTERS.computeIfAbsent(deviceType, key -> new AtomicInteger(0));
        int sequenceNumber = counter.incrementAndGet();
        return String.format("%s-%03d", deviceType.getPrefix(), sequenceNumber);
    }
    /** Counter reset utility for testing */
    public static void resetCounters() {
        DEVICE_COUNTERS.clear();
    }
}