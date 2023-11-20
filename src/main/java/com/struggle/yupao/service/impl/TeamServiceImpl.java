package com.struggle.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.yupao.common.ErrorCode;
import com.struggle.yupao.exception.BusinessException;
import com.struggle.yupao.mapper.TeamMapper;
import com.struggle.yupao.model.domain.Team;
import com.struggle.yupao.model.domain.User;
import com.struggle.yupao.model.domain.UserTeam;
import com.struggle.yupao.model.dto.TeamQuery;
import com.struggle.yupao.model.enums.TeamStatusEnum;
import com.struggle.yupao.model.request.TeamJoinRequest;
import com.struggle.yupao.model.request.TeamQuitRequest;
import com.struggle.yupao.model.request.TeamUpdateRequest;
import com.struggle.yupao.model.vo.TeamUserVO;
import com.struggle.yupao.model.vo.UserVO;
import com.struggle.yupao.service.TeamService;
import com.struggle.yupao.service.UserService;
import com.struggle.yupao.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
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

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if(teamQuery !=null){
            Long id = teamQuery.getId();
            if(id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw ->qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            //查询最大人数相等的
            if(maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum",maxNum);
            }
            Long userId = teamQuery.getUserId();
            //根据创建人来查询
            if(userId != null && userId > 0){
                queryWrapper.eq("userId",userId);
            }
            //根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if(statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if(!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status",statusEnum.getValue());

        }
        //不展示已过期的队伍
        //expireTime is null or expireTime > now()
        queryWrapper.and(qw ->qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询创建人的用户信息
        //1,自己写SQL
        //查询队伍和创建人的信息
        //select * from team t left join user u on t.userId = u.id
        //查询队伍和已加入队伍成员的信息
        //select * from team t join user_team ut on t.id = ut.teamId;
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            //脱敏用户信息
            if(user !=null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id ==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam ==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //只有管理员或者队伍的创建者可以修改
        if(oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if(statusEnum.equals(TeamStatusEnum.SECRET)){
            if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        //队伍必须存在
        if(teamId == null || teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if(expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }
        //该用户已加入的队伍数量
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        //用户最多加入5个队伍
        if(hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
        }
        //不能重复已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        //用户最多加入5个队伍
        if(hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已加入该队伍");
        }
        //已加入队伍的人数
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        if(teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        //修改用户队伍关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        if(teamId == null || teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        Long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if(count == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        //队伍只剩一人，解散
        if(teamHasJoinNum == 1){
            //删除队伍和所有加入队伍的关系
            this.removeById(teamId);
        }else {
            //队伍还剩至少两人
            // 是队长
            if(team.getUserId().equals(userId)){
                //把队伍转移给最早加入的用户
                //1,查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size()!=2){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if(!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
                //这段代码和前面的一样，所以就直接用前面的queryWrapper
//                userTeamQueryWrapper = new QueryWrapper<>();
//                userTeamQueryWrapper.eq("teamId",teamId);
//                userTeamQueryWrapper.eq("userId",userId);
//                return userTeamService.remove(userTeamQueryWrapper);
            }
        }
        //移除关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 获取某队伍的当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }
}




