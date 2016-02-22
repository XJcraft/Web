package org.jim.xj.service;

import java.util.List;

import org.jim.xj.bean.Article;
import org.jim.xj.bean.User;
import org.jim.xj.nutz.NameEntityService;
import org.jim.xj.util.LRUCache;
import org.jim.xj.util.Pagination;
import org.jim.xj.util.StringSet;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(args = { "refer:dao" })
public class ArticleService extends NameEntityService<Article> {

	private static final Log log = Logs.get();
	public Cnd cnd = Cnd.where("status", ">=", 0).and("display", "=", true);
	private Condition lastCnd = Cnd.where("status", ">=", 0).and("display", "=", true).desc("zding").desc("lastTime")
			.desc("date");
	private Condition hotCnd = Cnd.where("status", ">=", 0).and("display", "=", true).desc("visitors").desc("date");
	private Condition updateCnd = Cnd.where("status", ">=", 0).and("display", "=", true).desc("updateTime")
			.desc("date");
	@Inject
	private CommentService commentService;
	@Inject
	private UserService userService;

	public ArticleService(Dao dao) {
		super(dao);
	}

	public List<Article> lastest(Pagination page) {
		Pager p = page.create();
		page.setTotal(count(lastCnd));
		return query(lastCnd, p);
	}

	public List<Article> hot(Pagination page) {
		Pager p = page.create();
		page.setTotal(count(hotCnd));
		return query(hotCnd, p);
	}

	public List<Article> update(Pagination page) {
		Pager p = page.create();
		page.setTotal(count(updateCnd));
		return query(updateCnd, p);
	}

	public List<Article> queryComment(String userId, Pagination page) {
		return commentService.queryComment(userId, page);
	}

	public List<Article> queryArticle(String userId, Pagination page) {
		Cnd cnd = (Cnd) Cnd.where("status", ">=", 0).and("display", "=", true).and("authorId", "=", userId)
				.desc("date");
		Pager p = page.create();
		page.setTotal(count(cnd));
		return query(cnd, p);
	}

	public List<Article> list(Cnd cnd, Pagination page) {
		Pager p = page.create();
		page.setTotal(count(cnd));
		return query(cnd, p);
	}

	@Override
	public List<Article> query(Condition cnd, Pager pager) {
		List<Article> articles = super.query(cnd, pager);
		Lang.each(articles, new Each<Article>() {

			@Override
			public void invoke(int index, Article ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				fill(ele);
			}
		});
		return articles;
	}

	public Article add(Article article) {
		Article a = dao().insert(article);
		cache.put(a.get_id(), a);
		return fill(a);
	}

	public Article update(Article article) {
		dao().update(article);
		cache.remove(article.get_id());
		return fill(article);
	}

	public Article detail(String id, Pagination page) {
		Article a = fetch(id);
		if (a != null) {
			// dao().fetchLinks(a, "author");
			a.setAuthor(userService.fetch(a.getAuthorId()));
			a.setCommentsList(commentService.listUnRelayComments(id, page));
		}
		return a;
	}

	public List<Article> listComments(String id, Pagination page) {
		Cnd cnd = (Cnd) Cnd.where("refer", "=", id).and("display", "=", true)
				/* .and("replyToComment", "IS", null) */.desc("date");
		return list(cnd, page);
	}

	public List<Article> listUnRelayComments(String id, Pagination page) {
		return commentService.listUnRelayComments(id, page);
	}

	private Article fill(Article a) {
		if (a != null) {
			a.setAuthor(userService.fetch(a.getAuthorId()));
		}
		return a;
	}

	@Override
	public Article fetch(String name) {
		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		Article a = super.fetch(name);
		if (a != null)
			cache.put(a.get_id(), a);
		return a;
	}

	public int countArticles() {
		return count(cnd);
	}

	public int countComments() {
		return commentService.count();
	}

	private LRUCache<String, Article> cache = new LRUCache<String, Article>(30);

	private Chain visitedChain = Chain.makeSpecial("visitors", "+1");

	public void visit(Article a) {
		dao().update(getEntityClass(), visitedChain, Cnd.where("_id", "=", a.get_id()));
		a.setVisitors(a.getVisitors() + 1);
	}

	/**
	 * 点反对
	 * 
	 * @param articleId
	 * @param mark
	 *            点一次或者点两次
	 * @param user
	 */
	public void oppose(String articleId, boolean mark, User u) {
		Article a = fetch(articleId);
		if (a != null) {
			StringSet favors = new StringSet(a.getFavors());
			StringSet opposes = new StringSet(a.getOpposes());
			if (mark) {
				opposes.add(u.get_id());
				favors.remove(u.get_id());
			} else {
				opposes.remove(u.get_id());
			}
			a.setFavors(favors.join());
			a.setOpposes(opposes.join());
			update(a);
		}
	}

	/**
	 * 点赞
	 * 
	 * @param articleId
	 * @param mark
	 * @param user
	 */
	public void favor(String articleId, boolean mark, User u) {
		Article a = fetch(articleId);
		if (a != null) {
			StringSet favors = new StringSet(a.getFavors());
			StringSet opposes = new StringSet(a.getOpposes());
			if (mark) {
				favors.add(u.get_id());
				opposes.remove(u.get_id());
			} else {
				favors.remove(u.get_id());
			}
			a.setFavors(favors.join());
			a.setOpposes(opposes.join());
			update(a);
		}
	}

	public boolean deleteArticle(String articleId) {
		Article a = fetch(articleId);
		if (a != null) {
			a.setDisplay(false);
			update(a);
			return true;
		}
		return false;
	}
}
