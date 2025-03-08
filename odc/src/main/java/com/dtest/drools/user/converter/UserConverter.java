package com.dtest.drools.user.converter;

import com.dtest.drools.user.User;
import com.dtest.drools.user.UserRole;
import com.dtest.drools.user.dto.request.SignUpRequest;
import com.dtest.drools.user.dto.response.SignInResponse;
import com.dtest.drools.user.dto.response.TokenResponse;
import org.springframework.security.crypto.password.PasswordEncoder;


public class UserConverter {

    // 일반 로그인 유저
    public static User toAuthUser(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .userRole(UserRole.USER)
                .build();
    }

    public static SignInResponse toSignInResDto(User user, TokenResponse tokenResponse) {
        return SignInResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }
}
