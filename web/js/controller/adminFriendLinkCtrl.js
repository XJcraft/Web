/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('adminFriendLinkCtrl', ['app', '$scope',
    function (app, $scope) {
        var restAPI = app.restAPI.friendLink,
            originData = {},
            myConf = app.myConf,
            friendLinkList = [{
                _id: '',
                name: '',
                imgUrl: '',
                url: '',
                orderBy: 0,
                date: 0,
                display: false
            }];
        $scope.editFriendLink = {};
        $scope.parent = {
            editSave: false,
            isSelectAll: false
        };
        var getFriendList = function () {
            app.promiseGet({
                OP: 'admin'
            }, restAPI).then(function (data) {
                var pagination = data.pagination || {};
                pagination.path = app.location.path();
                pagination.pageSize = myConf.pageSize(pagination.pageSize, 'friendLinkAdmin');
                pagination.sizePerPage = [20, 100, 200];
                $scope.pagination = pagination;
                initFriendLinkList(data.data);
            });
        };
        var editModal = {
            show: function () {
                $('#frendlink-modal').modal('show');
            },
            hide: function () {
                $('#frendlink-modal').modal('hide');
            },
            cancel: function () {
                $scope.cancel();
            },
            init: function () {
                $('#frendlink-modal').on('shown.bs.modal', function (event) {
                    editModal.modalStatus = true;
                });
                $('#frendlink-modal').on('hidden.bs.modal', function (event) {
                    if (editModal.modalStatus) {
                        editModal.cancel();
                    }
                    editModal.modalStatus = false;
                });
            }
        };
        editModal.init();
        $scope.selectAll = function () {
            app.each($scope.friendLinkList, function (x) {
                x.isSelect = $scope.parent.isSelectAll;
            });
        };
        $scope.getfriendLinkModal = app.getFile.html('admin-friendLink-edit.html');
        $scope.postAdd = function () {
            editModal.show();
        };
        $scope.postUpdate = function () {
            var selected = app.findItem($scope.friendLinkList, function (x, i) {
                return x.isSelect;
            });
            if (!selected) {
                app.toast.error('请选择一个!');
                return;
            }
            $scope.editFriendLink = app.union(selected);
            editModal.show();
        };
        $scope.toggleDisplay = function (friendLink) {
            friendLink.display = !friendLink.display;
            restAPI.save({
                ID: friendLink._id,
                OP: 'display'
            }, {display: friendLink.display ? 1 : 0}, function (data) {
                if (!data.ack) {
                    friendLink.display = !friendLink.display;
                }
            });
        };
        $scope.postDelete = function () {
            var selected = [];
            app.each($scope.friendLinkList, function (x, i) {
                if (x.isSelect) {
                    selected.push(x);
                }
            });
            if (!selected) {
                app.toast.error('请至少选择一个!');
                return;
            }
            var ids = [];
            for (var s in selected) {
                ids.push(selected[s]._id);
            }
            restAPI.save({
                ID: 'delete'
            }, {fids: ids.join(',')}, function (data) {
                getFriendList();
            });
        };
        $scope.cancel = function () {
            editModal.hide();
            $scope.editFriendLink = {};

        };
        $scope.save = function () {
            var data = app.union($scope.editFriendLink);
            var ID = data._id;
            restAPI.save({
                ID: ID || 'index',
                OP: ID && 'edit'
            }, data, function (data) {
                if (ID) {
                    app.each($scope.friendLinkList, function (x, i) {
                        if (x._id == ID)
                            app.union(x, data.data);
                    });
                } else {
                    $scope.friendLinkList.push(data.data);
                }
            });
            $scope.cancel();
            $scope.editFriendLink = {};
        };
        function initFriendLinkList(list) {
            originData = app.intersect(app.union(friendLinkList), list);
            $scope.friendLinkList = app.union(originData);
            $scope.parent.editSave = !!app.checkDirty(friendLinkList, originData, $scope.friendLinkList);
        }

        getFriendList();
    }]);