package org.jim.xj;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jim.xj.bean.User;
import org.jim.xj.service.UserService;
import org.jim.xj.util.FileContext;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class AppContext implements Setup {
	
	public static final String SESSION_USER = "userId";
	public static final String CONF_GLOBAL = "global";

	private static Map<String, FileContext> constants = new HashMap<String, FileContext>();
	private static Ioc ioc;

	@Override
	public void init(NutConfig nc) {
		ioc = nc.getIoc();
	}
	
	public static Ioc getIoc(){
		if(ioc == null){
			try {
				ioc = new NutIoc(new ComboIocLoader("*org.nutz.ioc.loader.json.JsonLoader", "dao.js",
"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
"org.jim.xj"));
			} catch (ClassNotFoundException e) {
				throw Lang.wrapThrow(e);
			}
		}
		return ioc;
	}
	public static Dao getDao(){
		return getIoc().get(Dao.class, "dao");
	}

	@Override
	public void destroy(NutConfig nc) {
		save();
	}

	public static final Context getConfig(){
		return getConstant(CONF_GLOBAL);
	}

	public static synchronized Context getConstant(String key) {
		FileContext c = constants.get(key);
		if (c == null) {
			URL url = AppContext.class
					.getResource("/constant_" + key + ".json");
			File f;
			try {
				f = new File(url.toURI());
				c = new FileContext(f);
				constants.put(key, c);
			} catch (URISyntaxException e) {
				Lang.wrapThrow(e);
			}
		}
		return c;
	}

	public static void save() {
		Logs.get().info("saving constants...");
		for (FileContext c : constants.values())
			c.save();
	}
}
