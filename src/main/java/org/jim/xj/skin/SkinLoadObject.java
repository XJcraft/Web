package org.jim.xj.skin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jim.xj.bean.Setting;
import org.jim.xj.util.DatabaseLazyObject;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class SkinLoadObject extends DatabaseLazyObject {

	private static ExecutorService executor = Executors.newFixedThreadPool(5);
	private static Map<String, Future> futures = new HashMap<String, Future>();
	private static final Log log = Logs.get();

	public SkinLoadObject(Dao dao, String key) {
		super(dao, key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T getData() {
		final Setting setting = dao().fetch(Setting.class, getKey());
		if (setting != null) {
			if (!futures.containsKey(getKey()))
				futures.put(getKey(), executor.submit(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						try {
							Object data = load();
							//log.info("Updated " + getKey() + " : " + data);
							setting.setValue(Json.toJson(data,
									JsonFormat.compact()));
							setting.setLastTime(getLastTime());
							dao().update(setting);
							return data;
						} finally {
							futures.remove(getKey());
						}
					}
				}));
			return (T) Json.fromJson(SkinValue.class, setting.getValue());
		}

		if (setting == null) {
			if (!futures.containsKey(getKey()))
				futures.put(getKey(), executor.submit(new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						try {
							Object data = load();
							//log.info("Added " + getKey() + " : " + data);
							Setting setting = new Setting(getKey(), Json
									.toJson(data, JsonFormat.compact()),
									getLastTime());
							dao().insert(setting);
							return data;
						} finally {
							futures.remove(getKey());
						}
					}
				}));
		}
		return null;
	}

}
