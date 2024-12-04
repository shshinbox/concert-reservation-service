package me.songha.concert.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.exception.InvalidTokenException;
import me.songha.concert.shared.redis.StringRedisService;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class JwtValidator {
    private final StringRedisService redisService;
    private static volatile PublicKey cachedPublicKey;
    private static volatile long cacheExpiryTime = 0;

    public Claims validateAndExtractClaims(String token) throws Exception {
        PublicKey publicKey = getPublicKey();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String jwtId = claims.getId();
        if (isTokenBlacklisted(jwtId)) {
            throw new InvalidTokenException("[Error] Token is invalid.");
        }

        return claims;
    }

    private PublicKey getPublicKey() throws Exception {
        if (cachedPublicKey == null || System.currentTimeMillis() > cacheExpiryTime) {
            synchronized (this) {
                if (cachedPublicKey == null || System.currentTimeMillis() > cacheExpiryTime) {
                    cachedPublicKey = fetchPublicKeyFromServer();
                    cacheExpiryTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
                }
            }
        }
        return cachedPublicKey;
    }

    private boolean isTokenBlacklisted(String jwtId) {
        return redisService.isKeyExist("concert:auth:invalid:" + jwtId);
    }

    private PublicKey fetchPublicKeyFromServer() throws Exception {
        String publicKeyPem = getSamplePublicKeyPem();
        return parsePublicKeyFromPem(publicKeyPem);
    }

    private String getSamplePublicKeyPem() {
        return """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAizpln+cfUjJm3LzApGuC
                27uOGLd2AXlKaqgmrtouMC9mG2U0cozrt74fFmBiLKq4eL6BvOLtrR7qMqU9GT+6
                aO/aT8QsvmZKmaP9hMHByGdMpXFVGli7KFFAbbHIbf/JmVa2hIdaMZYBXj6z+PRW
                5y+W340PMXLautxtGdyQs5JbqlmAPuzTKqaRtgtZ5c2VjYBJzxmgQp3tdgRtVdBW
                ER+BqXJsr2Rl9NGOxhrN8SzGHe+Fmk7e3K6KY41fE8LIHz/B/uiGK3NhuXrR1//7
                7iOta5rnbfMWL3HEC2In/r7b+BR0P9x0OKjnmyZWFQvyzeGcLoKOMlcfJPdR1vpr
                2QIDAQAB
                -----END PUBLIC KEY-----
                """;
    }

    private PublicKey parsePublicKeyFromPem(String pem) throws Exception {
        String publicKeyPEM = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = java.util.Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

}
