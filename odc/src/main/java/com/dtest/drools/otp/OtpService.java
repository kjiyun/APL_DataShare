package com.dtest.drools.otp;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import com.dtest.drools.user.User;
import com.dtest.drools.user.UserRepository;
import com.dtest.drools.user.dto.request.SignInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import org.apache.commons.codec.binary.Base32;


@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private static final Base32 base32 = new Base32();
//    private static final String SECRET_KEY = "JBSWY3DPEHPK3PXP";  // Base32 인코딩된 비밀키

//    @Value("${otp.secret}")
//    private String secretKey;

    // OTP 생성
//    public String generateTotp(String seed, long timeIndex) {
////        long timeIndex = Instant.now().getEpochSecond() / 30;
//        return tOTPGenerator(seed, timeIndex);
//    }

    // OTP 검증
    public boolean verifyOtp(SignInRequest signInRequest) {

//        System.out.println("hihi"+otpRequest.getClass().getName());

        User user = userRepository.findByUsername(signInRequest.getUserName())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        System.out.println("otp user"+user+user.getSeed());
        String seed = user.getSeed();

        long timeIndex = Instant.now().getEpochSecond() / 30;
        String expectedOtp = generateTotp(seed, timeIndex);
        return expectedOtp.equals(signInRequest.getTotp());
    }

    // TOTP 생성 로직
    private String generateTotp(String seed, long timeIndex) {
        try {
            byte[] key = base32.decode(seed);
            byte[] timeBytes = new byte[8];
            for (int i = 7; i >= 0; i--) {
                timeBytes[i] = (byte) (timeIndex & 0xFF);
                timeIndex >>= 8;
            }

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            mac.init(new javax.crypto.spec.SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(timeBytes);

            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24) |
                    ((hash[offset + 1] & 0xFF) << 16) |
                    ((hash[offset + 2] & 0xFF) << 8) |
                    (hash[offset + 3] & 0xFF);

            int otp = binary % 1000000;
            return String.format("%06d", otp);
        } catch (Exception e) {
            throw new RuntimeException("OTP 생성 실패", e);
        }
    }
}
