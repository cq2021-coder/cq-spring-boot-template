package com.cq.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.template.model.entity.User;
import com.cq.template.model.vo.LoginUserVO;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
* @author cq
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-25 00:10:03
*/
public interface UserService extends IService<User> {
    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user 用户
     * @return {@link LoginUserVO}
     */
    LoginUserVO getLoginUserVO(User user);

    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo userInfo);

    boolean userLogout();

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword);
}
