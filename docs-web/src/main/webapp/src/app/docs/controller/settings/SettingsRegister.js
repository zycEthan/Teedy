'use strict';

/**
 * 注册申请管理控制器
 */
angular.module('docs').controller('SettingsRegister', function($scope, $state, Restangular, $dialog, $translate, $timeout) {
    // 初始化变量
    $scope.applications = [];
    $scope.loading = true;

    // 加载注册申请列表
    $scope.loadApplications = function() {
        $scope.loading = true;
        // 使用正确的API路径
        Restangular.one("registerUser/list").get().then(function(data) {
            // 调整数据结构匹配
            $scope.applications = data.register_users ? data.register_users : [];
            $scope.loading = false;
        }, function() {
            $scope.loading = false;
        });
    };

    // 批准注册申请
    $scope.approve = function(application) {
        var msg = $translate.instant('settings.register.approve_message', { username: application.username });

        if (confirm(msg)) {
            Restangular.one("registerUser/operate").post("", {
                username: application.username,
                new_status: 1
            }).then(function(data) {
                if (data.status === 'ok') {
                    application.operated_time = data.operated_time;
                    $scope.successApprove = true;
                    $timeout(function() {
                        $scope.successApprove = false;
                        // 刷新列表，移除已处理的申请
                        $scope.loadApplications();
                    }, 2000);
                }
            }, function(error) {
                $scope.errorApprove = true;
                $timeout(function() {
                    $scope.errorApprove = false;
                }, 2000);
            });
        }
    };

    // 拒绝注册申请
    $scope.reject = function(application) {
        var msg = $translate.instant('settings.register.reject_message', { username: application.username });

        if (confirm(msg)) {
            Restangular.one("registerUser/operate").post("", {
                username: application.username,
                new_status: 2
            }).then(function(data) {
                if (data.status === 'ok') {
                    application.operated_time = data.operated_time;
                    $scope.successReject = true;
                    $timeout(function() {
                        $scope.successReject = false;
                        // 刷新列表，移除已处理的申请
                        $scope.loadApplications();
                    }, 2000);
                }
            }, function(error) {
                $scope.errorReject = true;
                $timeout(function() {
                    $scope.errorReject = false;
                }, 2000);
            });
        }
    };

    // 初始加载
    $scope.loadApplications();
});