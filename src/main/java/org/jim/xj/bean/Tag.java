package org.jim.xj.bean;

public class Tag {

	private String _id;
	private String tag;
	private Integer articles;
	private Integer users;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Integer getArticles() {
		return articles;
	}
	public void setArticles(Integer articles) {
		this.articles = articles;
	}
	public Integer getUsers() {
		return users;
	}
	public void setUsers(Integer users) {
		this.users = users;
	}
	
	
}
