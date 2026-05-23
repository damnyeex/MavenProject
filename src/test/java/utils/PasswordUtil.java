package utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Хеширование пароля и верификация должны работать")
    void testHashAndVerify() {
        String rawPassword = "admin123";

        String hash = PasswordUtil.hash(rawPassword);

        assertNotNull(hash);
        assertNotEquals(rawPassword, hash);
        assertTrue(PasswordUtil.verify(rawPassword, hash));
        assertFalse(PasswordUtil.verify("wrong", hash));
    }
}