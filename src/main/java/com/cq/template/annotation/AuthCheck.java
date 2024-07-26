package com.cq.template.annotation;

import com.cq.template.model.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员身份验证
 *
 * @author cq
 * @since 2024/03/25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return {@link String}
     */
    UserRoleEnum[] mustRole() default UserRoleEnum.ADMIN;

}
