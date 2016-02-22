/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('adminGlobalCtrl', ['app', '$scope',
    function (app, $scope) {
        var globalTpl,
            originData = {},
            tagsArray = [],
            locale = app.locale,
            filter = app.filter,
            restAPI = app.restAPI.index,
            lengthFn = filter('length');


        function initglobal(data) {
            $scope.global = app.union(data);
            originData = app.union(data);
            originData = app.intersect(app.union(globalTpl), originData);
            $scope.editGlobal = app.union(originData);
            app.checkDirty(globalTpl, originData, $scope.editGlobal);
        }

        $scope.parent = {
            switchTab: 'tab1'
        };
        $scope.reset = function () {
            $scope.editGlobal = app.union(originData);
        };
        $scope.submit = function () {
            var data = app.union($scope.editGlobal);
            if (app.validate($scope)) {
                data = app.checkDirty(globalTpl, originData, data);
                if (app.isEmpty(data)) {
                    app.toast.info(locale.ADMIN.noUpdate);
                } else {
                    restAPI.save({
                        OP: 'admin'
                    }, data, function (data) {
                        initglobal(data.data);
                        var updated = app.union(originData);
                        //delete updated.smtp;
                        //delete updated.email;
                        app.union(app.rootScope.global, updated);
                        app.toast.success(locale.ADMIN.updated, locale.RESPONSE.success);
                    });
                }
            }
        };

        $scope.$watchCollection('editGlobal', function (value) {
            if(value && globalTpl)
                app.checkDirty(globalTpl, originData, value);
        });
        app.promiseGet({
            OP: 'admin'
        }, restAPI).then(function (data) {
            //globalTpl = data.configTpl;
            globalTpl = app.union(data.data);
            initglobal(data.data);
        });
    }
]);