'use strict';

/**
 * Login controller.
 */
angular.module('docs').controller('Login', function(Restangular, $scope, $rootScope, $state, $stateParams, $dialog, User, $translate, $uibModal) {
  $scope.codeRequired = false;

  // Get the app configuration
  Restangular.one('app').get().then(function(data) {
    $rootScope.app = data;
  });

  // Login as guest
  $scope.loginAsGuest = function() {
    $scope.user = {
      username: 'guest',
      password: ''
    };
    $scope.login();
  };

  // Login
  $scope.login = function() {
    User.login($scope.user).then(function() {
      User.userInfo(true).then(function(data) {
        $rootScope.userInfo = data;
      });

      if($stateParams.redirectState !== undefined && $stateParams.redirectParams !== undefined) {
        $state.go($stateParams.redirectState, JSON.parse($stateParams.redirectParams))
            .catch(function() {
              $state.go('document.default');
            });
      } else {
        $state.go('document.default');
      }
    }, function(data) {
      if (data.data.type === 'ValidationCodeRequired') {
        // A TOTP validation code is required to login
        $scope.codeRequired = true;
      } else {
        // Login truly failed
        var title = $translate.instant('login.login_failed_title');
        var msg = $translate.instant('login.login_failed_message');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }
    });
  };

  // Password lost
  $scope.openPasswordLost = function () {
    $uibModal.open({
      templateUrl: 'partial/docs/passwordlost.html',
      controller: 'ModalPasswordLost'
    }).result.then(function (username) {
      if (username === null) {
        return;
      }

      // Send a password lost email
      Restangular.one('user').post('password_lost', {
        username: username
      }).then(function () {
        var title = $translate.instant('login.password_lost_sent_title');
        var msg = $translate.instant('login.password_lost_sent_message', { username: username });
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }, function () {
        var title = $translate.instant('login.password_lost_error_title');
        var msg = $translate.instant('login.password_lost_error_message');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      });
    });
  };

  // Register
  $scope.register = function() {
    // 打开注册表单对话框
    var regModal = $uibModal.open({
      templateUrl: 'partial/docs/register.html',
      controller: 'ModalRegister'
    });

    // 处理表单提交结果
    regModal.result.then(function(userData) {
      // 用户取消操作处理
      if(userData === null) {
        return;
      }

      // 向服务器提交注册数据
// 向服务器提交注册数据
      Restangular.one("registerUser/register").put(userData)
          .then(
              // 提交成功回调
              function() {
                displayMessage(
                    'login.register_success_title',
                    'login.register_success_message'
                );
              },
              // 提交失败回调
              function(response) {
                // 获取具体错误信息
                var errorKey = 'login.register_submit_error_message';
                if (response.data && response.data.type) {
                  if (response.data.type === 'AlreadyExistingUsername') {
                    errorKey = 'login.register_username_exists';
                  } else if (response.data.type === 'AlreadyRegisteringUsername') {
                    errorKey = 'login.register_username_taken';
                  }
                }
                displayMessage(
                    'login.register_submit_title',
                    errorKey
                );
              }
          );
    });

    // 内部函数：显示提示对话框
    function displayMessage(titleKey, msgKey) {
      var title = $translate.instant(titleKey);
      var message = $translate.instant(msgKey);
      var buttons = [{
        result: 'ok',
        label: $translate.instant('ok'),
        cssClass: 'btn-primary'
      }];

      $dialog.messageBox(title, message, buttons);
    }
  };
});