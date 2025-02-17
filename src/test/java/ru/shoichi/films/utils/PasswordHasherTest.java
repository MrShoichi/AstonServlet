package ru.shoichi.films.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.config.AppConfig;


import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    private static final byte[] MOCK_SALT = "1234567890123456".getBytes(); // 16 байтов соли

    @BeforeAll
    static void mockAppConfig() {
        try (MockedStatic<AppConfig> mockedAppConfig = Mockito.mockStatic(AppConfig.class)) {
            mockedAppConfig.when(AppConfig::getSalt).thenReturn(MOCK_SALT);
        }
    }

    @Test
    void testHashPassword() {
        String password = "password123";
        String hashedPassword = PasswordHasher.hashPassword(password);

        assertNotNull(hashedPassword);
        assertNotEquals(password, hashedPassword);
    }

    @Test
    void testCheckPasswordCorrect() {
        String password = "securePassword123";
        String hashedPassword = PasswordHasher.hashPassword(password);

        assertTrue(PasswordHasher.checkPassword(password, hashedPassword));
    }

    @Test
    void testCheckPasswordIncorrect() {
        String password = "password123";
        String wrongPassword = "wrongPassword456";
        String hashedPassword = PasswordHasher.hashPassword(password);

        assertFalse(PasswordHasher.checkPassword(wrongPassword, hashedPassword));
    }

    @Test
    void testHashConsistency() {
        String password = "password123";
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);

        assertEquals(hash1, hash2);
    }
}
