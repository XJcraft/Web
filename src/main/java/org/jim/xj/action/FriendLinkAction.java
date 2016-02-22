package org.jim.xj.action;

import java.util.List;

import org.jim.xj.ajax.AjaxReturn;
import org.jim.xj.bean.FriendLink;
import org.jim.xj.bean.form.FriendLinkForm;
import org.jim.xj.bean.form.PageForm;
import org.jim.xj.filter.AdminSession;
import org.jim.xj.service.FriendLinkService;
import org.jim.xj.util.Assert;
import org.jim.xj.util.Pagination;
import org.jim.xj.util.UserSession;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Param;

@At("/api")
@IocBean
@Filters(@By(type = UserSession.class))
public class FriendLinkAction extends BaseAction {
	@Inject
	private FriendLinkService friendLinkService;

	@At("/friendLink/admin")
	@GET
	@Filters(@By(type = AdminSession.class))
	public Object listAll(@Param("..") PageForm form) {
		Pagination page = form.create();
		List<FriendLink> links = friendLinkService.listAll(page);
		return AjaxReturn.ok(links).setPagination(page);
	}

	@At("/friendLink/index")
	public Object save(@Param("..") FriendLinkForm form) {
		try {
			FriendLink f = new FriendLink();
			f.set_id(generateId("F"));
			f.setName(form.getName());
			f.setUrl(form.getUrl());
			f.setOrderBy(form.getOrderBy());
			f.setDisplay(false);
			f.setDate(now());
			friendLinkService.insert(f);
			return AjaxReturn.ok(f);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e);
		}
	}

	@At("/friendLink/?/edit")
	public Object update(String fId, @Param("..") FriendLinkForm form) {
		try {
			FriendLink f = friendLinkService.fetch(fId);
			Assert.notNull(f, "FriendLink not exist!");
			f.setName(form.getName());
			f.setUrl(form.getUrl());
			f.setOrderBy(form.getOrderBy());
			f.setDisplay(false);
			friendLinkService.update(f);
			return AjaxReturn.ok(f);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e);
		}
	}

	@At("/friendLink/?/display")
	public Object display(String fId, @Param("display") int value) {
		try {
			FriendLink f = friendLinkService.fetch(fId);
			Assert.notNull(f, "FriendLink not exist!");
			f.setDisplay(value == 1);
			friendLinkService.update(f);
			return AjaxReturn.ok(f);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e);
		}
	}

	@At("/friendLink/delete")
	public Object delete(@Param("fids") String fIds) {
		try {
			return AjaxReturn.ok(friendLinkService.delete(fIds.split(",")));
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e);
		}
	}
}
