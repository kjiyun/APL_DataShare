package com.dtest.drools.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
//    @JsonProperty("username")
    private String userName;
//    @JsonProperty("password")
    private String password;
//    @JsonProperty("totp")
    private String totp;

}
