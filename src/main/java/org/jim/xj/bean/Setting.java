package org.jim.xj.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("setting")
public class Setting implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8406587368586964354L;
	@Name
	private String id;// key
	@Column
	@ColDefine(width=3000)
	private String value;
	@Column
	private long lastTime =-1;
	
	

	public Setting() {
		super();
	}

	public Setting(String id, String value, long lastTime) {
		super();
		this.id = id;
		this.value = value;
		this.lastTime = lastTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public boolean isOutDate(){
		return lastTime>-1 &&  System.currentTimeMillis()>lastTime;
	}
	
	public static void main(String[] args) {
		
	}
}
