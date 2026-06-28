package cpdt.backend.controllers;

import cpdt.backend.entities.AlertEntity;
import cpdt.backend.services.AlertFetch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertFetch alert;

    @GetMapping
    public Page<AlertEntity> getAllAlerts(Pageable pageable) {
        return alert.getAllAlerts(pageable);
    }

    @GetMapping("/active")
    public List<AlertEntity> getActiveAlerts() {
        return alert.getActiveAlerts();
    }

    @PostMapping("/{alertId}/acknowledge")
    public void acknowledge(@PathVariable String alertId) {
        alert.acknowledge(alertId);
    }
}