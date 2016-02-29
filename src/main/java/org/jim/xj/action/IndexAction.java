package org.jim.xj.action;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jim.xj.AppContext;
import org.jim.xj.bean.Resource;
import org.jim.xj.service.ArticleService;
import org.jim.xj.service.FriendLinkService;
import org.jim.xj.service.UserService;
import org.jim.xj.util.LazyLoadObject;
import org.jim.xj.util.UserSession;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

@At("")
@IocBean(create = "onCreate")
public class IndexAction extends BaseAction {
	private Log log = Logs.get();
	@Inject
	private ArticleService articleService;
	@Inject
	private UserService userService;
	@Inject
	private FriendLinkService friendLinkService;

	private LazyLoadObject statusInfo;

	public void onCreate() {
		statusInfo = new LazyLoadObject() {

			@Override
			protected Object load() {
				NutMap result = new NutMap();
				result.put("friendLinks", friendLinkService.list());
				result.put("users", userService.countUser());
				result.put("articles", articleService.countArticles());
				result.put("comments", articleService.countComments());
				setLastTime(System.currentTimeMillis() + 2000 * 60);//2分钟刷新一次
				return result;
			}
		};
	}

	@At("/api/index")
	public Object index() throws Exception{
		userService.checkLogin();
		Context context = AppContext.getConfig().clone();
		context.putAll(statusInfo.getData());
		long now = now();
		int usersize = userService.onlines().size();//会员
		int visitors = 0;//游客
		@SuppressWarnings({ "unchecked" })
		Set<HttpSession> sessions = (Set<HttpSession>) Mvcs.getServletContext().getAttribute("sessions");
		if(sessions!=null)
			for (HttpSession session : sessions) {
				if (session.getAttribute(AppContext.SESSION_USER) == null)
					visitors++;
			}
		context.set("user", userService.getCurrentUser(Mvcs.getReq()));
		context.set("onlineNum", visitors);
		context.set("onlineUsers", usersize);
		context.set("date", now);

		return context.getInnerMap();
	}

	// (GET POST) 设置网站全局参数
	@At("/api/admin")
	public Object admin() {
		// TODO
		return null;
	}

	@At("/api/upload")
	@Filters(@By(type = UserSession.class))
	@AdaptBy(type = UploadAdaptor.class)
	@Ok("json")
	public Object upload(@Param("file") TempFile file) throws IOException {
		String dir = "/upload/";
		String md5 = Lang.md5(file.getFile());
		String name = md5 + file.getMeta().getFileExtension();
		String targetDir = Mvcs.getServletContext().getRealPath(dir);
		File target = new File(targetDir + File.separator + name);
		Files.move(file.getFile(), target);
		log.info("move:" + file.getFile().getAbsolutePath() + "-->" + target.getAbsolutePath());
		return new Resource(dir + name, file.getMeta().getFileLocalPath(), md5);
	}
}
