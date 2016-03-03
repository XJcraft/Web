## 功能
>TODO

## 构建

###目录结构
|--src ***项目源文件(gradle结构)***
|--libs ***在maven库找不到的jar包的依赖***
|--web ***js,css,angular模板源文件，已经压缩打包到/src/main/webapp/，修改后需要重新打包***
|--package.json ***node...***
|--bower.json ***一些js、css依赖***
|--gulpfile.js ***gulp...***
|--build.gradle ***gradle...***

### 后端结构：
- 核心框架 [Nutz](https://github.com/nutzam/nutz)
- 数据库 Mysql
- 数据库连接池 [Druid](https://github.com/alibaba/druid)
- 由gradle构建

### 前端结构
 - 主框架 [angularjs](https://github.com/angular/angular.js)、angular-route
 - Markdown编辑器 [editor.md](https://github.com/pandao/editor.md)
 - 轻量级css模块 [purecss](http://purecss.io/)
 
###打包前端的文件(需要node环境)
```
npm install -g gulp
npm install -g bower
bower install
npm install
gulp //压缩、打包文件
```

###构建项目
```
gradle build
```
