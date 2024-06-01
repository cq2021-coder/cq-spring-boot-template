package com.cq.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.template.common.ResultCodeEnum;
import com.cq.template.exception.BusinessException;
import com.cq.template.mapper.UserMapper;
import com.cq.template.mode.entity.User;
import com.cq.template.mode.enums.UserRoleEnum;
import com.cq.template.mode.vo.LoginUserVO;
import com.cq.template.service.UserService;
import com.cq.template.utils.CopyUtil;
import com.cq.template.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author cq
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-25 00:10:03
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final TokenUtil tokenUtil;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "cq+-#2024";

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        return CopyUtil.copy(user, LoginUserVO.class);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo userInfo) {
        String unionId = userInfo.getUnionId();
        String mpOpenId = userInfo.getOpenid();
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            User user = this.lambdaQuery().eq(User::getUnionId, unionId).one();
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ResultCodeEnum.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(userInfo.getHeadImgUrl());
                user.setUserName(userInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ResultCodeEnum.OPERATION_ERROR, "登录失败");
                }
            }
            LoginUserVO loginUserVO = getLoginUserVO(user);
            loginUserVO.setToken(tokenUtil.signToken(loginUserVO));
            return loginUserVO;
        }
    }

    @Override
    public boolean userLogout() {
        return tokenUtil.removeToken();
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 4 || checkPassword.length() < 4) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            Long count = this.lambdaQuery().eq(User::getUserAccount, userAccount).count();
            if (count > 0) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 4) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        User user = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptPassword)
                .one();
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "用户不存在或密码错误");
        }

        LoginUserVO loginUserVO = getLoginUserVO(user);
        loginUserVO.setToken(tokenUtil.signToken(loginUserVO));
        return loginUserVO;
    }

}




