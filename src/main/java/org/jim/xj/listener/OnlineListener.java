package org.jim.xj.listener;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.jim.xj.AppContext;
import org.jim.xj.service.UserService;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class OnlineListener implements HttpSessionAttributeListener {

	private static final Log log = Logs.get();
	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		log.debug("HttpSession attributeAdded:"+arg0.getName());
		if (AppContext.SESSION_USER.equals(arg0.getName())) {
			u().addOnline(arg0.getValue().toString());
			if(log.isDebugEnabled()){
				log.debug("Add online player: "+arg0.getValue().toString());
				log.debug("Current Players: "+u().onlines());
			}
		}

	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		log.debug("HttpSession attributeRemoved:"+arg0.getName());
		if (AppContext.SESSION_USER.equals(arg0.getName())) {
			u().removeOnline(arg0.getValue().toString());
			if(log.isDebugEnabled()){
				log.debug("remove online player: "+arg0.getValue().toString());
				log.debug("Current Players: "+u().onlines());
			}
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		log.debug("HttpSession attributeReplaced:"+arg0.getName());
		if (AppContext.SESSION_USER.equals(arg0.getName())) {
			u().addOnline(arg0.getValue().toString());
		}
	}

	private UserService u() {
		return AppContext.getIoc().get(UserService.class);
	}
}
