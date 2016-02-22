package org.jim.xj.service;

import java.util.List;

import org.jim.xj.bean.Article;
import org.jim.xj.util.Pagination;
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
import org.nutz.service.NameEntityService;

@IocBean(args = { "refer:dao" })
public class CommentService extends  NameEntityService<Article>{

	private Cnd cnd =Cnd.where("status", "<", 0).and("display", "=", true);
	@Inject
	private UserService userService;
	
	public CommentService(Dao dao) {
		super(dao);
	}

	public List<Article> queryComment(String userId,Pagination page){
		Cnd cnd = (Cnd) Cnd.where("status", "<", 0).and("display", "=", true).and("authorId", "=", userId).asc(
				"date");
		Pager p = page.create();
		page.setTotal(count(cnd));
		return  query(cnd, p);
	}
	
	public int count(){
		return count(cnd);
	}
	
	public List<Article> listUnRelayComments(String id,Pagination page){
		Cnd cnd = (Cnd) Cnd.where("refer", "=", id).and("display", "=", true).and("replyToComment", "IS", null).desc("date");
		Pager p = page.create();
		page.setTotal(count(cnd));
		return  query(cnd, p);
	}
	
	@Override
	public List<Article> query(Condition cnd, Pager pager) {
		List<Article> articles =  super.query(cnd, pager);
		Lang.each(articles, new Each<Article>() {

			@Override
			public void invoke(int index, Article ele, int length)
					throws ExitLoop, ContinueLoop, LoopException {
				ele.setAuthor(userService.fetch(ele.getAuthorId()));
			}
		});
		return articles;
	}
}
