package ru.shoichi.films.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Base64;

public class AppConfig {
    private static final Config config = ConfigFactory.load();
    private static final String salt = config.getString("jwt.salt");
    public static final String secret = config.getString("jwt.secret");

    public static byte[] getSalt() {
        return Base64.getDecoder().decode(salt); // Декодируем из Base64 в байты
    }
}
