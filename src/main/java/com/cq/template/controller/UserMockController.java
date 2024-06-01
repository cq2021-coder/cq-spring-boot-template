package com.cq.template.controller;

import com.cq.template.common.CommonResult;
import com.cq.template.mode.dto.user.UserLoginMockDTO;
import com.cq.template.mode.entity.User;
import com.cq.template.mode.vo.LoginUserVO;
import com.cq.template.service.UserService;
import com.cq.template.utils.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模拟控制器
 *
 * @author cq
 * @since 2024/03/25
 */
@RestController
@RequestMapping("/user/mock")
@Tag(name = "用户 mock 管理器")
@Profile({"local"})
@RequiredArgsConstructor
@Slf4j
public class UserMockController {
    private final UserService userService;

    private final TokenUtil tokenUtil;

    /**
     * 用户登录模拟接口
     *
     * @param userLoginMockDTO 用户登录请求
     * @return {@link CommonResult}<{@link LoginUserVO}>
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录模拟接口")
    public CommonResult<LoginUserVO> userLoginMock(@RequestBody UserLoginMockDTO userLoginMockDTO) {
        User user = userService.getById(userLoginMockDTO.getUserId());
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);
        loginUserVO.setToken(tokenUtil.signToken(loginUserVO));

        return CommonResult.success(loginUserVO);
    }
}
