package com.struggle.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.usercenter.common.ErrorCode;
import com.struggle.usercenter.exception.BusinessException;
import com.struggle.usercenter.mapper.TeamMapper;
import com.struggle.usercenter.model.domain.Team;
import com.struggle.usercenter.model.domain.User;
import com.struggle.usercenter.model.domain.UserTeam;
import com.struggle.usercenter.model.enums.TeamStatusEnum;
import com.struggle.usercenter.service.TeamService;
import com.struggle.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 *
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Override
    @Transactional(rollbackFor = Exception.class) //加个事务
    public long addTeam(Team team, User loginuser) {
        //一.请求参数是否为空
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //二.是否登录，未登录不允许创建
        if(loginuser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final Long userId = loginuser.getId();
        //三.校验信息
        //  1,队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        // 2,队伍标题<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        // 3,描述 <= 512
        String description = team.getDescription();
        if(StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        // 4,status 是否公开（int）不传默认为0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        // 5,如果status是加密状态，一定要有密码，且密码 <=32
        String password = team.getPassword();
        if(TeamStatusEnum.SECRET.equals(statusEnum)){
            if(StringUtils.isBlank(password) || password.length()>32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        // 6,超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().before(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        // 7,检验用户最多创建5个队伍
        //todo 有 bug,可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = this.count(queryWrapper);
        if(hasTeamNum >=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        //四，插入队伍信息到队伍列表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if(!result || teamId ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //五，输入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }
}




