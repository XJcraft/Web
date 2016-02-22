package org.jim.xj.util;

import org.jim.xj.AppContext;
import org.nutz.mvc.filter.CheckSession;

public class UserSession extends CheckSession {

	public UserSession() {
		super(AppContext.SESSION_USER, "/");
	}

}
