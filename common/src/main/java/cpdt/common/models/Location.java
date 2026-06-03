package cpdt.common.models;

import cpdt.common.enums.ProcessArea;

public class Location {

    private String locationId;
    private String name;
    private ProcessArea area;

    public Location(){}

    public Location(String locationId, String name, ProcessArea area){
        this.locationId = locationId;
        this.name = name;
        this.area = area;
    }
}
