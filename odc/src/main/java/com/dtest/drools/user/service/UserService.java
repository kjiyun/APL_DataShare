package com.dtest.drools.user.service;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import com.dtest.drools.global.redis.RedisClient;
import com.dtest.drools.global.security.jwt.JwtTokenProvider;
import com.dtest.drools.otp.OtpService;
import com.dtest.drools.user.User;
import com.dtest.drools.user.UserRepository;
import com.dtest.drools.user.converter.UserConverter;
import com.dtest.drools.user.dto.request.SignInRequest;
import com.dtest.drools.user.dto.request.SignUpRequest;
import com.dtest.drools.user.dto.response.SignInResponse;
import com.dtest.drools.user.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisClient redisClient;
    private final OtpService otpService;

    @Transactional
    public void createUser(SignUpRequest signUpRequest) {
//        User existingUser = userRepository.findByEmail(signUpRequest.getEmail()).orElse(null);
//        System.out.println("existing user: " + existingUser);

//        if (existingUser != null) {
//            throw new GeneralException(ErrorStatus.USER_ALREADY_EXISTS);
//        }

        User newUser = UserConverter.toAuthUser(signUpRequest, passwordEncoder);
        userRepository.save(newUser);  // 새로운 User 객체


    }

//    @Transactional
    public SignInResponse signIn(SignInRequest signInRequest) {
        System.out.println("here" + signInRequest.getUserName());
        User existingUser = userRepository.findByUsername(signInRequest.getUserName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        System.out.println("existing user: " + existingUser);

        if (existingUser != null && otpService.verifyOtp(signInRequest)) {
            System.out.println("OTP verified");
            TokenResponse tokenResponse = jwtTokenProvider.createToken(existingUser);
            // 로그인 시 refreshToken을 redis에 저장
            redisClient.setValue(existingUser.getEmail(), tokenResponse.getRefreshToken(), 1000 * 60 * 60 * 24 * 7L);

            return UserConverter.toSignInResDto(existingUser, tokenResponse);
//            }
//            else {
//                throw new GeneralException(ErrorStatus.TOTP_NOT_MATCH);
//            }
        } else {
            System.out.println("signIn failed");
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }
    }

    public void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GeneralException(ErrorStatus.PASSWORD_NOT_MATCH);
        }
    }
}
