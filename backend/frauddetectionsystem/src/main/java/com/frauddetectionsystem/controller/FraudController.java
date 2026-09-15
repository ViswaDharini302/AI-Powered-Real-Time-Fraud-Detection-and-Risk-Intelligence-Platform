package com.frauddetectionsystem.controller;


import com.frauddetectionsystem.entity.FraudAlert;
import com.frauddetectionsystem.service.FraudAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudAlertService fraudAlertService;

    @GetMapping("/alerts")
    public ResponseEntity<List<FraudAlert>> alerts() {
        return ResponseEntity.ok(fraudAlertService.getAllAlerts());
    }

    @GetMapping("/alerts/{id}")
    public ResponseEntity<FraudAlert> alert(@PathVariable Long id) {
        return ResponseEntity.ok(fraudAlertService.getAlert(id));
    }

    // body: { "decision": "FRAUD" }  or  { "decision": "LEGITIMATE" }
    @PutMapping("/alerts/{id}/review")
    public ResponseEntity<FraudAlert> review(@PathVariable Long id,
                                              @RequestBody Map<String, String> body,
                                              Authentication auth) {
        return ResponseEntity.ok(fraudAlertService.review(id, body.get("decision"), auth.getName()));
    }
}