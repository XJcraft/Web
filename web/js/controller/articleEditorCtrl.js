/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('articleEditorCtrl', ['app', '$scope', '$routeParams',  'getMarkdown',
    function (app, $scope, $routeParams, getMarkdown) {
        var oldArticle,
            ID = $routeParams.ID && 'A' + $routeParams.ID,
            toStr = app.toStr,
            locale = app.locale,
            global = app.rootScope.global,
            filter = app.filter,
            upyun = app.upyun,
            lengthFn = filter('length'),
            cutTextFn = filter('cutText'),
            restAPI = app.restAPI.article,
            articleCache = app.cache.article,
            article = {
                title: '',
                content: '',
                refer: '',
                tagsList: []
            },
            originData = app.union(article);

        if (!global.isLogin) {
            return app.location.search({}).path('/');
        }

        function initArticle(data) {
            originData = app.union(article);
            if (data) {
                data = app.union(data);
                app.each(data.tagsList, function (x, i, list) {
                    list[i] = x.tag;
                });
                data.refer = data.refer && data.refer.url;
                app.intersect(originData, data);
                $scope.article = app.union(originData);
                app.checkDirty(article, originData, $scope.article);
            } else {
                $scope.article = {};
                app.each(article, function (value, key) {
                    $scope.article[key] = app.store.get('article.' + key) || value;
                });
            }
            preview(data);
        }

        function preview(value) {
            var parent = $scope.parent,
                article = $scope.article;
            if (value) {
                parent.title = locale.ARTICLE.preview + toStr(article.title);
                parent.content = article.content;
            } else {
                getMarkdown().success(function (data) {
                    parent.title = locale.ARTICLE.markdown;
                    parent.content = data;
                });
            }
        }

        global.title2 = app.locale.ARTICLE.title;
        $scope.parent = {
            edit: !! ID,
            wmdPreview: true,
            contentBytes: 0,
            titleBytes: 0,
            title: '',
            content: ''
        };
        var baseUrl = global.cloudDomian || global.url ||window.location.origin;
        $scope.uploaderOptions = {
            scope: $scope,
            //allowFileType: upyun.allowFileType,
            allowFileType: '|jpg|png|jpeg|bmp|gif|',
            //url: upyun.url,
            url:'/api/upload',
            baseUrl: baseUrl,
            // policy: upyun.policy,
            //signature: upyun.signature,
            clickImage: function (file) {
                $scope.article.content += '\n' + '![' + file.name + '](' + file.url + ')\n';
            }
        };

        $scope.validateTooltip = app.union(app.rootScope.validateTooltip);
        $scope.validateTooltip.placement = 'bottom';
        $scope.store = function (key) {
            var value = $scope.article[key];
            app.store.set('article.' + key, value);
        };
        $scope.checkTitleMin = function (scope, model) {
            var length = lengthFn(model.$value);
            $scope.parent.titleBytes = length;
            if ($scope.parent.wmdPreview) {
                $scope.parent.title = locale.ARTICLE.preview + app.sanitize(model.$value, 0);
            }
            return length >= global.TitleMinLen;
        };
        $scope.checkTitleMax = function (scope, model) {
            return lengthFn(model.$value) <= global.TitleMaxLen;
        };
        $scope.checkContentMin = function (scope, model) {
            var length = lengthFn(model.$value);
            $scope.parent.contentBytes = length;
            if ($scope.parent.wmdPreview) {
                $scope.parent.content = model.$value;
            }
            return length >= global.ContentMinLen;
        };
        $scope.checkContentMax = function (scope, model) {
            return lengthFn(model.$value) <= global.ContentMaxLen;
        };
        $scope.checkTag = function (scope, model) {
            var list = model.$value || '';
            list = angular.isString(list) ? list.split(/[,，、]/) : list;
            return list.length <= global.ArticleTagsMax;
        };
        $scope.getTag = function (tag) {
            var tagsList = $scope.article.tagsList;
            if (tagsList.indexOf(tag.tag) < 0 && tagsList.length < global.ArticleTagsMax) {
                $scope.article.tagsList = tagsList.concat(tag.tag); // 此处push方法不会更新tagsList视图
                $scope.store('tagsList');
            }
        };
        $scope.wmdPreview = function () {
            var parent = $scope.parent;
            parent.wmdPreview = !parent.wmdPreview;
            preview(parent.wmdPreview);
        };

        $scope.submit = function () {
            var data = app.union($scope.article);
            if (app.validate($scope)) {
                if (app.checkDirty(article, originData, data)) {
                    data.title = app.sanitize(data.title, 0);
                    restAPI.save({
                        ID: ID || 'index',
                        OP: ID && 'edit'
                    }, data, function (data) {
                        var article = data.data;

                        if (oldArticle) {
                            delete article.commentsList;
                            article = data.data = app.union(oldArticle.data, article);
                        }
                        articleCache.put(article._id, data);
                        initArticle(article);
                        app.toast.success(locale.ARTICLE[ID ? 'updated' : 'added'] + article.title);
                        var timing = app.timing(null, 1000, 2);
                        timing.then(function () {
                            app.location.search({}).path('/' + article._id);
                        });
                        app.store.clear();
                    });
                } else {
                    app.toast.info(locale.ARTICLE.noUpdate);
                }
            }
        };

        if (!app.store.enabled) {
            $scope.$watchCollection('article', function (value) {
                app.checkDirty(article, originData, value);
            });
        }

        if (ID) {
            oldArticle = articleCache.get(ID);
            app.promiseGet({
                ID: ID
            }, restAPI, ID, articleCache).then(function (data) {
                initArticle(data.data);
            });
        } else {
            initArticle();
        }
    }
]);