package org.jim.xj.bean;

public class Resource {

	private String url;
	private String name;
	private String md5;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Resource() {
		super();
	}
	public Resource(String url, String name) {
		super();
		this.url = url;
		this.name = name;
	}
	
	public Resource(String url, String name, String md5) {
		super();
		this.url = url;
		this.name = name;
		this.md5 = md5;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	
}
