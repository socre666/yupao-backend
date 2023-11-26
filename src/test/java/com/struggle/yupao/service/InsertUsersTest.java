package com.struggle.yupao.service;

import com.struggle.yupao.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mr.Chen
 */
@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;
    // CPU 密集型：分配的核心线程数=CPU-1
    // IO 密集型：分配的核心线程数可以大于 CPU 核数
    private ExecutorService executorService = new ThreadPoolExecutor(40,1000,1000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeStruggle");
            user.setAvatarUrl("https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg");
            user.setGender("男");
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setIsDelete(0);
            user.setUserRole(0);
            userList.add(user);
        }
        userService.saveBatch(userList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        //分十组
        int batchSize = 2500;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakeStruggle");
                user.setAvatarUrl("https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg");
                user.setGender("男");
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setIsDelete(0);
                user.setUserRole(0);
                userList.add(user);
                if (j % batchSize == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName:"+Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
