/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('userArticleCtrl', ['app', '$scope', '$routeParams',
    function (app, $scope, $routeParams) {
        var restAPI = app.restAPI.user,
            myConf = app.myConf,
            locale = app.locale,
            global = app.rootScope.global;

        function getArticleList() {
            var params = {
                ID: $routeParams.ID && 'U' + $routeParams.ID || $routeParams.OP,
                OP: $routeParams.OP || ($routeParams.ID ? 'article' : 'index'),
                p: $routeParams.p,
                s: $routeParams.s || myConf.pageSize(null, 'home', 20)
            };
            app.promiseGet(params, restAPI, app.param(params), app.cache.list).then(function (data) {
                var newArticles = 0,
                    pagination = data.pagination || {};

                pagination.path = app.location.path();
                pagination.pageSize = myConf.pageSize(pagination.pageSize, 'home');
                $scope.pagination = pagination;
                // $scope.user = data.user;
                if (!$routeParams.ID) {
                    var user = global.user || {};
                    app.each(data.data, function (x) {
                        if (data.readtimestamp > 0) {
                            x.read = x.updateTime < data.readtimestamp;
                            newArticles += !x.read;
                        }
                        x.isAuthor = x.author._id === user._id;
                    });
                    $scope.parent.title = params.OP !== 'index' ? locale.HOME[params.OP] : newArticles + locale.HOME.index + app.filter('date')(data.readtimestamp, 'medium')| '';
                } else {
                    $scope.parent.title = data.user.name + locale.USER[params.OP];
                    $scope.user = data.user;
                }
                $scope.articleList = data.data;
            });
        }

        $scope.parent = {
            sumModel: myConf.sumModel(null, 'index', false),
            title: ''
        };
        $scope.pagination = {};
        $scope.removeArticle = null;

        $scope.removeArticleModal = {
            confirmBtn: locale.BTN_TEXT.confirm,
            confirmFn: function () {
                var article = $scope.removeArticle;
                app.restAPI.article.remove({
                    ID: article._id
                }, function () {
                    app.findItem($scope.articleList, function (x, i, list) {
                        if (x._id === article._id) {
                            list.splice(i, 1);
                            app.toast.success(locale.ARTICLE.removed + article.title, locale.RESPONSE.success);
                            return true;
                        }
                    });
                    $scope.removeArticle = null;
                });
                return true;
            },
            cancelBtn: locale.BTN_TEXT.cancel,
            cancelFn: function () {
                $scope.removeArticle = null;
                return true;
            }
        };
        $scope.setListModel = function () {
            var parent = $scope.parent;
            parent.sumModel = myConf.sumModel(!parent.sumModel, 'home');
            myConf.pageSize(parent.sumModel ? 20 : 10, 'home');
            app.location.search({});
        };
        $scope.remove = function (article) {
            if (article.isAuthor || global.isEditor) {
                $scope.removeArticle = article;
                $scope.removeArticleModal.modal(true);
            }
        };

        getArticleList();
    }
]);