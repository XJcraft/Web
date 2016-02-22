/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('userListCtrl', ['app', '$scope', '$routeParams',
    function (app, $scope, $routeParams) {
        var restAPI = app.restAPI.user,
            myConf = app.myConf,
            locale = app.locale,
            params = {
                ID: $routeParams.ID && 'U' + $routeParams.ID || $routeParams.OP,
                OP: $routeParams.OP || 'fans',
                p: $routeParams.p,
                s: $routeParams.s || myConf.pageSize(null, 'user', 20)
            };

        $scope.parent = {
            title: ''
        };

        app.promiseGet(params, restAPI, app.param(params), app.cache.list).then(function (data) {
            var pagination = data.pagination || {};

            pagination.path = app.location.path();
            pagination.pageSize = myConf.pageSize(pagination.pageSize, 'user');
            $scope.pagination = pagination;
            app.each(data.data, function (x) {
                app.checkFollow(x);
            });
            if (!$routeParams.ID) {
                $scope.parent.title = locale.HOME[params.OP];
            } else {
                $scope.parent.title = data.user.name + locale.USER[params.OP];
            }
            $scope.userList = data.data;
        });
    }
]);