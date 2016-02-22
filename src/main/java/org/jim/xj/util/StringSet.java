package org.jim.xj.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.nutz.lang.Strings;
/**
 * string 包装，添加join,add,remove等方法
 * @author JIMLIANG
 * @version V1.0
 */
public class StringSet {

	private static String split = ",";
	
	private Set<String> set = new HashSet<String>();

	public StringSet(String stringList) {
		super();
		if(!Strings.isEmpty(stringList))
			this.set.addAll(Arrays.asList(stringList.split(split)));
	}
	
	public String join(){
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = set.iterator();
		if(it.hasNext())
			sb.append(it.next());
		while(it.hasNext())
			sb.append(","+it.next());
		return sb.toString();
	}
	
	public boolean add(String e){
		return set.add(e);
	}
	public boolean remove(String e){
		return set.remove(e);
	}
}
