package org.jim.xj.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("login_session")
public class LoginSession implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -713827085961753697L;
	@Name
	private String _id;
	@Column
	private String uId;
	@Column
	private long last;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public long getLast() {
		return last;
	}
	public void setLast(long last) {
		this.last = last;
	}
	@Override
	public String toString() {
		return "LoginSession [_id=" + _id + ", uId=" + uId + ", last=" + last
				+ "]";
	}
	
}
