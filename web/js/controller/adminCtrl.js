/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('adminCtrl', ['app', '$scope', '$routeParams',
    function (app, $scope, $routeParams) {
        var global = app.rootScope.global,
            path = $routeParams.OP || 'index';

        if (!global.isEditor) {
            return app.location.search({}).path('/');
        }

        function tplName(path) {
            path = path === 'comment' ? 'article' : path;
            return 'admin-' + path + '.html';
        }

        $scope.parent = {
            getTpl: app.getFile.html(tplName(path)),
            viewPath: path
        };
        global.title2 = app.locale.ADMIN[path];
    }
]);