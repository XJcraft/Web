package org.jim.xj.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.jim.xj.AppContext;

public class RequestListener implements ServletRequestListener {

	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		

	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		//int n = AppContext.getParams().getInt("visitors",0);
		//AppContext.getParams().set("visitors", n+1);
	}

}
