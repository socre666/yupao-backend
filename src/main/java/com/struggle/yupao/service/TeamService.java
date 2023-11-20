package com.struggle.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.yupao.model.domain.Team;
import com.struggle.yupao.model.domain.User;
import com.struggle.yupao.model.dto.TeamQuery;
import com.struggle.yupao.model.request.TeamJoinRequest;
import com.struggle.yupao.model.request.TeamQuitRequest;
import com.struggle.yupao.model.request.TeamUpdateRequest;
import com.struggle.yupao.model.vo.TeamUserVO;

import java.util.List;


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

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
}
