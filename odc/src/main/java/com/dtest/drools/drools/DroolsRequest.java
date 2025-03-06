package com.dtest.drools.drools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroolsRequest {

    private String ruleUrl;

    private String frameUrl;
}
