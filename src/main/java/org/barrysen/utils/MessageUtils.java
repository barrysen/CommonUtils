package org.barrysen.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 功能：国际化消息处理
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:41
 */
@Component
public class MessageUtils {

    private static MessageSource messageSource;

    /**
     * 消息初始化
     * @param source
     */
    public MessageUtils(MessageSource source) {
        messageSource = source;
    }

    /**
     * 获取单个国际化翻译值
     */
    public static String get(String msgKey) {
        try {
            return messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return msgKey;
        }
    }
}
