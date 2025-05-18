package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.dto.RegisterUserDto;
import com.sismics.docs.core.model.jpa.RegisterUser;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 注册用户数据访问类
 */
public class RegisterUserDao {
    /**
     * 创建注册申请
     *
     * @param registerUser 注册用户对象
     * @return 创建的注册申请ID
     * @throws Exception 如果用户名已存在则抛出异常
     */
    public String create(RegisterUser registerUser) throws Exception{
        // 创建用户UUID
        registerUser.setId(UUID.randomUUID().toString());

        // 检查用户唯一性
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        // 检查是否有同名的待审核申请
        Query q1 = em.createQuery("select u from RegisterUser u where u.username = :username and u.status = 0");
        q1.setParameter("username", registerUser.getUsername());
        List<?> l1 = q1.getResultList();
        if (l1.size() > 0) {
            throw new Exception("AlreadyRegisteringUsername");
        }

        // 检查是否有同名的已存在用户
        Query q2 = em.createQuery("select u from User u where u.username = :username and u.deleteDate is null");
        q2.setParameter("username", registerUser.getUsername());
        List<?> l2 = q2.getResultList();
        if(l2.size() > 0){
            throw new Exception("AlreadyExistingUsername");
        }

        // 创建注册申请
        registerUser.setSubmitTime(new Date());
        registerUser.setStatus(0); // 确保状态为待审核
        em.persist(registerUser);

        // 创建审计日志
        AuditLogUtil.create(registerUser, AuditLogType.CREATE, registerUser.getUsername());

        return registerUser.getId();
    }

    /**
     * 获取待处理的注册申请
     *
     * @return 待处理的注册申请DTO列表
     */
    public List<RegisterUserDto> listPending() {
        StringBuilder sb = new StringBuilder("select u.REG_ID_C as c0, u.REG_USERNAME_C as c1, u.REG_EMAIL_C as c2, u.REG_STORAGE_N as c3, u.REG_SUBMIT_TIME_D as c4, u.REG_STATUS_N as c5, u.REG_OPERATED_TIME_D as c6");
        sb.append(" from T_REGISTER_USER u");
        sb.append(" where u.REG_STATUS_N = 0"); // 仅选择待处理申请
        sb.append(" order by u.REG_SUBMIT_TIME_D desc"); // 按提交时间降序排列

        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q1 = em.createNativeQuery(sb.toString());
        List<Object[]> l1 = q1.getResultList();

        List<RegisterUserDto> registerUserDtoList = new ArrayList<>();
        for(Object[] o: l1){
            int i = 0;
            RegisterUserDto registerUserDto = new RegisterUserDto();
            registerUserDto.setId((String) o[i++]);
            registerUserDto.setUsername((String) o[i++]);
            registerUserDto.setEmail((String) o[i++]);
            registerUserDto.setStorage((Long) o[i++]);
            registerUserDto.setSubmitTime(((Timestamp) o[i++]).getTime());
            registerUserDto.setStatus((Integer) o[i++]);
            if(o[i] != null){
                registerUserDto.setOperatedTime(((Timestamp) o[i]).getTime());
            }
            registerUserDtoList.add(registerUserDto);
        }
        return registerUserDtoList;
    }

    /**
     * 根据用户名查找注册申请
     *
     * @param username 用户名
     * @return 注册申请DTO，如果不存在则返回null
     */
    public RegisterUserDto findByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        StringBuilder sb = new StringBuilder("select u.REG_ID_C as c0, u.REG_USERNAME_C as c1, u.REG_EMAIL_C as c2, u.REG_STORAGE_N as c3, u.REG_SUBMIT_TIME_D as c4, u.REG_STATUS_N as c5, u.REG_OPERATED_TIME_D as c6");
        sb.append(" from T_REGISTER_USER u");
        sb.append(" where u.REG_USERNAME_C = :username");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("username", username);

        List<Object[]> results = q.getResultList();
        if (results.isEmpty()) {
            return null;
        }

        Object[] o = results.get(0);
        int i = 0;
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setId((String) o[i++]);
        registerUserDto.setUsername((String) o[i++]);
        registerUserDto.setEmail((String) o[i++]);
        registerUserDto.setStorage((Long) o[i++]);
        registerUserDto.setSubmitTime(((Timestamp) o[i++]).getTime());
        registerUserDto.setStatus((Integer) o[i++]);
        if(o[i] != null){
            registerUserDto.setOperatedTime(((Timestamp) o[i]).getTime());
        }

        return registerUserDto;
    }

    /**
     * 通过用户名更新注册申请状态
     *
     * @param username 用户名
     * @param status 新状态：1-批准，2-拒绝
     * @return 操作时间戳
     * @throws Exception 如果状态无效或用户不存在则抛出异常
     */
    public Long updateStatusByUsername(String username, Integer status) throws Exception{
        if(status < 0 || status > 2){
            throw new Exception("InvalidStatus");
        }

        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q1 = em.createQuery("select u from RegisterUser u where u.username = :username and u.status = 0");
        q1.setParameter("username", username);
        List<?> l1 = q1.getResultList();
        if(l1.isEmpty()){
            throw new Exception("NoSuchUser");
        }
        if(l1.size() > 1){
            throw new Exception("MultipleUser");
        }

        Date date = new Date();
        RegisterUser registerUser = (RegisterUser) l1.get(0);
        registerUser.setStatus(status);
        registerUser.setOperatedTime(date);

        // 如果批准申请，则创建实际用户
        if(status == 1){
            User user = new User();
            user.setRoleId(Constants.DEFAULT_USER_ROLE);
            user.setUsername(registerUser.getUsername());
            user.setPassword(registerUser.getPassword());
            user.setEmail(registerUser.getEmail());
            user.setStorageQuota(registerUser.getStorage());
            user.setOnboarding(true);

            UserDao userDao = new UserDao();
            String userId = userDao.create(user, user.getUsername());
        }

        // 创建审计日志
        AuditLogUtil.create(registerUser, AuditLogType.UPDATE, registerUser.getUsername());
        return date.getTime();
    }
}