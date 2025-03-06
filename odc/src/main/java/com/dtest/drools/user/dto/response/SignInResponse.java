package com.dtest.drools.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponse {

    private Long id;

    private String email;

    private String accessToken;

    private String refreshToken;
}
