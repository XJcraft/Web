package org.jim.xj.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.jim.xj.AppContext;
import org.jim.xj.ajax.AjaxReturn;
import org.jim.xj.bean.User;
import org.jim.xj.service.SkinService;
import org.jim.xj.service.SkinServiceImpl2;
import org.jim.xj.service.UserService;
import org.jim.xj.skin.SkinValue;
import org.jim.xj.util.Assert;
import org.jim.xj.util.UserSession;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

@At("/api/skin")
@IocBean
@Filters(@By(type = UserSession.class))
public class SkinAction extends BaseAction {

	@Inject
	private SkinService skinService;
	@Inject
	private UserService userService;

	/**
	 * xjcraft皮肤插件接口
	 * @param name
	 * @param request
	 * @return
	 * @throws Exception
     */
	@At("/?")
	@Fail("http:404")
	@Filters
	public Object profile(String name, HttpServletRequest request) throws Exception{
		User u = userService.getUser(name);
		if (u != null)
			return AjaxReturn.ok(getProfile(u));
		return AjaxReturn.error("User not found!");

	}

	@At("/?/index")
	@Fail("http:404")
	@Filters
	public Object profile2(String name) {
		User user = userService.getUserByPlayerId(name);
		Assert.notNull(user, "User who name "+name+" not found!");
		return skinService.getOfflineSkin(name);
	}

	@At("/?/skin")
	@POST
	public Object updateSkin(String name, @Param("md5") String id)
			throws IOException {
		try {
			final User user = userService.getCurrentUser(Mvcs.getReq());
			Assert.notNull(user.getPlayerId(), "未绑定玩家！");
			if (!user.isEditor())
				Assert.isTrue(user.getPlayerId().equals(name));
			skinService.updateSkin(user, id);
			return AjaxReturn.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e);
		}
	}

	@At("/?/cloak")
	@POST
	public Object updateCloak(String name, @Param("md5") String id)
			throws IOException {
		try {
			final User user = userService.getCurrentUser(Mvcs.getReq());
			Assert.notNull(user.getPlayerId(), "未绑定玩家！");
			if (!user.isEditor())
				Assert.isTrue(user.getPlayerId().equals(name));
			skinService.updateCloak(user, id);
			return AjaxReturn.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e);
		}
	}

	@At("/?/playerSkin")
	@POST
	public Object playerSkin(String id, @Param("name") final String name)
			throws IOException {
		final User user = userService.getCurrentUser(Mvcs.getReq());
		Assert.notNull(user.getPlayerId(), "未绑定玩家！");
		skinService.updateOnlineSkin(user, name);
		return AjaxReturn.ok(name);

	}

	@At("/?/skin")
	@DELETE
	public void deleteSkin(String name) {
		skinService.deleteSkin(name);
	}

	@At("/?/cloak")
	@DELETE
	public void deleteCloak(String name) {
		skinService.deleteCloak(name);
	}

	private Object getProfile(User name) throws Exception{
		SkinValue data = skinService.getSkinValue(name);
		return data;
	}
	/**
	 * 测试用，后台修复头像
	 */
	@At("/fixAvatar")
	public void fixAvatar(){
		AppContext.getIoc().get(SkinServiceImpl2.class,"skinService").fixAvatarsAsync();
	}
}
