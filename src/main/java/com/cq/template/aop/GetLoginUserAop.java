package com.cq.template.aop;

import com.cq.template.mode.entity.User;
import com.cq.template.mode.vo.LoginUserVO;
import com.cq.template.service.UserService;
import com.cq.template.utils.ThreadLocalUtil;
import com.cq.template.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 获取当前登录用户并存到 threadLocal
 *
 * @author cq
 * @since 2024/03/25
 */
@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class GetLoginUserAop {

    private final UserService userService;

    private final TokenUtil tokenUtil;

    /**
     * 执行拦截
     *
     * @param point 切入点
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Around("execution(public * com.cq.template.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        User loginUser = getLoginUser();
        ThreadLocalUtil.setLoginUser(loginUser);
        try {
            return point.proceed();
        } finally {
            // 方法执行结束清空 threadLocal
            ThreadLocalUtil.removeAll();
        }

    }

    private User getLoginUser() {
        LoginUserVO currentUser = tokenUtil.getUserByRequest();
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return userService.getById(userId);
    }
}

