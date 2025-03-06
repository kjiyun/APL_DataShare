package com.dtest.drools.otp;

import com.dtest.drools.user.dto.request.SignInRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OtpController {

    public final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // 1. OTP 검증 API
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody SignInRequest request) {
        boolean isValid = otpService.verifyOtp(request);
        if (isValid) {
            return ResponseEntity.ok("유효한 OTP입니다.");
        } else {
            return ResponseEntity.badRequest().body("잘못된 OTP입니다.");
        }
    }

    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        return ResponseEntity.ok("예제 데이터 전송 완료");
    }
}
