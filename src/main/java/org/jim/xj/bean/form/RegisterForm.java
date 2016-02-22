package org.jim.xj.bean.form;

public class RegisterForm {

	private String name;
	private String passwd;//SHA256(user.passwd)
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	
}
