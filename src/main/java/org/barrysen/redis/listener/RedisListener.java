package org.barrysen.redis.listener;


import org.barrysen.redis.base.RedisMap;

/**
 * 功能：自定义消息监听
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:33
 */
public interface RedisListener {

    void onMessage(RedisMap message);
}
