package com.dtest.drools.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
    @NotBlank
    private String username;

}
