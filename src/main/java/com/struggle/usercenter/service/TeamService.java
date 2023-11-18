package com.struggle.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.usercenter.model.domain.Team;
import com.struggle.usercenter.model.domain.User;


/**
 *
 */
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginuser
     * @return
     */
    long addTeam(Team team, User loginuser);
}
