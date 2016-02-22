package org.jim.xj.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jim.xj.ServerException;
import org.jim.xj.ajax.AjaxReturn;
import org.jim.xj.bean.Article;
import org.jim.xj.bean.User;
import org.jim.xj.bean.form.ArticleEditForm;
import org.jim.xj.bean.form.PageForm;
import org.jim.xj.service.ArticleService;
import org.jim.xj.service.UserService;
import org.jim.xj.util.Pagination;
import org.jim.xj.util.StringSet;
import org.jim.xj.util.UserSession;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

@At("/api")
@IocBean
public class ArticleAction extends BaseAction {

	private static Log log = Logs.get();
	@Inject
	private ArticleService articleService;
	@Inject
	private UserService userService;

	@At("/article/latest")
	public Object latest(@Param("..") PageForm form) {
		Pagination page = form.create();
		List<Article> articles = articleService.lastest(page);
		AjaxReturn result = AjaxReturn.ok(articles);
		result.setPagination(page);
		return result;
	}

	@At("/article/hots")
	public Object hots(@Param("..") PageForm form) {
		Pagination page = form.create();
		List<Article> articles = articleService.hot(page);
		AjaxReturn result = AjaxReturn.ok(articles);
		result.setPagination(page);
		return result;
	}

	@At("/article/update")
	public Object update(@Param("..") PageForm form) {
		Pagination page = form.create();
		List<Article> articles = articleService.update(page);
		AjaxReturn result = AjaxReturn.ok(articles);
		result.setPagination(page);
		return result;
	}

	@At("/user/comment/comment")
	@Filters(@By(type = UserSession.class))
	public Object myComments(@Param("..") PageForm form) {
		User u = userService.getCurrentUser(Mvcs.getReq());
		return articleService.queryComment(u.get_id(), form.create());
	}

	@At("/article/?/comment")
	@POST
	@Filters(@By(type = UserSession.class))
	public Object addComment(String articleId, @Param("..") Article form) {
		Article a = articleService.fetch(articleId);
		User u = userService.getCurrentUser(Mvcs.getReq());
		if (a == null || u == null) {
			return AjaxReturn.error("文章不存在");
		}
		Article comment = new Article();
		comment.set_id(generateId("A"));
		comment.setAuthorId(u.get_id());
		comment.setTitle(form.getTitle());
		comment.setContent(form.getContent());
		comment.setDate(now());
		comment.setUpdateTime(now());
		// comment.setRefer(form.getRefer());
		comment.setRefer(articleId);
		comment.setReplyToComment(form.getReplyToComment());
		comment.setDisplay(true);
		comment.setStatus(-1);
		articleService.add(comment);
		a.setComments(a.getComments() + 1);
		a.setLastTime(now());
		articleService.update(a);
		// 回复评论，评论+1 更新最后回复时间
		if (!articleId.equals(form.getRefer())) {
			Article c = articleService.fetch(form.getRefer());
			if (c != null) {
				c.setComments(c.getComments() + 1);
				c.setLastTime(now());
				articleService.update(c);
			}
		}
		return AjaxReturn.ok(comment);
	}

	@At("/article/?/comment")
	@GET
	public Object listComment(String articleId, @Param("..") PageForm form) {
		Pagination page = form.create();
		return AjaxReturn.ok(articleService.listUnRelayComments(articleId, page)).setPagination(page);
	}

	@At("/article/comment")
	@POST
	public Object listRelayComment(@Param("..") ArticleEditForm form) {
		Cnd cnd = (Cnd) Cnd.where("refer", "=", form.getRefer()).and("replyToComment", "=", form.getReplyToComment())
				.and("display", "=", true).desc("date");
		return AjaxReturn.ok(articleService.query(cnd, null));
	}

	// api/article //(GET POST) 获取最新文章列表，添加文章
	// api/article/index //(GET POST)
	@At("/article/index")
	@POST
	@Filters(@By(type = UserSession.class))
	public Object addArticle(@Param("..") ArticleEditForm form) {
		User u = userService.getCurrentUser(Mvcs.getReq());
		Article article = new Article();
		article.set_id(generateId("A"));
		article.setAuthorId(u.get_id());
		article.setTitle(form.getTitle());
		article.setContent(form.getContent());
		long now = now();
		article.setDate(now);
		article.setUpdateTime(now);
		article.setLastTime(now);
		article.setDisplay(true);
		article.setStatus(0);
		article.setSource(form.getSource());
		try {
			return AjaxReturn.ok(articleService.add(article));
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxReturn.error(e.getLocalizedMessage());
		}
	}

	@At("/article/?/edit")
	@POST
	@Filters(@By(type = UserSession.class))
	public Object editArticle(String articleId, @Param("..") ArticleEditForm form) {
		Article article = articleService.fetch(articleId);
		if (article == null) {
			AjaxReturn.error("文章不存在");
		}
		User u = userService.getCurrentUser(Mvcs.getReq());
		if (!(u.isEditor() || u.get_id().equals(article.get_id()))) {
			AjaxReturn.error("权限不足!");
		}
		article.setTitle(form.getTitle());
		article.setContent(form.getContent());
		article.setSource(form.getSource());
		article.setUpdateTime(now());
		articleService.update(article);
		return AjaxReturn.ok(article);
	}

	@At("/article/?")
	@GET
	public Object articleDetail(String id, @Param("..") PageForm form) throws ServerException {
		Pagination page = form.create();
		Article a = articleService.detail(id, page);
		if (a == null || !a.isDisplay()) {
			throw new ServerException("文章不存在或被删除!");
		}
		articleService.visit(a);
		return AjaxReturn.ok(a).setPagination(page);
	}

	@At({ "/user/?/article", "/user/index"/* ,"/user/?" */ })
	@GET
	public AjaxReturn userArticles(String id, @Param("..") PageForm form) {
		Pagination pager = form.create();
		List<Article> articles = articleService.queryArticle(id, pager);
		User user = (id == null) ? userService.getCurrentUser(Mvcs.getReq()) : userService.fetch(id);
		AjaxReturn j = AjaxReturn.ok(articles);
		j.setPagination(pager);
		j.setUser(user);
		return j;
	}

	@At("/user/article/article")
	@GET
	@Filters(@By(type = UserSession.class))
	public Object myArticles(@Param("..") PageForm form,HttpServletRequest req) {
		User user = userService.getCurrentUser(req);
		AjaxReturn j = userArticles(user.get_id(), form);
		j.setReadtimestamp(user.getReadtimestamp());
		user.setReadtimestamp(System.currentTimeMillis());
		userService.update(user);
		return j;
	}

	@At("/article/?")
	@DELETE
	@Filters(@By(type = UserSession.class))
	public Object deleteArticle(String articleId,HttpServletRequest req) {
		Article a = articleService.fetch(articleId);
		User u = userService.getCurrentUser(req);
		// editor or author
		if (u.isEditor() || u.get_id().equals(a.getAuthorId())) {
			articleService.deleteArticle(articleId);
			return AjaxReturn.ok(a);
		}
		return AjaxReturn.error("删除失败");
	}

	// 点赞
	@At("/article/?/favor")
	@Filters(@By(type = UserSession.class))
	@POST
	public Object favorArticle(String articleId, @Param("favor") boolean mark) {
		User u = userService.getCurrentUser(Mvcs.getReq());
		articleService.favor(articleId, mark, u);
		return AjaxReturn.ok(null);
	}

	/**
	 * 点反对
	 * 
	 * @param articleId
	 * @param mark
	 * @return
	 */
	@At("/article/?/oppose")
	@Filters(@By(type = UserSession.class))
	@POST
	public Object opposeArticle(String articleId, @Param("oppose") boolean mark, HttpServletRequest req) {
		User u = userService.getCurrentUser(req);
		articleService.oppose(articleId, mark, u);
		return AjaxReturn.ok();
	}

	/**
	 * 置顶、取消置顶
	 * 
	 * @param articleId
	 * @param zding
	 * @return
	 */
	@At("/article/?/zding")
	@Filters(@By(type = UserSession.class))
	@POST
	public Object zdingArticle(String articleId, @Param("zding") boolean zding, HttpServletRequest req) {
		Article a = articleService.fetch(articleId);
		if (a != null) {
			User u = userService.getCurrentUser(req);
			if (u != null && u.isEditor()) {
				a.setZding(zding);
				articleService.update(a);
			}
		}
		return AjaxReturn.ok(a);
	}

	/**
	 * 精华、取消精华
	 * 
	 * @param articleId
	 * @param jing
	 * @return
	 */
	@At("/article/?/jing")
	@Filters(@By(type = UserSession.class))
	@POST
	public Object jingArticle(String articleId, @Param("jing") boolean jing, HttpServletRequest req) {
		Article a = articleService.fetch(articleId);
		if (a != null) {
			User u = userService.getCurrentUser(req);
			if (u != null && u.isEditor()) {
				// 权限
				a.setJing(jing);
				articleService.update(a);
			} else {
				AjaxReturn.error("权限不足");
			}
		}
		return AjaxReturn.ok(a);
	}
}
