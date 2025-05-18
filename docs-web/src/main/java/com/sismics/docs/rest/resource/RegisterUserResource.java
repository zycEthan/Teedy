package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.RegisterUserDao;
import com.sismics.docs.core.dao.dto.RegisterUserDto;
import com.sismics.docs.core.model.jpa.RegisterUser;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * 注册申请管理资源类
 */
@Path("/registerUser")
public class RegisterUserResource extends BaseResource {

    /**
     * 创建注册申请
     */
    @PUT
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("email") String email,
            @FormParam("storage") String storage
    ){
        // 验证输入参数
        RegisterUser application = validateAndCreateApplication(username, password, email, storage);

        // 提交申请
        try {
            new RegisterUserDao().create(application);
            return buildSuccessResponse();
        } catch(Exception e) {
            return handleRegistrationError(e);
        }
    }

    /**
     * 查询待处理申请列表
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingApplications() {
        // 权限校验
        checkAdminPermission();

        // 获取申请列表
        List<RegisterUserDto> applications = new RegisterUserDao().listPending();

        // 构建响应
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("register_users", formatApplicationList(applications));

        return Response.ok().entity(response.build()).build();
    }

    /**
     * 处理注册申请
     */
    @POST
    @Path("/operate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processApplication(
            @FormParam("username") String username,
            @FormParam("new_status") Integer status
    ) {
        // 权限校验
        checkAdminPermission();

        // 参数验证
        if (username == null || status == null) {
            return buildErrorResponse("缺少必要参数", Response.Status.BAD_REQUEST);
        }

        // 处理申请
        try {
            Long operationTime = new RegisterUserDao().updateStatusByUsername(username, status);
            return Response.ok().entity(Json.createObjectBuilder()
                    .add("status", "ok")
                    .add("operated_time", operationTime)
                    .build()).build();
        } catch(Exception e) {
            return handleProcessingError(e);
        }
    }

    /**
     * 校验并创建申请对象
     */
    private RegisterUser validateAndCreateApplication(String username, String password, String email, String storage) {
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        ValidationUtil.validateEmail(email, "email");
        Long storageQuota = ValidationUtil.validateLong(storage, "storage");

        RegisterUser application = new RegisterUser();
        application.setUsername(username)
                .setPassword(password)
                .setEmail(email)
                .setStorage(storageQuota)
                .setStatus(0);

        return application;
    }

    /**
     * 校验管理员权限
     */
    private void checkAdminPermission() {
        if (!authenticate()) {
            throw new ForbiddenException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
    }

    /**
     * 格式化申请列表为JSON
     */
    private JsonArrayBuilder formatApplicationList(List<RegisterUserDto> applications) {
        JsonArrayBuilder result = Json.createArrayBuilder();

        for(RegisterUserDto app : applications) {
            result.add(Json.createObjectBuilder()
                    .add("id", app.getId())
                    .add("username", app.getUsername())
                    .add("email", app.getEmail())
                    .add("storage", app.getStorage())
                    .add("createDate", app.getSubmitTime()));
        }

        return result;
    }

    /**
     * 构建成功响应
     */
    private Response buildSuccessResponse() {
        return Response.ok().entity(Json.createObjectBuilder()
                .add("status", "ok")
                .build()).build();
    }

    /**
     * 构建错误响应
     */
    private Response buildErrorResponse(String message, Response.Status status) {
        return Response.status(status).entity(Json.createObjectBuilder()
                .add("status", "error")
                .add("message", message)
                .build()).build();
    }

    /**
     * 处理注册错误
     */
    private Response handleRegistrationError(Exception e) {
        if("AlreadyRegisteringUsername".equals(e.getMessage())){
            throw new ClientException("AlreadyRegisteringUsername","该用户名正在审核中", e);
        } else if("AlreadyExistingUsername".equals(e.getMessage())){
            throw new ClientException("AlreadyExistingUsername", "该用户名已被使用", e);
        } else {
            throw new ServerException("UnknownError", "服务器发生未知错误", e);
        }
    }

    /**
     * 处理审核错误
     */
    private Response handleProcessingError(Exception e) {
        if("InvalidStatus".equals(e.getMessage())) {
            return buildErrorResponse("无效的状态值", Response.Status.BAD_REQUEST);
        } else if("NoSuchUser".equals(e.getMessage())) {
            return buildErrorResponse("找不到该用户", Response.Status.BAD_REQUEST);
        } else {
            return buildErrorResponse("服务器处理错误", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}