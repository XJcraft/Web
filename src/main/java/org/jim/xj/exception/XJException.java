package org.jim.xj.exception;

import org.nutz.lang.Strings;
import org.nutz.log.Logs;

public class XJException extends Exception {

	private String name = "错误提示";
	private String message;
	public XJException(String message) {
		super(message);
		this.message = message;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static void notNull(Object o,String message) throws XJException{
		if(null == o)
			throw new XJException(message);
	}
	public static void equals(String s1,String s2,String message)throws XJException{
		if (!Strings.equals(s1, s2))
			throw new XJException(message);
	}
	
	public static void ifTrue(boolean value,String message)throws XJException{
		if (value)
			throw new XJException(message);
	}
}
