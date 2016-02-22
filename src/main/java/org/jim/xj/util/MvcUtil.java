package org.jim.xj.util;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.jim.xj.AppContext;
import org.nutz.mvc.Mvcs;

public class MvcUtil {

	public static String basePath() {
		HttpServletRequest request = Mvcs.getReq();
		if (request == null)
			return null;
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + path + "/";
		return basePath;
	}

	public static String host() {
		String host = AppContext.getConstant("global").getString("url");
		if (!host.endsWith("/")) {
			host += "/";
		}
		return host;
	}

	public static File webFile(String path) {
		String real = Mvcs.getServletContext().getRealPath(path);
		return new File(real);
	}
}
