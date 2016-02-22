/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
jsGen.controller('userCtrl', ['app', '$scope', '$routeParams', 'getUser',
    function (app, $scope, $routeParams, getUser) {

        function tplName() {
            switch ($routeParams.OP) {
                case 'fans':
                    return 'user-list.html';
                case 'article':
                    return 'user-article.html';
                default:
                    return 'user-article.html';
            }
        }

        app.rootScope.global.title2 = app.locale.USER.title;
        $scope.parent = {
            getTpl: app.getFile.html(tplName()),
            isMe: false,
            viewPath: $routeParams.OP || 'index'
        };

        getUser('U' + $routeParams.ID).then(function (data) {
            $scope.user = data.data;
            app.rootScope.global.title2 = $scope.user.name + app.locale.USER.title;
        });
    }
]);