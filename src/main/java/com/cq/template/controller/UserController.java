package com.cq.template.controller;

import com.cq.template.common.CommonResult;
import com.cq.template.common.ResultCodeEnum;
import com.cq.template.exception.BusinessException;
import com.cq.template.model.dto.user.UserLoginDTO;
import com.cq.template.model.dto.user.UserRegisterDTO;
import com.cq.template.model.entity.User;
import com.cq.template.model.vo.LoginUserVO;
import com.cq.template.service.UserService;
import com.cq.template.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author cq
 * @since 2024/04/05
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理器")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final WxMpService wxMpService;

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册请求
     * @return {@link CommonResult}<{@link Long}>
     */
    @PutMapping("/register")
    @Operation(summary = "用户注册")
    public CommonResult<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数不能为空");
        }
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "参数不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return CommonResult.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录请求
     * @return {@link CommonResult}<{@link LoginUserVO}>
     */
    @PostMapping("/login")
    @Operation(summary = "账号密码登录")
    public CommonResult<LoginUserVO> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword);
        return CommonResult.success(loginUserVO);
    }

    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx-open")
    @Operation(summary = "微信开放平台登录")
    public CommonResult<LoginUserVO> userLoginByWxOpen(String code) {
        WxOAuth2AccessToken accessToken;
        try {
            accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "登录失败，unionId 或 mpOpenId 为空");
            }
            return CommonResult.success(userService.userLoginByMpOpen(userInfo));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 用户注销
     *
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public CommonResult<Boolean> userLogout() {
        boolean result = userService.userLogout();
        return CommonResult.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @return {@link CommonResult}<{@link LoginUserVO}>
     */
    @GetMapping("/get/login")
    @Operation(summary = "获取当前登录用户")
    public CommonResult<LoginUserVO> getLoginUser() {
        User loginUser = ThreadLocalUtil.getLoginUser();
        return CommonResult.success(userService.getLoginUserVO(loginUser));
    }
}
