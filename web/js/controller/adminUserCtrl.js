/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('adminUserCtrl', ['app', '$scope', '$routeParams',
    function (app, $scope, $routeParams) {
        var originData = {},
            restAPI = app.restAPI.user,
            myConf = app.myConf,
            locale = app.locale,
            params = {
                ID: 'admin',
                p: $routeParams.p,
                s: $routeParams.s || myConf.pageSize(null, 'userAdmin', 20)
            },
            userList = [{
                _id: '',
                name: '',
                locked: false,
                email: '',
                role: 0,
                score: 0,
                date: 0,
                lastLoginDate: 0
            }];

        function initUserList(list) {
            originData = app.intersect(app.union(userList), list);
            $scope.userList = app.union(originData);
            $scope.parent.editSave = !! app.checkDirty(userList, originData, $scope.userList);
        }

        $scope.parent = {
            editSave: false,
            isSelectAll: false
        };
        $scope.pagination = {};
        $scope.roleArray = [0, 1, 2, 3, 4, 5];

        $scope.selectAll = function () {
            app.each($scope.userList, function (x) {
                x.isSelect = $scope.parent.isSelectAll;
            });
        };
        $scope.reset = function () {
            $scope.userList = app.union(originData);
        };
        $scope.submit = function () {
            var list = [{
                _id: '',
                locked: false,
                role: 0
            }];
            if (app.validate($scope)) {
                var data = app.checkDirty(userList, originData, $scope.userList);
                if (app.isEmpty(data)) {
                    app.toast.info(locale.USER.noUpdate);
                } else {
                    data = app.intersect(list, data);
                    restAPI.save({
                        ID: 'admin'
                    }, {
                        data: data
                    }, function (data) {
                        var updated = [];
                        app.each(data.data, function (x) {
                            app.findItem($scope.userList, function (y) {
                                if (x._id === y._id) {
                                    app.union(y, x);
                                    updated.push(x.name);
                                    return true;
                                }
                            });
                        });
                        initUserList($scope.userList);
                        app.toast.success(locale.USER.updated + updated.join(', '), locale.RESPONSE.success);
                    });
                }
            }
        };
        $scope.$watch('userList', function (value) {
            $scope.parent.editSave = !! app.checkDirty(userList, originData, value);
        }, true);
        app.promiseGet(params, restAPI).then(function (data) {
            var pagination = data.pagination || {};
            pagination.path = app.location.path();
            pagination.pageSize = myConf.pageSize(pagination.pageSize, 'userAdmin');
            pagination.sizePerPage = [20, 100, 200];
            $scope.pagination = pagination;
            initUserList(data.data);
        });
    }
]);