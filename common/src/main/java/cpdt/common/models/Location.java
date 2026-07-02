package cpdt.common.models;

import cpdt.common.enums.ProcessArea;

/**
 * Immutable value object representing the physical location of a device
 * within the simulated chemical plant.
 *
 * <p>A location uniquely identifies where a device is installed by
 * combining a location identifier, a human-readable name, and the
 * associated {@link ProcessArea}. Devices use their assigned location
 * to determine which process area environment they interact with during
 * simulation.
 *
 * @param locationId unique identifier of the location
 * @param name human-readable name of the location
 * @param area process area in which the location resides
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */
public record Location(
        String locationId,
        String name,
        ProcessArea area
) { }
