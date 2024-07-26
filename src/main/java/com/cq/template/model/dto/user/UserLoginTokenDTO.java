package com.cq.template.model.dto.user;

import com.cq.template.model.vo.LoginUserVO;
import lombok.Data;

import java.util.Date;

/**
 * 用户登录令牌 DTO
 *
 * @author cq
 * @since 2024/01/12
 */
@Data
public class UserLoginTokenDTO {

    private LoginUserVO loginUserVO;

    private Date expireTime;

}
