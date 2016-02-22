/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('userSkinCtrl', ['app', '$scope', 'FileUploader', 'applyFn', function (app, $scope, FileUploader, applyFn) {
    var restAPI = app.restAPI.skin,
        global = app.rootScope.global,
        playerId = global.user.playerId,
        info = $scope.info = {
            inputSkin:global.user.playerSkin || '',
            skin: {}, cloak: {}
        };

    $scope.parent = {
        title: '我的皮肤'
    };
    if (!global.user.playerId) {
        app.toast.error('未绑定游戏id!');
        return;
    }
    var skinNameModal = $scope.skinNameModal = {
        confirmBtn: '确定',
        cancelBtn: '取消',
        confirmFn: function () {
            restAPI.save({
                ID: playerId,
                OP: 'playerSkin'
            }, {name: info.inputSkin}, function (data) {
                global.user.playerSkin = info.inputSkin;
                app.toast.success('更改成功!');
            });
            return true;
        },
        cancelFn: function () {
            return true;
        }
    };
    var deleteModal = $scope.deleteModal = {
        confirmBtn: '确定',
        cancelBtn: '取消',
        confirmFn: function () {
            restAPI.remove({
                ID: playerId,
                OP: $scope.deleteSkin.type
            }, function (data) {
                $scope.info[$scope.deleteSkin.type] = {};
                //$scope.deleteSkin = null;
                app.toast.success('删除成功');
            });
            return true;
        },
        cancelFn: function () {
            return true;
        }
    };
    $scope.deleteSkin = function () {
        $scope.deleteSkin = $scope.info.skin;
        deleteModal.modal(true);
    };
    $scope.deleteCloak = function () {
        $scope.deleteSkin = $scope.info.cloak;
        deleteModal.modal(true);
    };
    $scope.upload = function (type) {
        uploadType = type;
        angular.element('.upload-input').click();
    };
    $scope.save = function (type) {
        restAPI.save({
            ID: playerId,
            OP: type
        }, {md5: $scope.info[type].change}, function (data) {
            $scope.info[type].change = false;
            app.toast.success('皮肤更改成功!');
        });
    };
    var baseUrl = global.cloudDomian || global.url || window.location.origin;
    var uploader = $scope.uploader = new FileUploader({
        url: '/api/upload',
    });
    uploader.filters.push({
        name: 'imageFilter',
        fn: function (item /*{File|FileLikeObject}*/, options) {
            var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            var parts = '|png|'.indexOf(type) !== -1;
            if (!parts)
                app.toast.warning('只能上传PNG文件!!!!');
            return parts;
        }
    });
    var uploadType;
    uploader.onAfterAddingFile = function (fileItem) {
        fileItem.upload();
    };
    $scope.loading = false;
    uploader.onBeforeUploadItem = function (item) {
        $scope.loading = true;
        $scope.info[uploadType] = $scope.info[uploadType] || {};
        $scope.info[uploadType].upload = item;
    };
    uploader.onCompleteItem = function (fileItem, response, status, headers) {
        var file = app.union(fileItem.file, response);
        if (file && file.url) {
            file.url = baseUrl + file.url;
            $scope.info[uploadType].url = file.url;
            $scope.info[uploadType].change = file.md5;
        }
        $scope.loading = false;
    };
    uploader.onErrorItem = function (fileItem, response, status, headers) {
        app.toast.warning(response.message, response.code);
    };
    restAPI.get({
        ID: playerId,
        OP: 'index'
    }, function (data) {
        app.union($scope.info, data.data);
    }, function (data) {
        app.toast.error(data);
    });
}]);