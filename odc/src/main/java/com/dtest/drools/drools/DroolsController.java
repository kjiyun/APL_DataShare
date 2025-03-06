package com.dtest.drools.drools;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drools")
@RequiredArgsConstructor
public class DroolsController {

    private final DroolsService droolsService;

    @PostMapping("/run")
    public ResponseEntity<String> executeDrools(@RequestBody DroolsRequest droolsRequest) {
        String resultURL = droolsService.executeRules(droolsRequest);
        return ResponseEntity.ok(resultURL);
    }
}
