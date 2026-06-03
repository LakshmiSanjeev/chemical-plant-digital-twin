package cpdt.common.models;

import cpdt.common.enums.ProcessArea;

public record Location(
        String locationId,
        String name,
        ProcessArea area) { }
