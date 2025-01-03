package org.barrysen.exception;

/**
 * 功能：异常管理基类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:32
 */
public class BaseException extends RuntimeException{
    private static final long serialVersionUID = -4538977495728137901L;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}
