package com.struggle.usercenter.service;
import java.util.Date;

import com.struggle.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * 用户服务测试
 * @author Mr.Chen
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("struggle");
        user.setUserAccount("123");
        user.setAvatarUrl("https://ebui-cdn.bj.bcebos.com/yiyan-logo.png");
        user.setUserPassword("122");
        user.setPhone("11");
        user.setEmail("222");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "struggle1";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "st";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "struggle1";
        userPassword = "123456";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "str uggle";
        userPassword = "12345678";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="struggle";
        checkPassword="12345678";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="struggle1";
        result = userService.userRegister(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);

    }
}