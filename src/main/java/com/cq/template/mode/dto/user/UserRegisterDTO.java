package com.cq.template.mode.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author cq
 * @since 2024/04/07
 */
@Data
public class UserRegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
