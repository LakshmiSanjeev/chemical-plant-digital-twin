package cpdt.common.dto;

import cpdt.common.enums.AlertSeverity;

public class AlertMessage {

    private String alertId;
    private String deviceId;
    private AlertSeverity severity;
    private String message;
    private long timestamp;

    public AlertMessage() {}

    public AlertMessage(String alertId, String deviceId, AlertSeverity severity, String message, long timestamp){
        this.alertId = alertId;
        this.deviceId = deviceId;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
    }
}