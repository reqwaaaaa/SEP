package com.pidu.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== Key操作 ====================

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire != null ? expire : -1;
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除key
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 批量删除key
     */
    public long delete(Collection<String> keys) {
        Long count = redisTemplate.delete(keys);
        return count != null ? count : 0;
    }

    /**
     * 模糊删除key
     */
    public long deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        }
        return 0;
    }

    // ==================== String操作 ====================

    /**
     * 获取值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置值
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置值并设置过期时间
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 如果不存在则设置
     */
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit)
        );
    }

    /**
     * 递增
     */
    public long increment(String key, long delta) {
        Long result = redisTemplate.opsForValue().increment(key, delta);
        return result != null ? result : 0;
    }

    /**
     * 递减
     */
    public long decrement(String key, long delta) {
        Long result = redisTemplate.opsForValue().decrement(key, delta);
        return result != null ? result : 0;
    }

    // ==================== Hash操作 ====================

    /**
     * 获取Hash中的值
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取Hash所有键值对
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置Hash值
     */
    public boolean hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 批量设置Hash值
     */
    public boolean hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除Hash中的字段
     */
    public long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 判断Hash中是否存在字段
     */
    public boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== Set操作 ====================

    /**
     * 获取Set中的所有值
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断Set中是否存在值
     */
    public boolean sIsMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    /**
     * 添加值到Set
     */
    public long sAdd(String key, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        return count != null ? count : 0;
    }

    /**
     * 从Set中移除值
     */
    public long sRemove(String key, Object... values) {
        Long count = redisTemplate.opsForSet().remove(key, values);
        return count != null ? count : 0;
    }

    // ==================== List操作 ====================

    /**
     * 获取List指定范围的元素
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取List长度
     */
    public long lSize(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    /**
     * 从右边添加元素
     */
    public long rPush(String key, Object value) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        return count != null ? count : 0;
    }

    /**
     * 从左边弹出元素
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
}
