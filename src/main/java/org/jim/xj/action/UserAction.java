package org.jim.xj.action;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpSession;

import org.jim.xj.ajax.AjaxReturn;
import org.jim.xj.bean.User;
import org.jim.xj.bean.form.LoginForm;
import org.jim.xj.bean.form.RegisterForm;
import org.jim.xj.bean.form.UserForm;
import org.jim.xj.exception.XJException;
import org.jim.xj.service.ArticleService;
import org.jim.xj.service.SkinService;
import org.jim.xj.service.UserService;
import org.jim.xj.util.Assert;
import org.jim.xj.util.UserSession;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

@At("/api/user")
@IocBean
public class UserAction extends BaseAction {
	private static Log log = Logs.get();
	@Inject
	private UserService userService;
	@Inject
	private ArticleService articleService;
	@Inject
	private SkinService skinService;

	// (GET POST) 用户管理相关后台接口
	/*
	 * @At("/admin") public Object admin(){ return null; }
	 */

	// (POST) 用户登录
	@POST
	@At("/login")
	public Object login(@Param("..") LoginForm form) throws XJException {
		User u = userService.login(form);
		userService.tryLogin(u, Mvcs.getReq(), form.isLogauto());
		return u;
	}

	@At("/register")
	// (POST) 用户注册
	@POST
	public Object register(@Param("..") RegisterForm form) {
		if (userService.existsNickName(form.getName())) {
			return AjaxReturn.error("用户名已存在");
		}
		User user = new User();
		user.set_id(generateId("U"));
		user.setName(form.getName());
		user.setNickName(form.getName());
		user.setPasswd(form.getPasswd());
		user.setDate(now());
		user.setRole(2);
		user.setLastLoginDate(now());
		try {
			userService.add(user);
			userService.tryLogin(user, Mvcs.getReq(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e.getMessage());
		}
		return AjaxReturn.ok(user);
	}

	// (GET) 退出登录
	@GET
	@At("/logout")
	public Object logout(HttpSession session) {
		userService.logout(Mvcs.getReq());
		return AjaxReturn.ok(null);
	}

	// api/user/reset //(GET POST) 用户邮箱验证、申请解锁、重置密码、修改邮箱等涉及邮箱验证的操作

	@At("/?")
	@GET
	public Object userInfo(String userId) {
		return AjaxReturn.ok(userService.fetch(userId));
	}

	@At("/")
	@POST
	@Filters(@By(type = UserSession.class))
	public Object userModify(@Param("..") UserForm form) {
		User u = userService.getCurrentUser(Mvcs.getReq());
		if (form.getQq() != null)
			u.setQq(form.getQq());
		if (form.getEmail() != null && Strings.isEmail(form.getEmail())) {
			u.setEmail(form.getEmail());
		}
		if (form.getSex() != null) {
			u.setSex(form.getSex());
		}
		if (form.getDescp() != null) {
			u.setDescp(form.getDescp());
		}
		userService.update(u);
		return AjaxReturn.ok(u);
	}

	@At("/avatar/?")
	@Ok("raw")
	@Deprecated
	public BufferedImage avatar(String userId) {
		try {
			BufferedImage b = skinService.head(userId);
			Assert.notNull(b);
			return b;
			// return Skinmes.head(userId);
		} catch (Exception e) {
			try {
				return skinService.head("steve");
			} catch (Exception e1) {
				throw Lang.wrapThrow(e);
			}
		}
	}

	/*
	 * 
	 * 
	 * /api/user/admin //(GET POST) 用户管理相关后台接口
	 * 
	 * /api/user/article //(GET) 获取已登录用户（自己）的文章列表
	 * 
	 * /api/user/comment //(GET) 获取已登录用户（自己）的评论列表
	 * 
	 * /api/user/mark //(GET) 获取已登录用户（自己）的标记文章列表
	 * 
	 * /api/user/fans //(GET) 获取已登录用户（自己）的粉丝列表
	 * 
	 * /api/user/follow //(GET) 获取已登录用户（自己）的关注列表 /api/user/Uxxxxx //(GET POST)
	 * 获取用户Uxxxxx的用户信息，包括用户公开的个人信息和用户最新发表的文章列表
	 * 
	 * /api/user/Uxxxxx/article //(GET) 获取用户Uxxxxx的文章列表
	 * 
	 * /api/user/Uxxxxx/fans //(GET) 获取用户Uxxxxx的粉丝列表
	 */
}
