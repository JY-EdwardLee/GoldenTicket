package com.ssafy.ticket_backend.util;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class AesEncryptor {

    private SecretKey secretKey;

    // 생성자: 키 생성 또는 기존 키를 사용
    public AesEncryptor() throws NoSuchAlgorithmException {
        // 1. 키 생성: AES 알고리즘을 사용하고, 128비트 키를 생성
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128, 192, 256 중 선택 가능
        this.secretKey = keyGenerator.generateKey();
    }

    // 암호화 메서드
    public String encrypt(String plainText) throws Exception {
        // 암호화 객체 생성 및 초기화
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 평문을 바이트 배열로 변환하여 암호화
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // 암호화된 바이트 배열을 Base64로 인코딩하여 문자열로 반환
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 복호화 메서드
    public String decrypt(String encryptedText) throws Exception {
        // 복호화 객체 생성 및 초기화
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Base64로 인코딩된 문자열을 바이트 배열로 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

        // 복호화
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        // 복호화된 바이트 배열을 문자열로 변환하여 반환
        return new String(decryptedBytes, "UTF-8");
    }

    // 키를 다른 곳에서 사용할 수 있도록 Base64로 인코딩하여 반환
    public String getKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}