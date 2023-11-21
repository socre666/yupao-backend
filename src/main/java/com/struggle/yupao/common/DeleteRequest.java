package com.struggle.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 * @author Mr.Chen
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -7172877748072128232L;

    private long id;
}
