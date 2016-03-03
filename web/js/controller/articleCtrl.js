/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('articleCtrl', ['app', '$scope', '$routeParams', 'getList', 'getMarkdown',
    function (app, $scope, $routeParams, getList, getMarkdown) {
        var ID = 'A' + $routeParams.ID,
            myConf = app.myConf,
            locale = app.locale,
            global = app.rootScope.global,
            filter = app.filter,
            lengthFn = filter('length'),
            cutTextFn = filter('cutText'),
            commentCache = app.cache.comment,
            listCache = app.cache.list,
            restAPI = app.restAPI.article,
            user = global.user || {};

        user = {
            _id: user._id,
            name: user.name,
            avatar: user.avatar
        };

        function checkArticleIs(article) {
            var _id = user._id || '-';
            if (!angular.isObject(article)) {
                return;
            }
            article.favorsList = article.favors?article.favors.split(','):[];
            article.opposesList = article.opposes?article.opposes.split(','):[];
            article.isAuthor = _id === article.author._id;
            article.isMark = !!app.findItem(article.markList, function (x) {
                return x._id === _id;
            });
            article.isFavor = !!app.findItem(article.favorsList, function (x) {
                return x ===_id;
            });
            article.isOppose = !!app.findItem(article.opposesList, function (x) {
                return x ===_id;
            });
            if(article.commentsList)
                app.each(article.commentsList, function (x) {
                    checkArticleIs(x);
                });
        }

        function checkLogin() {
            if (!global.isLogin) {
                app.toast.error(locale.USER.noLogin);
            }
            return global.isLogin;
        }

        function canPublish() {
            if (!global.canPublish) {
                app.toast.error(locale.USER.gag);
            }
            return global.canPublish;
        }

        function initReply() {
            var comment = $scope.comment,
                article = $scope.article;
            comment.replyToComment = '';
            comment.title = '评论：' + cutTextFn(article.title, global.TitleMaxLen - 9);
            comment.content = '';
            comment.refer = article._id;
            $scope.replyMoving.prependTo('#comments');
        }

        $scope.parent = {
            wmdPreview: false,
            contentBytes: 0,
            markdownHelp: ''
        };
        $scope.comment = {
            title: '',
            content: '',
            refer: '',
            replyToComment: ''
        };
        $scope.replyMoving = {};
        $scope.commentMoving = {};
        $scope.markdownModal = {
            title: locale.ARTICLE.markdown,
            cancelBtn: locale.BTN_TEXT.goBack
        };
        $scope.validateTooltip = app.union(app.rootScope.validateTooltip);
        $scope.validateTooltip.placement = 'bottom';
        // 删除确认modal
        $scope.removeCommentModal = {
            confirmBtn: locale.BTN_TEXT.confirm,
            confirmFn: function () {
                var comment = $scope.removeComment;
                app.restAPI.article.remove({
                    ID: comment._id
                }, function () {
                    app.findItem($scope.article.commentsList, function (x, i, list) {
                        if (x._id === comment._id) {
                            list.splice(i, 1);
                            $scope.article.comments = list.length;
                            app.toast.success(locale.ARTICLE.removed + comment.title, locale.RESPONSE.success);
                            return true;
                        }
                    });
                    $scope.removeComment = null;
                });
                return true;
            },
            cancelBtn: locale.BTN_TEXT.cancel,
            cancelFn: function () {
                $scope.removeComment = null;
                return true;
            }
        };
        $scope.remove = function (comment) {
            if (comment.isAuthor || global.isEditor) {
                $scope.removeComment = comment;
                $scope.removeCommentModal.modal(true);
            }
        };

        $scope.wmdHelp = function () {
            getMarkdown().success(function (data) {
                $scope.parent.markdownHelp = data;
                $scope.markdownModal.modal(true);
            });
        };
        $scope.wmdPreview = function () {
            $scope.parent.wmdPreview = !$scope.parent.wmdPreview;
            $scope.replyMoving.scrollIntoView(true);
        };
        $scope.checkContentMin = function (scope, model) {
            var length = lengthFn(model.$value);
            $scope.parent.contentBytes = length;
            return length >= global.ContentMinLen;
        };
        $scope.checkContentMax = function (scope, model) {
            return lengthFn(model.$value) <= global.ContentMaxLen;
        };
        $scope.reply = function (article) {
            var comment = $scope.comment;
            comment.refer = article._id;
            $scope.parent.wmdPreview = false;
            if (article._id === $scope.article._id) {
                //如果是回复原来的文章，则初始化reply
                initReply();
            } else {
                comment.replyToComment = article._id;
                comment.title = locale.ARTICLE.reply + cutTextFn(app.sanitize(article.content, 0), global.TitleMaxLen - 9);
                $scope.replyMoving.appendTo('#' + article._id);
            }
            $scope.replyMoving.scrollIntoView();
        };
        $scope.getComments2 = function(refer,to){

            if(to.commentsList){
                if ($scope.commentMoving.childrenOf('#' + to._id)) {
                    $scope.referComments = null;
                    $scope.commentMoving.appendTo('#comments');
                } else {
                    $scope.referComments = to.commentsList;
                    $scope.commentMoving.appendTo('#' + to._id);
                }
            }else if (to.comments>0){
                $scope.referComments = [];
                restAPI.save({
                    ID: 'comment'
                }, {
                    refer:refer,
                    replyToComment:to._id
                }, function (data) {
                    app.each(data.data, function (x) {
                        checkArticleIs(x);
                        commentCache.put(x._id, x);
                    });
                    to.commentsList = data.data;
                    if ($scope.commentMoving.childrenOf('#' + to._id)) {
                        $scope.commentMoving.appendTo('#comments');
                        $scope.referComments = null;
                    } else {
                        $scope.referComments = data.data;
                        $scope.commentMoving.appendTo('#' + to._id);
                    }
                });
            }
        };
     /*   $scope.getComments = function (idArray, to) {
            var idList = [],
                result = {};

            function getResult() {
                var list = [];
                app.each(idArray, function (x) {
                    if (result[x]) {//过滤出result
                        list.push(result[x]);
                    }
                });
                return list;
            }

            $scope.referComments = [];
            if (to && idArray && idArray.length > 0) {
                if ($scope.commentMoving.childrenOf('#' + to._id)) {
                    $scope.commentMoving.appendTo('#comments');
                    return;
                } else {
                    $scope.commentMoving.appendTo('#' + to._id);
                }
                app.each(idArray, function (x) {
                    var comment = commentCache.get(x);
                    if (comment) {
                        result[x] = comment;
                    } else {
                        idList.push(x);
                    }
                });
                $scope.referComments = getResult();
                if (idList.length > 0) {
                    restAPI.save({
                        ID: 'comment'
                    }, {
                        data: idList
                    }, function (data) {
                        app.each(data.data, function (x) {
                            checkArticleIs(x);
                            commentCache.put(x._id, x);
                            result[x._id] = x;
                        });
                        $scope.referComments = getResult();
                    });
                }
            }
        };*/
        $scope.highlight = function (article) {
            // this is todo
            article.status = article.status === 2 ? 0 : 2;
        };

        $scope.zding = function(article){
            if (checkLogin()) {
                restAPI.save({
                    ID: article._id,
                    OP: 'zding'
                }, {
                    zding: !article.zding
                },function(){
                    article.zding = !article.zding;
                });
            }
        };
        $scope.jing = function(article){
            if (checkLogin()) {
                restAPI.save({
                    ID: article._id,
                    OP: 'jing'
                }, {
                    jing: !article.jing
                },function(){
                    article.jing = !article.jing;
                });
            }
        };
        $scope.setMark = function (article) {
            /*if (checkLogin()) {
             restAPI.save({
             ID: article._id,
             OP: 'mark'
             }, {
             mark: !article.isMark
             }, function () {
             article.isMark = !article.isMark;
             if (article.isMark) {
             article.markList.push(user);
             } else {
             app.removeItem(article.markList, user._id);
             }
             app.toast.success(locale.ARTICLE[article.isMark ? 'marked' : 'unmarked']);
             });
             }*/
        };
        $scope.setFavor = function (article) {
            if (checkLogin()) {
                restAPI.save({
                    ID: article._id,
                    OP: 'favor'
                }, {
                    favor: !article.isFavor
                }, function () {
                    article.isFavor = !article.isFavor;
                    if (article.isFavor) {
                        article.favorsList.push(user._id);
                        app.removeItem(article.opposesList, user._id);
                        article.isOppose = false;
                    } else {
                        app.removeItem(article.favorsList, user._id);
                    }
                    app.toast.success(locale.ARTICLE[article.isFavor ? 'favored' : 'unfavored']);
                });
            }
        };
        $scope.setOppose = function (article) {
            if (checkLogin()) {
                restAPI.save({
                    ID: article._id,
                    OP: 'oppose'
                }, {
                    oppose: !article.isOppose
                }, function () {
                    article.isOppose = !article.isOppose;
                    if (article.isOppose) {
                        article.opposesList.push(user._id);
                        app.removeItem(article.favorsList, user._id);
                        article.isFavor = false;
                    } else {
                        app.removeItem(article.opposesList, user._id);
                    }
                    app.toast.success(locale.ARTICLE[article.isOppose ? 'opposed' : 'unopposed']);
                });
            }
        };
        $scope.submit = function () {
            if (checkLogin() && canPublish() && app.validate($scope)) {
                var data = app.union($scope.comment),
                    article = $scope.article;
                restAPI.save({
                    ID: article._id,
                    OP: 'comment'
                }, data, function (data) {
                    var comment = data.data,
                        replyToComment = $scope.comment.replyToComment;
                    //article.commentsList.unshift(comment);
                    article.comments += 1;
                    article.updateTime = Date.now();
                    comment.favorsList =[];
                    comment.opposesList =[];
                    //
                    if (replyToComment) {
                        app.findItem(article.commentsList, function (x, i, list) {
                            if (replyToComment === x._id) {
                                x.comments = x.comments||0;
                                x.comments++;
                                if(x.commentsList)
                                    x.commentsList.push(comment);
                                return true;
                            }
                        });
                    }else{
                        article.commentsList.unshift(comment);
                    }
                    commentCache.put(comment._id, comment);
                    initReply();
                });
            }
        };
        $scope.$on('genPagination', function (event, p, s) {
            event.stopPropagation();
            var params = {
                ID: ID,
                OP: 'comment',
                p: p,
                s: myConf.pageSize(s, 'comment', 10)
            };
            app.promiseGet(params, restAPI, app.param(params), listCache).then(function (data) {
                var pagination = data.pagination || {},
                    commentsList = data.data;
                pagination.pageSize = myConf.pageSize(pagination.pageSize, 'comment');
                $scope.pagination = pagination;
                app.each(commentsList, function (x) {
                    checkArticleIs(x);
                    commentCache.put(x._id, x);
                });
                $scope.article.commentsList = commentsList;
                app.anchorScroll.toView('#comments', true);
            });
        });

        // 获取文章
        app.promiseGet({
            ID: ID
        }, restAPI, ID, app.cache.article).then(function (data) {
            var pagination = data.pagination || {},
                article = data.data;
            pagination.pageSize = myConf.pageSize(pagination.pageSize, 'comments');
            checkArticleIs(article);
            app.each(article.commentsList, function (x) {
                commentCache.put(x._id, x);
            });
            global.title2 = article.title;
            $scope.pagination = pagination;
            $scope.article = article;//!
            initReply();

            app.promiseGet({
                ID: article.author._id,
                OP: 'article'
            }, app.restAPI.user, article.author._id, listCache).then(function (data) {
                var user = data.user,
                    author = $scope.article.author;
                app.checkFollow(user);
                // app.union(author, user);
                author.articlesList = data.data;
            });
        });
        /* getList('hots').then(function (data) {
         $scope.hotArticles = data.data.slice(0, 10);
         });*/
    }
]);