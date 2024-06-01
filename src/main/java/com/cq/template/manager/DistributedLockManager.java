package com.cq.template.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 分布式锁管理器
 *
 * @author cq
 * @since 2024/04/28
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DistributedLockManager {

    private final RedissonClient redissonClient;

    private final String LOCK_KEY_PREFIX = "lock:";

    /**
     * 生产者执行的方法（阻塞）
     *
     * @param lockKey  锁
     * @param supplier 生产者
     * @return {@link T}
     */
    public <T> T blockExecute(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + lockKey);
        try {
            lock.lock();
            // 执行方法
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 任务执行的方法（非阻塞）
     *
     * @param lockKey  锁
     * @param runnable 任务
     */
    public void nonBlockExecute(String lockKey, Runnable runnable) {
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + lockKey);
        if (lock.tryLock()) {
            try {
                // 执行方法
                runnable.run();
            } finally {
                lock.unlock();
            }
        }
    }

}
