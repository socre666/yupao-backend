package com.struggle.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.struggle.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        Map<String,Integer> map = new HashMap<>();
        map.put("struggle",10);
        map.get("struggle");

        RMap<Object, Object> map1 = redissonClient.getMap("test-map");
        //set
        //stack
    }
    @Test
    void testWatchDog(){
        RLock lock = redissonClient.getLock("yupao:precache:docache:lock");
        try {
            //只有一个线程可以获取到锁
            if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                Thread.sleep(3000000);
                System.out.println("getLock:"+Thread.currentThread().getId());

            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("unLock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
