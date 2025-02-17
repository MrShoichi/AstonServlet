package ru.shoichi.films.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AppConfig {
    private static final Config config = ConfigFactory.load();
    private static final String salt = config.getString("jwt.salt");
    private static final String secret = config.getString("jwt.secret");
    private static final byte[] BASE_SALT = Base64.getDecoder().decode("c29tZXJhbmRvbXNhbHQ=");
    private static final String BASE_SECRET = "MySuperSecretKeyForJWTGeneration12345";

    public static byte[] getSalt() {
        try {
            return Base64.getDecoder().decode(salt);
        } catch (IllegalArgumentException e) {
            return BASE_SALT;
        }
    }

    public static SecretKey getSecret() {
        try {
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return Keys.hmacShaKeyFor(BASE_SECRET.getBytes(StandardCharsets.UTF_8));
        }
    }
}
