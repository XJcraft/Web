/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('userLoginCtrl', ['app', '$scope','$routeParams',
    function (app, $scope,$routeParams) {
        app.clearUser();
        app.rootScope.global.title2 = app.locale.USER.login;
        $scope.login = {
            logauto: true,
            logname: '',
            logpwd: ''
        };
        $scope.reset = {
            title: '',
            type: ''
        };

        $scope.submit = function () {
            if (app.validate($scope)) {
                var data = app.union($scope.login);
                data.logtime = Date.now() - app.timeOffset;
                data.p = data.logpwd;
                data.logpwd = app.CryptoJS.SHA256(data.logpwd).toString();
                //data.logpwd = app.CryptoJS.HmacSHA256(data.logpwd, 'jsGen').toString();
                // data.logpwd = app.CryptoJS.HmacSHA256(data.logpwd, data.logname + ':' + data.logtime).toString();

                app.restAPI.user.save({
                    ID: 'login'
                }, data, function (data) {
                    app.rootScope.global.user = data.data;
                    app.checkUser();
                    $scope.$destroy();
                    app.location.search({}).path($routeParams.url || '/home');
                }, function (data) {
                    $scope.reset.type = data.error.name;
                    $scope.reset.title = app.locale.RESET[data.error.name];
                });
            }
        };
    }
]);