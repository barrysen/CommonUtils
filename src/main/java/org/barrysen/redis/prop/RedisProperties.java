package org.barrysen.redis.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 功能：redis配置
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:34
 */
@Getter
@Setter
@ConfigurationProperties(RedisProperties.PREFIX)
public class RedisProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.redis";
    /**
     * 是否开启Lettuce
     */
    private Boolean enable = true;
}
