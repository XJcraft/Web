/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen
    .controller('indexCtrl', ['app', '$scope', '$routeParams', 'getList',
        function (app, $scope, $routeParams, getList) {
            var ID = '',
                restAPI = app.restAPI.article,
                myConf = app.myConf,
                global = app.rootScope.global;

            function checkRouteParams() {
                var path = app.location.path().slice(1).split('/');
                if ($routeParams.TAG || (/^T[0-9A-Za-z]{3,}$/).test(path[0])) {
                    restAPI = app.restAPI.tag;
                    $scope.other._id = path[0];
                    $scope.other.title = $routeParams.TAG || path[0];
                    $scope.parent.viewPath = '';
                } else {
                    restAPI = app.restAPI.article;
                    $scope.parent.viewPath = path[0] || 'latest';
                }
                ID = $routeParams.TAG || path[0] || 'latest';
            }

            function getArticleList() {
                var params = {
                    ID: ID,
                    p: $routeParams.p,
                    s: $routeParams.s || myConf.pageSize(null, 'index', 10)
                };

                app.promiseGet(params, restAPI, app.param(params), app.cache.list).then(function (data) {
                    var pagination = data.pagination || {};
                    if (data.tag) {
                        $scope.other.title = data.tag.tag;
                        $scope.other._id = data.tag._id;
                    }
                    pagination.path = app.location.path();
                    pagination.pageSize = myConf.pageSize(pagination.pageSize, 'index');
                    $scope.pagination = pagination;
                    $scope.articleList = data.data;
                });
            }

            global.title2 = global.description;
            $scope.parent = {
                getTpl: app.getFile.html('index-article.html'),
                viewPath: 'latest',
                sumModel: myConf.sumModel(null, 'index', false)
            };
            $scope.other = {};
            $scope.pagination = {};

            $scope.setListModel = function () {
                var parent = $scope.parent;
                parent.sumModel = myConf.sumModel(!parent.sumModel, 'index');
                myConf.pageSize(parent.sumModel ? 20 : 10, 'index');
                app.location.search({});
            };

            checkRouteParams();
            getArticleList();
        }
    ]);