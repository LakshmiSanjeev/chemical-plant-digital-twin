package cpdt.backend.controllers;

import cpdt.backend.twin.TwinDeviceState;
import cpdt.backend.twin.TwinStateStore;
import cpdt.common.enums.ProcessArea;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/twin")
@RequiredArgsConstructor
public class TwinController {

    private final TwinStateStore twinStateStore;

    @GetMapping
    public Collection<TwinDeviceState> getTwin() {
        return twinStateStore.getAllStates();
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<TwinDeviceState> getDeviceState(
            @PathVariable("deviceId") String deviceId){

        return twinStateStore.getState(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/area/{processArea}")
    public List<TwinDeviceState> getAreaState(@PathVariable ProcessArea processArea) {
        return twinStateStore.getStatesByProcessArea(processArea);
    }
}