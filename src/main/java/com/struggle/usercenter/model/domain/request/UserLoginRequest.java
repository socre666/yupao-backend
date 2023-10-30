package com.struggle.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author Mr.Chen
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -3630710365564735954L;
    private String userAccount;
    private String userPassword;


}
