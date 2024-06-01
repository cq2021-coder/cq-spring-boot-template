package com.cq.template.constants;

/**
 * 分布式锁 key 常量
 *
 * @author cq
 * @since 2024/04/29
 */
public interface DistributedLockKeyConstant {
    /**
     * 用户注册 lock
     */
    String USER_REGISTRY_LOCK = "user_registry_lock:";
    /**
     * 微信开发平台用户注册 lock
     */
    String WXMP_USER_REGISTRY_LOCK = "wxmp_user_registry_lock:";

}
