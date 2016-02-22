package org.jim.xj.listener;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class UserListener implements HttpSessionListener {

	private static final Log log = Logs.get();
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		log.debug("sessionCreated:"+event.getSession());
		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();

		// 在application范围由一个HashSet集保存所有的session
		synchronized (application) {
			@SuppressWarnings("unchecked")
			Set<HttpSession> sessions = (Set<HttpSession>) application.getAttribute("sessions");
			if (sessions == null) {
				sessions = new HashSet<HttpSession>();
				application.setAttribute("sessions", sessions);
			}
			// 新创建的session均添加到HashSet集中
			sessions.add(session);
			
			if(log.isDebugEnabled()){
				log.debug("Current sessions: "+sessions.size());
			}
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		log.debug("sessionDestroyed:"+event.getSession());
		HttpSession session = event.getSession();
		ServletContext application = session.getServletContext();
		@SuppressWarnings({ "unchecked" })
		Set<HttpSession> sessions = (Set<HttpSession>) application.getAttribute("sessions");

		// 销毁的session均从HashSet集中移除
		if (sessions != null)
			sessions.remove(session);

	}

}
