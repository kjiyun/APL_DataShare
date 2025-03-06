package com.dtest.drools.otp;

import lombok.*;

// 요청 DTO
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    private String otp;
    private String username;
    private String password;

    public String getUsername() { // ✅ 수동으로 추가
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
