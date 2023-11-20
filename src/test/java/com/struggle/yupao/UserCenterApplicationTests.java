package com.struggle.yupao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
class yupaoApplicationTests {

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String password = DigestUtils.md5DigestAsHex(("struggle" + "myPassword").getBytes());
        System.out.println(password);

    }
    @Test
    void contextLoads() {
    }

}
