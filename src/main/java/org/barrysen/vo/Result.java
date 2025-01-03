package org.barrysen.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;
import org.barrysen.constant.CommonConstant;

import java.io.Serializable;

/**
 * 功能：对象返回 基类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:34
 */
@Data
@Schema(description = "接口返回对象",title = "接口返回对象")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 2308092186487867787L;
    /**
     * 成功标志
     */
    @Schema(description = "成功标志")
    private boolean success = true;

    /**
     * 返回处理消息
     */
    @Schema(description = "返回处理消息")
    private String message = "操作成功！";

    /**
     * 返回代码
     */
    @Schema(description = "返回代码")
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    @Schema(description = "返回数据对象")
    private T result;

    /**
     * 返回数据对象-其他 data
     */
    @Schema(description = "返回数据对象-其他")
    private T resultOther;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳")
    private long timestamp = System.currentTimeMillis();

    public Result() {

    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = CommonConstant.SC_OK_200;
        this.success = true;
        return this;
    }

    public static <T> Result<T> OK() {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage("成功");
        return r;
    }

    public static <T> Result<T> OK(T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> OK(T data, T dataOther) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setResult(data);
        r.setResultOther(dataOther);
        return r;
    }

    public static <T> Result<T> OK(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(CommonConstant.SC_OK_200);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static Result<Object> error(String msg) {
        return error(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg);
    }

    public static Result<Object> error(int code, String msg) {
        Result<Object> r = new Result<Object>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public Result<T> error500(String message) {
        this.message = message;
        this.code = CommonConstant.SC_INTERNAL_SERVER_ERROR_500;
        this.success = false;
        return this;
    }

    /**
     * 无权限访问返回结果
     */
    public static Result<Object> noauth(String msg) {
        return error(CommonConstant.SC_JEECG_NO_AUTHZ, msg);
    }

    @JsonIgnore
    private String onlTable;


}
