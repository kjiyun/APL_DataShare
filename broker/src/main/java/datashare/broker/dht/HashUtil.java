package datashare.broker.dht;

import java.math.BigInteger;
import java.security.MessageDigest;

public final class HashUtil {
    // 정적 메서드만 사용하도록 설계된 클래스
    private HashUtil() {}

    // 입력 문자열 src를 받아서 SHA-1 해시 값을 정수형 ID로 변환하는 함수
    public static int sha1ToInt(String src) {
        try {
            // MessageDigest : Java의 암호화 해시 함수 유틸리티
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(src.getBytes());
            return new BigInteger(1, digest).intValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
