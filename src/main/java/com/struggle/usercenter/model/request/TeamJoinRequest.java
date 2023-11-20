package com.struggle.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author Mr.Chen
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = -3630710365564735954L;
    /**
     * teamId
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;




}
