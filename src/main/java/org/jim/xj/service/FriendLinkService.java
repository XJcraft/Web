package org.jim.xj.service;

import java.util.List;

import org.jim.xj.bean.FriendLink;
import org.jim.xj.nutz.NameEntityService;
import org.jim.xj.util.Pagination;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = { "refer:dao" })
public class FriendLinkService extends NameEntityService<FriendLink> {

	private List<FriendLink> links;

	public FriendLinkService(Dao dao) {
		super(dao);
	}

	public List<FriendLink> list() {
		if (links == null) {
			synchronized (this) {
				if (links == null)
					links = query(Cnd.where("display", "=", 1).desc("orderBy"),
							null);
			}
		}
		return links;
	}

	public List<FriendLink> listAll(Pagination page) {
		List<FriendLink> links = query(Cnd.orderBy().desc("orderBy"),
				page.create());
		page.setTotal(count());
		return links;
	}

	public int delete(String[] ids) {
		int success = 0;
		for (String id : ids) {
			if (delete(id) > 0) {
				success++;
			}
		}
		links = null;
		return success;
	}

	@Override
	public FriendLink fetch(String name) {
		return super.fetch(name);
	}

	public void update(FriendLink fl) {
		dao().update(fl);
		links = null;
	}

	public void insert(FriendLink fl) {
		dao().insert(fl);
	}
}
