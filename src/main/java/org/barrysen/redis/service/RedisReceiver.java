package org.barrysen.redis.service;



import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.barrysen.constant.RedisConstant;
import org.barrysen.redis.base.RedisMap;
import org.barrysen.redis.listener.RedisListener;
import org.barrysen.utils.SpringContextHolder;
import org.springframework.stereotype.Component;

@Component
@Data
public class RedisReceiver {

    /**
     * 接受消息并调用业务逻辑处理器
     *
     * @param params
     */
    public void onMessage(RedisMap params) {
        Object handlerName = params.get(RedisConstant.HANDLER_NAME);
        RedisListener messageListener = SpringContextHolder.getHandler(handlerName.toString(), RedisListener.class);
        if (ObjectUtil.isNotEmpty(messageListener)) {
            messageListener.onMessage(params);
        }
    }

}
