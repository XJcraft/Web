/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('adminTagCtrl', ['app', '$scope', '$routeParams',
    function (app, $scope, $routeParams) {
        var originData = {},
            restAPI = app.restAPI.tag,
            myConf = app.myConf,
            locale = app.locale,
            params = {
                ID: 'index',
                p: $routeParams.p,
                s: $routeParams.s || myConf.pageSize(null, 'tagAdmin', 20)
            },
            tagList = [{
                _id: '',
                articles: 0,
                tag: '',
                users: 0
            }];

        function initTagList(list) {
            originData = app.union(app.union(tagList), list);
            $scope.tagList = app.union(originData);
            $scope.parent.editSave = !! app.checkDirty(tagList, originData, $scope.tagList);
        }

        $scope.parent = {
            editSave: false
        };
        $scope.pagination = {};
        $scope.removeTag = null;

        $scope.removeTagModal = {
            confirmBtn: locale.BTN_TEXT.confirm,
            confirmFn: function () {
                var tag = $scope.removeTag;
                restAPI.remove({
                    ID: tag._id
                }, function () {
                    app.findItem($scope.tagList, function (x, i, list) {
                        if (x._id === tag._id) {
                            list.splice(i, 1);
                            app.toast.success(locale.TAG.removed + tag.tag, locale.RESPONSE.success);
                            return true;
                        }
                    });
                    initTagList($scope.tagList);
                    $scope.removeTag = null;
                });
                return true;
            },
            cancelBtn: locale.BTN_TEXT.cancel,
            cancelFn: function () {
                $scope.removeTag = null;
                return true;
            }
        };

        $scope.checkTag = function (scope, model) {
            var tag = app.toStr(model.$value);
            return !/[,，、]/.test(tag);
        };
        $scope.checkTagMin = function (scope, model) {
            return app.filter('length')(model.$value) >= 3;
        };
        $scope.reset = function () {
            $scope.tagList = app.union(originData);
        };
        $scope.remove = function (tag) {
            $scope.removeTag = tag;
            $scope.removeTagModal.modal(true);
        };
        $scope.submit = function () {
            var list = [{
                _id: '',
                tag: ''
            }];
            if (app.validate($scope)) {
                var data = app.checkDirty(tagList, originData, $scope.tagList);
                if (app.isEmpty(data)) {
                    app.toast.info(locale.TAG.noUpdate);
                } else {
                    data = app.intersect(list, data);
                    restAPI.save({
                        ID: 'admin'
                    }, {
                        data: data
                    }, function (result) {
                        var updated = [];
                        app.each(data, function (x) {
                            var tag = result.data[x._id];
                            if (!tag) {
                                app.findItem($scope.tagList, function (y, i, list) {
                                    if (x._id === y._id) {
                                        list.splice(i, 1);
                                        return true;
                                    }
                                });
                            }
                        });
                        app.each(result.data, function (x) {
                            app.findItem($scope.tagList, function (y, i, list) {
                                if (x._id === y._id) {
                                    app.union(y, x);
                                    updated.push(x.tag);
                                    return true;
                                }
                            });
                        });
                        initTagList($scope.tagList);
                        app.toast.success(locale.TAG.updated + updated.join(', '), locale.RESPONSE.success);
                    });
                }
            }
        };

        $scope.$watch('tagList', function (value) {
            $scope.parent.editSave = !! app.checkDirty(tagList, originData, value);
        }, true);
        app.promiseGet(params, restAPI).then(function (data) {
            var pagination = data.pagination || {};
            pagination.path = app.location.path();
            pagination.pageSize = myConf.pageSize(pagination.pageSize, 'tagAdmin');
            pagination.sizePerPage = [20, 50, 100];
            $scope.pagination = pagination;
            initTagList(data.data);
        });
    }
]);