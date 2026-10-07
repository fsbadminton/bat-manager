package com.fsb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=test-secret-for-context-tests-0123456789")
class BatManagerApplicationTests {

    @Test
    void contextLoads() {
    }
}
