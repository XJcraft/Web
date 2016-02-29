package org.jim.xj.util;

import org.jim.xj.bean.Setting;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public abstract class DatabaseLazyObject extends LazyLoadObject {

	private Dao dao;
	private String key;

	public DatabaseLazyObject(Dao dao, String key) {
		super();
		this.dao = dao;
		this.key = key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T getData() throws Exception{
		Setting setting = dao.fetch(Setting.class, key);
		if (setting != null && !setting.isOutDate())
			return (T) Json.fromJson(setting.getValue());
		if (setting == null) {
			Object data = load();
			setting = new Setting(key, Json.toJson(data, JsonFormat.compact()),
					getLastTime());
			dao.insert(setting);
		} else {
			Object data = load();
			setting.setValue(Json.toJson(data, JsonFormat.compact()));
			setting.setLastTime(getLastTime());
			dao.update(setting);
		}
		return (T) Json.fromJson(setting.getValue());
	}
	
	protected Dao dao(){
		return dao;
	}
	
	public String getKey(){
		return key;
	}
}
