/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */

jsGen.controller('userEditCtrl', ['app', '$scope',
    function (app, $scope) {
        var originData = {},
            tagsArray = [],
            locale = app.locale,
            filter = app.filter,
            global = app.rootScope.global,
            lengthFn = filter('length'),
            user = {
                avatar: '',
                name: '',
                sex: '',
                email: '',
                qq:'',
                descp: '',
                passwd: '',
                tagsList: ['']
            };

        function initUser() {
            originData = app.union(global.user);
            app.each(originData.tagsList, function (x, i, list) {
                list[i] = x.tag;
            });
            originData = app.intersect(app.union(user), originData);
            $scope.user = app.union(originData);
            app.checkDirty(user, originData, $scope.user);
        }

        $scope.sexArray = ['male', 'female'];

        $scope.checkName = function (scope, model) {
            //return filter('checkName')(model.$value);
            return true;
        };
        $scope.checkMin = function (scope, model) {
            return lengthFn(model.$value) >= 5;
        };
        $scope.checkMax = function (scope, model) {
            return lengthFn(model.$value) <= 15;
        };
        $scope.checkDesc = function (scope, model) {
            return lengthFn(model.$value) <= global.SummaryMaxLen;
        };
        $scope.checkTag = function (scope, model) {
            var list = model.$value || '';
            list = angular.isString(list) ? list.split(/[,，、]/) : list;
            return list.length <= global.UserTagsMax;
        };
        $scope.checkPwd = function (scope, model) {
            var passwd = model.$value || '';
            return passwd === ($scope.user.passwd || '');
        };
        $scope.getTag = function (tag) {
            var tagsList = $scope.user.tagsList;
            if (tagsList.indexOf(tag.tag) < 0 && tagsList.length < global.UserTagsMax) {
                $scope.user.tagsList = tagsList.concat(tag.tag); // 此处push方法不会更新tagsList视图
            }
        };
        $scope.reset = function () {
            $scope.user = app.union(originData);
        };
        $scope.verifyEmail = function () {
            var verify = app.restAPI.user.save({
                ID: 'reset'
            }, {
                request: 'role'
            }, function () {
                app.toast.success(locale.RESET.email, locale.RESPONSE.success);
            });
        };
        $scope.submit = function () {
            var data = app.union($scope.user);
            if (app.validate($scope)) {
                data = app.checkDirty(user, originData, data);
                if (app.isEmpty(data)) {
                    app.toast.info(locale.USER.noUpdate);
                } else {
                    if (data.passwd) {
                        data.passwd = app.CryptoJS.SHA256(data.passwd).toString();
                        // data.passwd = app.CryptoJS.HmacSHA256(data.passwd, 'jsGen').toString();
                    }
                    /*  if (data.email) {
                     app.restAPI.user.save({
                     ID: 'reset'
                     }, {
                     email: data.email,
                     request: 'email'
                     }, function () {
                     app.toast.success(locale.USER.email, locale.RESPONSE.success);
                     });
                     delete data.email;
                     }*/
                    if (!app.isEmpty(data)) {
                        app.restAPI.user.save({}, data, function (data) {
                            app.union(global.user, data.data);
                            initUser();
                            app.toast.success(locale.USER.updated, locale.RESPONSE.success);
                        });
                    } else {
                        initUser();
                    }
                }
            }
        };

        $scope.$watchCollection('user', function (value) {
            app.checkDirty(user, originData, value);
        });
        initUser();
    }
]);