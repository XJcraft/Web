package org.jim.xj.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("crazylogin_accounts")
public class CrazyAccount implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 634204828309628821L;
	@Name
	private String name;
	@Column
	private String password;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
