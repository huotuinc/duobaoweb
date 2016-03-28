package com.huotu.duobaoweb.exceptions;

/**
 * 第三方错误
 * <p>该错误发生无需跟用户汇报，但需要跟运营人士汇报</p>
 * @author CJ
 */
public class InterrelatedException extends Exception{
    public InterrelatedException(Throwable cause) {
        super(cause);
    }

    public InterrelatedException() {
    }

    public InterrelatedException(String message) {
        super(message);
    }

    public InterrelatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterrelatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
