package org.jim.xj.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jim.xj.AppContext;
import org.jim.xj.bean.LoginSession;
import org.jim.xj.bean.User;
import org.jim.xj.bean.form.LoginForm;
import org.jim.xj.exception.XJException;
import org.jim.xj.nutz.NameEntityService;
import org.jim.xj.util.Cookies;
import org.jim.xj.util.LRUCache;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.trans.Atom;

//@IocBean(args = { "refer:dao" })
public class UserService extends NameEntityService<User> {

	public static final String SESSION_COOKIE = "xjcraft";
	private static final Log log = Logs.get();
	private static int expiry = 1000 * 60 * 60 * 24 * 7;// 7d
	private Map<String, User> onlines = new LinkedHashMap<String, User>();
	
	public UserService(Dao dao) {
		super(dao);
	}

	public void add(User user) {
		dao().insert(user);
	}
	
	public void removeOnline(String id){
		onlines.remove(id);
	}
	
	public void addOnline(String id){
		if(!Strings.isEmpty(id) && !onlines.containsKey(id)){
			onlines.put(id, fetch(id));
		}
	}
	
	public boolean isOnline(String id){
		return onlines.containsKey(id);
	}
	
	public Collection<User> onlines(){
		return onlines.values();
	}

	public User getCurrentUser(HttpServletRequest req) {
		String id = (String) req.getSession(true).getAttribute(AppContext.SESSION_USER);
		if(id == null) return null;
		return fetch(id);
	}

	public void tryLogin(final User user, HttpServletRequest request,
			boolean saveCookie) {
		HttpSession session = request.getSession(true);
		session.setAttribute(AppContext.SESSION_USER, user.get_id());
		session.setAttribute("role", user.getRole());
		user.setLastLoginDate(System.currentTimeMillis());
		FieldFilter.create(getEntityClass(), "^lastLoginDate$").run(new Atom() {

			@Override
			public void run() {
				update(user);
			}
		});
		addOnline(user.get_id());
		if (saveCookie) {
			long last = System.currentTimeMillis() + expiry;
			LoginSession s = createSession(user.get_id(), last);
			Cookies.setCookie(Mvcs.getResp(), SESSION_COOKIE, s.get_id(),
					expiry / 1000);
		}
	}

	public boolean existsNickName(String name) {
		return dao().count(getEntityClass(), Cnd.where("nickName", "=", name)) > 0;
	}

	public User getUser(String name) {
		User u = NameCache.get(name);
		if(u == null){
			u = fetch(Cnd.where("nickName", "=", name));
			if(u != null)
				NameCache.put(name, u);
		}
		return u;
	}
	
	public User getUserByPlayerId(String name){
		return fetch(Cnd.where("playerId", "=", name));
	}

	public int countUser() {
		return count(Cnd.where("locked", "=", false));
	}

	public User login(LoginForm form) throws XJException {
		User u = getUser(form.getLogname());
		XJException.notNull(u, "用户不存在");
		XJException.equals(form.getLogpwd(), u.getPasswd(), "密码不正确！");
		XJException.ifTrue(u.isLocked(), "您的帐号被锁定!");
		return u;
	}

	protected LoginSession createSession(String uid, long lastDate) {
		LoginSession s = new LoginSession();
		s.set_id(R.sg(16).next());
		s.setuId(uid);
		s.setLast(lastDate);
		dao().insert(s);
		return s;
	}

	protected LoginSession getSession(String id) {
		LoginSession s = dao().fetch(LoginSession.class, id);
		if (s != null && (System.currentTimeMillis() - s.getLast()) > 0) {
			//dao().delete(s);
			deleteInvalidSession();
		}
		return s;
	}
	
	protected void deleteInvalidSession(){
		int count = dao().clear(LoginSession.class, Cnd.where("last", "<", System.currentTimeMillis()));
		log.info("Clear invalid session: "+count);
	}

	public List<User> queryAll(Pager pager) {
		return query(null, pager);
	}

	@Override
	public User fetch(String id) {
		if(cache.containsKey(id)){
			return cache.get(id);
		}
		User u = super.fetch(id);
		if(u!=null)
			cache.put(u.get_id(), u);
		return u;
	}

	public void update(User u) {
		dao().update(u);
		cache.put(u.get_id(), u);
		NameCache.put(u.getName(), u);
	}
	

	private LRUCache<String, User> NameCache = new LRUCache<String, User>(30);
	private LRUCache<String, User> cache = new LRUCache<String, User>(30);

	public void logout(HttpServletRequest req) {
		req.getSession(true).invalidate();
		Cookies.setCookie(Mvcs.getResp(), SESSION_COOKIE, null, 0);
	}

	public boolean checkLogin() {
		HttpServletRequest request = Mvcs.getReq();
		User user = getCurrentUser(request);
		if(user==null){
			Cookie login = Cookies.getCookie(request,SESSION_COOKIE);
			if(login!=null){
				LoginSession s = getSession(login.getValue());
				log.infof("try login: %s,%s",login,s);
				User u = fetch(s.getuId());
				if (u != null && !u.isLocked()){
					tryLogin(u, request, false);
					return true;
				}else{
					Cookies.setCookie(Mvcs.getResp(), SESSION_COOKIE, null, 0);
				}
			}
		}
		return false;
	}
}
