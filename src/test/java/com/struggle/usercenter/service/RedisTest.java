package com.struggle.usercenter.service;
import java.util.Date;

import com.struggle.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author Mr.Chen
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("struggleString","people");
        valueOperations.set("struggleInt",1);
        valueOperations.set("struggleDouble",2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("struggle");
        valueOperations.set("struggleUser",user);
        //查
        Object struggle = valueOperations.get("struggleString");
        Assertions.assertTrue("people".equals((String)struggle));
        struggle= valueOperations.get("struggleInt");
        Assertions.assertTrue(1==(Integer) struggle);
        struggle=valueOperations.get("struggleDouble");
        Assertions.assertTrue(2.0==(Double) struggle);
        System.out.println(valueOperations.get("struggleUser"));
        //改
        valueOperations.set("struggleString","people");
        //删
        redisTemplate.delete("struggleString");
    }
}
