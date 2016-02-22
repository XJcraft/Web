/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('tagCtrl', ['app', '$scope', '$routeParams', 'getList',
    function (app, $scope, $routeParams, getList) {
        var restAPI = app.restAPI.tag,
            myConf = app.myConf,
            params = {
                p: $routeParams.p,
                s: $routeParams.s || myConf.pageSize(null, 'tag', 50)
            };

        app.rootScope.global.title2 = app.locale.TAG.title;
        $scope.parent = {
            getTpl: app.getFile.html('index-tag.html')
        };
        $scope.pagination = {};

        app.promiseGet(params, restAPI, app.param(params), app.cache.list).then(function (data) {
            var pagination = data.pagination || {};
            pagination.path = app.location.path();
            pagination.pageSize = myConf.pageSize(pagination.pageSize, 'tag');
            pagination.sizePerPage = [50, 100, 200];
            $scope.pagination = pagination;
            $scope.tagList = data.data;
        });

        getList('comment').then(function (data) {
            data = app.union(data.data);
            app.each(data, function (x, i) {
                x.content = app.filter('cutText')(x.content, 180);
            });
            $scope.hotComments = data.slice(0, 6);
        });
    }
]);