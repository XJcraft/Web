package org.jim.xj.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.jim.xj.bean.User;
import org.nutz.dao.impl.NutDao;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.Mvcs;

public abstract class BaseAction {
	
	

	public static String generateId(String prefix){
		return prefix+R.sg(5).next();
	}

	
	protected long now(){
		return System.currentTimeMillis();
	}
	
	public Cookie getCookie(String key){
		Cookie[] cookies =  Mvcs.getReq().getCookies();
		if(cookies == null) return null;
		for(Cookie c :cookies){
			if(Strings.equals(key, c.getName())){
				return c;
			}
		}
		return null;
	}
	
	public String getUploadDir(){
		return Mvcs.getServletContext().getRealPath("/upload/");
	}
}
