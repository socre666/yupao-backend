package com.struggle.usercenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Chen
 */
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Test
    void test(){
        //list,数据存在本地的JVM中
        List<String> list = new ArrayList<>();
        list.add("struggle");
        System.out.println("list:"+list.get(0));
//        list.remove(0);
        //数据存在redis的内存中
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("struggle");
        System.out.println("rlist:"+rList.get(0));
//        rList.remove(0);
        //map

        //set
    }
}
