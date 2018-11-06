var ioc = {
	dataSource : {
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			create : "init",
			depose : 'close'
		},
		fields : {
            url: "jdbc:mysql://localhost:3306/mc?useUnicode=true&characterEncoding=utf-8",
            username: "mc",
            password: "password",
			testWhileIdle : true,
			maxActive : 100
		}
	},
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [ {
			refer : 'dataSource'
		} ]
	},
	skinService : {
		type : "org.jim.xj.service.SkinServiceImpl2",
		args : [ {
			refer : 'dao'
		}, '~/.minecraft_skin' ]
	}
};