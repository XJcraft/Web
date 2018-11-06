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

@IocBean(name = "userService", args = {"refer:dao"})
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
                u.setDate(System.currentTimeMillis());
                u.setRole(2);
                u.setPlayerId(name);// 重点，不为空时对应crazylogin登录
                dao().insert(u);
            }
        }
        return u;
    }

    public User getUserByPlayerId(String playerId) {
        return fetch(Cnd.where("playerId", "=", playerId));
    }

    @Override
    public User login(LoginForm form) throws XJException {
        User u = getUser(form.getLogname());
        XJException.notNull(u, "用户不存在" + form.getLogname());
        if (u.getPlayerId() != null) {
            // crazylogin帐号登录
            boolean v = verify(u.getPlayerId(), form.getP());
            XJException.ifTrue(!v, "密码不正确！");
        } else {
            // 正常密码登录
            XJException.equals(form.getLogpwd(), u.getPasswd(), "密码不正确:可能未关联玩家id！");
        }
        XJException.ifTrue(u.isLocked(), "您的帐号被锁定!");
        return u;
    }

    /**
     * 检验crazylogin帐号，不检验user帐号
     *
     * @param name     用户名
     * @param password 原密码
     * @return
     * @throws XJException
     */
    public boolean verify(String name, String password) throws XJException {
        Record r = dao().fetch(crazyLoginTable, Cnd.where("name", "=", name));
        if (r == null) {
            throw new XJException("用户不存在" + name);
        }
//		String in = c.encrypt(name, password);
        String in = password;
        return in.equals(r.getString("password"));
    }
}
