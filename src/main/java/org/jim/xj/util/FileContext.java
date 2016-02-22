package org.jim.xj.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;

public class FileContext extends SimpleContext {

	private File cacheFile;
	private boolean needSave = false;
	
	public FileContext(Context context){
		putAll(context);
	}

	public FileContext(File f) {
		cacheFile = f;
		if(f.exists()){
			@SuppressWarnings("rawtypes")
			Map m = Json.fromJsonFile(LinkedHashMap.class, f);
			putAll(m);
		}
	}

	@Override
	public Context set(String name, Object value) {
		needSave = true;
		return super.set(Strings.trim(name), value);
	}
	
	@Override
	public Object get(String name) {
		return super.get(Strings.trim(name));
	}
	
	public void save(){
		if(needSave){
			needSave = false;
			Json.toJsonFile(cacheFile, getInnerMap());
		}
	}

	@Override
	public SimpleContext clone() {
		return new FileContext(this);
	}
}
