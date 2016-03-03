/**
 * Created by JIMLIANG on 2015/8/18 0018.
 */
'use strict';
var path = require('path');
var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var minifycss = require('gulp-minify-css');
var minifyhtml = require('gulp-minify-html');

var src = path.join(__dirname, 'web');
var dist = path.join(__dirname, 'src/main/webapp');

var srcs = {
    css: 'web/css/**.css',
    js: [
        'web/js/app.js',
        'web/js/filters.js',
        'web/js/router.js',
        'web/js/services.js',
        'web/js/directives.js',
        'web/js/locale_zh-cn.js',
        'web/js/controller/**.js'
    ],
    tpl: 'web/tpl/**.html'
};

var dists = {
    tpl: 'src/main/webapp/tpl/'
}

gulp.task('css', function () {
    return gulp.src([
            'bower_components/pure/pure.css',
            'bower_components/toastr/toastr.css',
            'bower_components/font-awesome/css/font-awesome.css',
            path.join(src, 'css/editormd.css'),
            path.join(src, 'css/main.css')
        ])
        .pipe(concat('app.css'))
        .pipe(minifycss())
        .pipe(gulp.dest(path.join(dist, 'css')));


});

gulp.task('js-lib', function () {
    return gulp.src([
            'bower_components/jquery/dist/jquery.js',
            'bower_components/angular/angular.js',
            'bower_components/angular-animate/angular-animate.js',
            'bower_components/angular-cookies/angular-cookies.js',
            'bower_components/angular-resource/angular-resource.js',
            'bower_components/angular-route/angular-route.js',
            'bower_components/angular-i18n/angular-locale_zh-cn.js',
            'bower_components/angular-ui-validate/dist/validate.js',
            'bower_components/angular-file-upload/dist/angular-file-upload.min.js',
            'bower_components/toastr/toastr.js',
            //'bower_components/crypto-js/hmac-sha256.js',
            'bower_components/crypto-js/core.js',
            'bower_components/crypto-js/sha256.js',
            'bower_components/crypto-js/hmac.js',
            'bower_components/store2/dist/store2.js',
            'bower_components/jsonkit/jsonkit.js',
            'web/js/lib/bootstrap.js',
            'web/js/lib/sanitize.js',
            'web/js/lib/utf8.js',
            'web/js/lib/editormd.js',
        ])
        .pipe(concat('lib.js'))
        .pipe(uglify())
        .pipe(gulp.dest(path.join(dist, 'js')));
});


gulp.task('js-main', function () {
    return gulp.src(srcs.js)
        .pipe(concat('main.js'))
        .pipe(uglify())
        .pipe(gulp.dest(path.join(dist, 'js')));
});

gulp.task('tpl', function () {
    return gulp.src(srcs.tpl)
        .pipe(minifyhtml({
            quotes: true,
            spare: true
        }))
        .pipe(gulp.dest(dists.tpl))
});

gulp.task('default',function(){
    gulp.start(['js-lib','js-main','tpl','css']);
});

gulp.task('watch', function () {
    gulp.watch(srcs.tpl, ['tpl']);
    gulp.watch(srcs.js, ['js-main']);
    gulp.watch(srcs.css, ['css']);
});