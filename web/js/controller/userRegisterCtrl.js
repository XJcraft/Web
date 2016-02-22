/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('userRegisterCtrl', ['app', '$scope',
    function (app, $scope) {
        var filter = app.filter,
            lengthFn = filter('length'),
            global = app.rootScope.global;

        app.clearUser();
        global.title2 = app.locale.USER.register;
        $scope.user = {
            name: '',
            email: '',
            passwd: '',
            passwd2: ''
        };

        $scope.checkName = function (scope, model) {
            return filter('checkName')(model.$value);
        };
        $scope.checkMin = function (scope, model) {
            return lengthFn(model.$value) >= 1;
        };
        $scope.checkMax = function (scope, model) {
            return lengthFn(model.$value) <= 15;
        };
        $scope.submit = function () {
            var user = $scope.user;
            if (app.validate($scope)) {
                var data = {
                    name: user.name
                    // email: user.email
                };
                data.passwd = app.CryptoJS.SHA256(user.passwd).toString();
                //data.passwd = app.CryptoJS.HmacSHA256(data.passwd, 'Xjcraft').toString();

                app.restAPI.user.save({
                    ID: 'register'
                }, data, function (data) {
                    app.rootScope.global.user = data.data;
                    app.checkUser();
                    $scope.$destroy();
                    app.location.path('/home');
                });
            }
        };
    }
]);