package org.barrysen.api.dto;

import lombok.Data;
import org.barrysen.vo.LoginUser;

import java.io.Serializable;
import java.util.Date;

/**
 * 日志对象
 * cloud api 用到的接口传输对象
 */
@Data
public class LogDTO implements Serializable {

    private static final long serialVersionUID = 8482720462943906924L;

    /**
     * 内容
     */
    private String logContent;

    /**
     * 日志类型(0:操作日志;1:登录日志;2:定时任务)
     */
    private Integer logType;

    /**
     * 操作类型(1:添加;2:修改;3:删除;)
     */
    private Integer operateType;

    /**
     * 登录用户
     */
    private LoginUser loginUser;

    private String id;
    private String createBy;
    private Date createTime;
    private Long costTime;
    private String ip;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 操作人用户名称
     */
    private String username;

    /**
     * 操作人用户账户
     */
    private String userid;

    /**
     * 任务编码（消息管理sys_sms表）
     */
    private String sysSmsCode;

    /**
     * 任务名称（消息管理sys_sms表）
     */
    private String sysSmsName;

    /**
     * 返回状态（true false）
     */
    private Boolean returnStatus = true;
    /**
     * 返回编码
     */
    private Integer returnCode;
    /**
     * 返回数据
     */
    private String returnData;
    /**
     * 返回错误信息
     */
    private String returnMsg;

    /**
     * 当前租户id
     */
    private Integer tenantId;

    public LogDTO(){

    }

    public LogDTO(String logContent, Integer logType, Integer operatetype){
        this.logContent = logContent;
        this.logType = logType;
        this.operateType = operatetype;
    }

    public LogDTO(String logContent, Integer logType, Integer operatetype, LoginUser loginUser){
        this.logContent = logContent;
        this.logType = logType;
        this.operateType = operatetype;
        this.loginUser = loginUser;
    }
}
