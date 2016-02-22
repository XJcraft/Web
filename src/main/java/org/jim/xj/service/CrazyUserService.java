package org.jim.xj.service;

import org.jim.xj.action.BaseAction;
import org.jim.xj.bean.User;
import org.jim.xj.bean.form.LoginForm;
import org.jim.xj.exception.XJException;
import org.jim.xj.service.crazylogin.CrazyCrypt1;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(name = "userService", args = { "refer:dao" })
public class CrazyUserService extends UserService {

	private static final Log log = Logs.get();
	private CrazyCrypt1 c = new CrazyCrypt1();
	private String crazyLoginTable = "crazylogin_accounts";

	public CrazyUserService(Dao dao) {
		super(dao);
	}

	@Override
	public synchronized User getUser(String name) {
		User u = super.getUser(name);
		if (u == null) {
			Record r = dao().fetch(crazyLoginTable,
					Cnd.where("name", "=", name));// crazylogin帐号
			if (r != null) {
				// 从Crazylogin生成帐号
				u = new User();
				u.setName(name);
				u.set_id(BaseAction.generateId("U"));
				u.setNickName(name);
				u.setPasswd(r.getString("password"));// 不怕被覆盖掉
				u.setDate(System.currentTimeMillis());
				u.setRole(2);
				u.setPlayerId(name);// 重点
				dao().insert(u);
			}
		} else {
			if (u.getPlayerId() != null) {// 已经关联玩家帐号的
				Record r = dao().fetch(crazyLoginTable,
						Cnd.where("name", "=", name));
				if (r == null) {
					u.setPlayerId(null);
					u.setLocked(true);
					dao().update(u);
				} else {
					u.setPasswd(r.getString("password"));// 可能会覆盖掉原密码
				}
			}
		}
		return u;
	}

	@Override
	public User login(LoginForm form) throws XJException {
		User u = getUser(form.getLogname());
		XJException.notNull(u, "用户不存在");
		if (u.getPlayerId() != null) {
			String in = c.encrypt(form.getLogname(), form.getP());
			log.infof("玩家登录[%s]:input %s -->real %s", form.getLogname(), in,
					u.getPasswd());
			XJException.equals(u.getPasswd(), in, "密码不正确！");
		} else {
			XJException.equals(form.getLogpwd(), u.getPasswd(),
					"密码不正确:可能未关联玩家id！");
		}
		XJException.ifTrue(u.isLocked(), "您的帐号被锁定!");
		return u;
	}
}
