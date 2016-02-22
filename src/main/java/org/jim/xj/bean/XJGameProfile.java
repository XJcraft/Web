package org.jim.xj.bean;

public class XJGameProfile {

	private String name;
	
	private String payload;
	
	private String signature;
	
	

	public XJGameProfile(String name, String payload, String signature) {
		super();
		this.name = name;
		this.payload = payload;
		this.signature = signature;
	}

	public XJGameProfile() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	
}
