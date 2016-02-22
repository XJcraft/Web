package org.jim.xj.bean.form;

public class LoginForm {
	
	private String logname;
	private String logpwd;
	private long logtime;
	private String p;//明文密码
	private boolean logauto = true;
	
	public String getLogname() {
		return logname;
	}
	public void setLogname(String logname) {
		this.logname = logname;
	}
	public String getLogpwd() {
		return logpwd;
	}
	public void setLogpwd(String logpwd) {
		this.logpwd = logpwd;
	}
	public long getLogtime() {
		return logtime;
	}
	public void setLogtime(long logtime) {
		this.logtime = logtime;
	}
	public boolean isLogauto() {
		return logauto;
	}
	public void setLogauto(boolean logauto) {
		this.logauto = logauto;
	}
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	
}
