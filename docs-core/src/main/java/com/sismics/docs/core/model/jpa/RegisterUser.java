package com.sismics.docs.core.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

/**
 * 用户注册申请实体类
 */
@Entity
@Table(name = "T_REGISTER_USER")
public class RegisterUser implements Loggable {

    /**
     * 基本信息字段
     */
    @Id
    @Column(name = "REG_ID_C", nullable = false, length = 36)
    private String id;

    @Column(name = "REG_USERNAME_C", nullable = false, length = 50)
    private String username;

    @Column(name = "REG_PASSWORD_C", nullable = false, length = 60)
    private String password;

    @Column(name = "REG_EMAIL_C", nullable = false, length = 100)
    private String email;

    /**
     * 额外配置字段
     */
    @Column(name = "REG_STORAGE_N", nullable = false)
    private Long storage;

    /**
     * 状态跟踪字段
     */
    @Column(name = "REG_SUBMIT_TIME_D", nullable = false)
    private Date submitTime;

    @Column(name = "REG_STATUS_N", nullable = false)
    private Integer status;

    @Column(name = "REG_OPERATED_TIME_D")
    private Date operatedTime;

    /**
     * 获取申请ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * 设置申请ID
     */
    public RegisterUser setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 获取申请用户名
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 设置申请用户名
     */
    public RegisterUser setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 获取密码哈希值
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 设置密码哈希值
     */
    public RegisterUser setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 获取申请人邮箱
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * 设置申请人邮箱
     */
    public RegisterUser setEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     * 获取申请的存储空间大小(MB)
     */
    public Long getStorage() {
        return this.storage;
    }

    /**
     * 设置申请的存储空间大小(MB)
     */
    public RegisterUser setStorage(Long storageSize) {
        this.storage = storageSize;
        return this;
    }


    /**
     * 设置申请提交时间
     */
    public RegisterUser setSubmitTime(Date submissionTime) {
        this.submitTime = submissionTime;
        return this;
    }

    /**
     * 获取申请状态(0-待审核,1-已通过,2-已拒绝)
     */
    public Integer getStatus() {
        return this.status;
    }

    /**
     * 设置申请状态
     */
    public RegisterUser setStatus(Integer applicationStatus) {
        this.status = applicationStatus;
        return this;
    }

    /**
     * 设置操作时间(审核时间)
     */
    public RegisterUser setOperatedTime(Date reviewTime) {
        this.operatedTime = reviewTime;
        return this;
    }

    /**
     * 实现Loggable接口的消息转换方法
     */
    @Override
    public String toMessage() {
        return "注册申请 [ID=" + id + ", 用户=" + username + ", 邮箱=" + email + "]";
    }

    /**
     * 实现Loggable接口的删除日期获取方法
     * 以操作时间作为删除日期
     */
    @Override
    public Date getDeleteDate() {
        return this.operatedTime;
    }
}