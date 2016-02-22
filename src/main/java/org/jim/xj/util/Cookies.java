package org.jim.xj.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;

/**
 * Cookie处理工具
 * 
 * @author JIMLIANG
 * @date 2015年8月29日 上午8:52:52
 * @version V1.0
 */
public class Cookies {

	public static void setCookie(HttpServletResponse response, String key,
			String value) {
		setCookie(response, key, value, 999999999);
	}

	public static void setCookie(HttpServletResponse response, String key,
			String value, int expiry) {
		Cookie c = new Cookie(key, value);
		c.setPath("/");
		c.setMaxAge(expiry);
		response.addCookie(c);
	}

	public static Cookie getCookie(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return null;
		for (Cookie c : cookies) {
			if (Strings.equals(key, c.getName())) {
				return c;
			}
		}
		return null;
	}
}
