package com.dtest.drools.user.controller;

import com.dtest.drools.global.apipayload.ApiResponse;
import com.dtest.drools.user.dto.request.SignInRequest;
import com.dtest.drools.user.dto.request.SignUpRequest;
import com.dtest.drools.user.dto.response.SignInResponse;
import com.dtest.drools.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.createUser(signUpRequest);

        return ApiResponse.onSuccess("회원가입이 완료되었습니다.");
    }

    @PostMapping("/signin")
    public ApiResponse<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        System.out.println("request: " + signInRequest);
        System.out.println("request"+ signInRequest.getPassword()+signInRequest.getUserName()+signInRequest.getTotp());
        return ApiResponse.onSuccess(userService.signIn(signInRequest));
    }
}
