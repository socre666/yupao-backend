package com.struggle.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍
 * @author Mr.Chen
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -3630710365564735954L;
    /**
     * teamId
     */
    private Long teamId;





}
