package org.jim.xj.filter;

import javax.servlet.http.HttpSession;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ServerRedirectView;

public class AdminSession implements ActionFilter {
	
	private String name;
	private String path;
	
	public AdminSession() {
		this.name = "role";
		this.path = "/";
	}

	@Override
	public View match(ActionContext context) {
		HttpSession session = Mvcs.getHttpSession(false);
    	if (session == null)
    		return null;
        Object obj = session.getAttribute(name);
        if (null == obj)
            return new ServerRedirectView(path);
        int role = ((Number)obj).intValue();
        if(role != 5)
        	 return new ServerRedirectView(path);
        return null;
	}
}
