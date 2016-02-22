package org.jim.xj.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
@Table("friend_link")
public class FriendLink implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8324471143239448396L;
	@Name
	private String _id;
	@Column
	private String name;
	@Column
	@ColDefine(width=500)
	private String url;
	@Column
	private Long date;
	@Column
	private int orderBy = 0;//排序
	@Column
	//@JsonField(ignore=true)
	private boolean display = false;
	//@Column
	//@ColDefine(width=500)
	//public String imgUrl;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
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
	public boolean isDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public int getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}
	
}
