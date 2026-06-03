package cpdt.common.dto;

import cpdt.common.enums.Alerts;

public class AlertMessage {

    private String alertId;
    private String deviceId;

    private Alerts severity;

    private String message;

    private long timestamp;
}