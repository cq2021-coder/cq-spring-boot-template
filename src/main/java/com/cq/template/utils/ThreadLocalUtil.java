package com.cq.template.utils;


import com.cq.template.common.ResultCodeEnum;
import com.cq.template.exception.BusinessException;
import com.cq.template.mode.entity.User;

/**
 * 线程本地 util
 *
 * @author cq
 * @since 2024/03/25
 */
public final class ThreadLocalUtil {
    private static final ThreadLocal<User> LOGIN_USER = ThreadLocal.withInitial(User::new);

    public static User getLoginUser() {
        User user = LOGIN_USER.get();
        if (user == null || user.getId() == null) {
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN_ERROR);
        }
        return user;
    }

    public static User getLoginUserNoException() {
        User user = LOGIN_USER.get();
        if (user == null || user.getId() == null) {
            return null;
        }
        return user;
    }

    public static void setLoginUser(User user) {
        LOGIN_USER.set(user);
    }

    public static void removeLoginUser() {
        LOGIN_USER.remove();
    }

    /**
     * 清空当前线程的所有 ThreadLocal 变量
     */
    public static void removeAll() {
        removeLoginUser();
    }
}