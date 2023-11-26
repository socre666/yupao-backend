package com.struggle.yupao.once;

import com.struggle.yupao.mapper.UserMapper;
import com.struggle.yupao.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @author Mr.Chen
 */
@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeStruggle");
            user.setAvatarUrl("https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg");
            user.setGender("男");
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("");
            user.setUserStatus(0);
            user.setIsDelete(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
