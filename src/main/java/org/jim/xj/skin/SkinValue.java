package org.jim.xj.skin;

import java.util.Map;

import org.jim.xj.bean.Skin;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class SkinValue {

	private Skin skin;

	private Skin cloak;

	private Map<String, String> textures;

	public Skin getSkin() {
		return skin;
	}

	public void setSkin(Skin skin) {
		this.skin = skin;
	}

	public Skin getCloak() {
		return cloak;
	}

	public void setCloak(Skin cloak) {
		this.cloak = cloak;
	}

	public Map<String, String> getTextures() {
		return textures;
	}

	public void setTextures(Map<String, String> textures) {
		this.textures = textures;
	}

	@Override
	public String toString() {
		return Json.toJson(this, JsonFormat.compact());
	}

}
