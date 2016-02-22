package org.jim.xj.bean;

import java.util.Map;

public class ProfileTexture {

	private  String url;
	private  Map<String, String> metadata;

	public ProfileTexture(String url, Map<String, String> metadata) {
		this.url = url;
		this.metadata = metadata;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

}
