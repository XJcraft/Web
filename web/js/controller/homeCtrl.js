/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('homeCtrl', ['app', '$scope', '$routeParams',
    function (app, $scope, $routeParams) {
        var global = app.rootScope.global;

        if (!global.isLogin) {
            return app.location.search({}).path('/');
        }

        function tplName(path) {
            switch (path) {
                case 'follow':
                case 'fans':
                    return 'user-list.html';
                case 'detail':
                    return 'user-edit.html';
                case 'skin':
                    return 'user-skin.html';
                case 'article':
                case 'comment':
                case 'mark':
                    return 'user-article.html';
                default:
                    return 'user-article.html';
            }
        }

        global.title2 = app.locale.HOME.title;
        $scope.user = global.user;
        $scope.parent = {
            getTpl: app.getFile.html(tplName($routeParams.OP)),
            isMe: true,
            viewPath: $routeParams.OP || 'index'
        };
    }
]);