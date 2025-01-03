package org.barrysen.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginUser {

    /**
     * 登录人id
     */
    private String id;

    /**
     * 登录人账号
     */
    private String username;

    /**
     * 登录人名字
     */
    private String realname;

    /**
     * 登录人密码
     */
    private String password;

    /**
     * 当前登录部门code
     */
    private String orgCode;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 状态(1：正常 2：冻结 ）
     */
    private Integer status;

    private Integer delFlag;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 身份（1 普通员工 2 上级）
     */
    private Integer userIdentity;

    /**
     * 管理部门ids
     */
    private String departIds;

    /**
     * 职务，关联职务表
     */
    private String post;

    /**
     * 座机号
     */
    private String telephone;

    /**
     * 多租户id配置，编辑用户的时候设置
     */
    private String relTenantIds;

    /**
     * 当前租户id
     */
    private Integer tenantId;

    /**
     * 当前租户名称
     */
    private String tenantName;

    /**
     * 设备id uniapp推送用
     */
    private String clientId;

    /**
     * 校验码
     */
    private String validateCode;

    /**
     * 用户角色ID
     */
    private String roleId;

    /**
     * 密码验证规则
     */
    private Integer validType;

    public LoginUser() {
    }

    public LoginUser(String username, String realname) {
        this.username = username;
        this.realname = realname;
    }
}
