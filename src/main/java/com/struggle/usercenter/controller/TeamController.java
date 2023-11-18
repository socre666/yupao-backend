package com.struggle.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.struggle.usercenter.common.BaseResponse;
import com.struggle.usercenter.common.ErrorCode;
import com.struggle.usercenter.common.ResultUtils;
import com.struggle.usercenter.exception.BusinessException;
import com.struggle.usercenter.model.domain.Team;
import com.struggle.usercenter.model.domain.User;
import com.struggle.usercenter.model.dto.TeamQuery;
import com.struggle.usercenter.model.request.TeamAddRequest;
import com.struggle.usercenter.service.TeamService;
import com.struggle.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 队伍接口
 * @author Mr.Chen
 */
@RestController
@RequestMapping("/team")
//默认允许所有的域名连接（解决前后端跨域问题）,这里设置了只允许前端（"http://localhost:5173"）访问
//allowCredentials: 指定是否允许携带凭据（如 cookies、HTTP 认证）进行跨域请求。设置为 true 表示允许，设置为 false 表示不允许。
//@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateById(team);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if(id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }
    @GetMapping("/list")
    public BaseResponse<List<Team>> listTeams(TeamQuery teamQuery){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamQuery);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);
    }
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsPage(TeamQuery teamQuery){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        long current = teamQuery.getPageNum();
        Page<Team> page = new Page<Team>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }



}
