package com.sismics.docs.core.dao.dto;

/**
 * 用户注册申请数据传输对象
 */
public class RegisterUserDto {
    // 基本申请信息
    private String id;
    private String username;
    private String email;
    private Long storage;

    // 状态与时间信息
    private Long submitTime;
    private Integer status;
    private Long operatedTime;

    /**
     * 获取申请ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置申请ID
     */
    public RegisterUserDto setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     */
    public RegisterUserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 获取邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     */
    public RegisterUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     * 获取存储空间
     */
    public Long getStorage() {
        return storage;
    }

    /**
     * 设置存储空间大小（MB）
     */
    public RegisterUserDto setStorage(Long storage) {
        this.storage = storage;
        return this;
    }

    /**
     * 获取提交时间戳
     */
    public Long getSubmitTime() {
        return submitTime;
    }

    /**
     * 设置提交时间戳
     */
    public RegisterUserDto setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
        return this;
    }

    /**
     * 获取申请状态
     * 0-待审批, 1-已通过, 2-已拒绝
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置申请状态
     */
    public RegisterUserDto setStatus(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * 设置操作时间戳
     */
    public RegisterUserDto setOperatedTime(Long operatedTime) {
        this.operatedTime = operatedTime;
        return this;
    }

    @Override
    public String toString() {
        return "注册申请[ID=" + id + ", 用户=" + username + ", 邮箱=" + email + "]";
    }
}