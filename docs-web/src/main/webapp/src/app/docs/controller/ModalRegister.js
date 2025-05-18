'use strict';

/**
 * 用户注册表单控制器
 */
angular.module('docs').controller('ModalRegister', function($scope, $uibModalInstance) {
    // 初始化申请数据
    $scope.user = {
        username: '',
        email: '',
        password: '',
        passwordConfirm: '',
        storage: 10000 // 默认存储空间 1GB
    };

    // 错误信息容器
    $scope.formErrors = {};

    // 提交状态标志
    $scope.isProcessing = false;

    /**
     * 提交注册申请
     */
    $scope.submitRegistration = function() {
        // 验证表单数据
        if (!validateForm()) {
            return;
        }

        // 设置处理中状态
        $scope.isProcessing = true;

        // 准备提交数据
        var applicationData = {
            username: $scope.user.username,
            password: $scope.user.password,
            email: $scope.user.email,
            storage: $scope.user.storage
        };

        // 关闭对话框并返回数据
        $uibModalInstance.close(applicationData);
    };

    /**
     * 取消注册流程
     */
    $scope.cancelRegistration = function() {
        $uibModalInstance.close(null);
    };

    /**
     * 表单验证
     */
    /**
     * 表单验证
     */
    function validateForm() {
        // 重置错误信息
        $scope.formErrors = {};

        // 验证用户名
        if (!$scope.user.username || $scope.user.username.length < 3 || $scope.user.username.length > 50) {
            $scope.formErrors.username = '用户名长度必须在3到50个字符之间';
            return false;
        }

        if (!/^[a-zA-Z0-9_@.-]*$/.test($scope.user.username)) {
            $scope.formErrors.username = '用户名只能包含字母、数字和特殊字符(_@.-)';
            return false;
        }

        // 验证密码
        if (!$scope.user.password || $scope.user.password.length < 8 || $scope.user.password.length > 50) {
            $scope.formErrors.password = '密码长度必须在8到50个字符之间';
            return false;
        }

        // 验证密码确认
        if ($scope.user.password !== $scope.user.passwordConfirm) {
            $scope.formErrors.passwordConfirm = '两次输入的密码不一致';
            return false;
        }

        // 验证邮箱
        if (!$scope.user.email || !isValidEmail($scope.user.email)) {
            $scope.formErrors.email = '请输入有效的电子邮箱地址';
            return false;
        }

        // 验证存储空间
        if (!$scope.user.storage || isNaN($scope.user.storage) || $scope.user.storage <= 0) {
            $scope.formErrors.storage = '请输入有效的存储空间大小';
            return false;
        }

        return true;
    }

    /**
     * 邮箱格式验证
     */
    function isValidEmail(email) {
        var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
});