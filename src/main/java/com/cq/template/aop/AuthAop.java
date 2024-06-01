package com.cq.template.aop;

import com.cq.template.annotation.AuthCheck;
import com.cq.template.common.ResultCodeEnum;
import com.cq.template.mode.entity.User;
import com.cq.template.mode.enums.UserRoleEnum;
import com.cq.template.utils.ThreadLocalUtil;
import com.cq.template.utils.ThrowUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限校验 AOP
 *
 * @author cq
 * @since 2024/03/25
 */
@Aspect
@Component
public class AuthAop {

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 身份验证检查
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        List<UserRoleEnum> mustRoleList = List.of(authCheck.mustRole());
        User loginUser = ThreadLocalUtil.getLoginUser();
        UserRoleEnum currentUserRole = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        ThrowUtils.throwIf(!mustRoleList.contains(currentUserRole), ResultCodeEnum.NO_AUTH_ERROR);
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

