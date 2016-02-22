package org.jim.xj.bean.form;



public class FriendLinkForm {
	
	//private String _id;
	private String name;
	private String url;
	
	private int orderBy = 0;//排序

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}
	
}
