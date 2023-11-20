package com.struggle.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍
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
