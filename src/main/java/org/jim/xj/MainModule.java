package org.jim.xj;

import org.jim.xj.ajax.AjaxViewMaker;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@Modules(scanPackage=true)
@Encoding(input = "UTF-8", output = "UTF-8")
@IocBy(type = ComboIocProvider.class, args = {
		"*org.nutz.ioc.loader.json.JsonLoader", "dao.js",
		"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
		"org.jim.xj" })
@Ok("ajax")
@Fail("ajax")
@Views(AjaxViewMaker.class)
@SetupBy(AppContext.class)
public class MainModule {

}
