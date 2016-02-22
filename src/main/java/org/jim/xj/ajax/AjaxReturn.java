package org.jim.xj.ajax;

import org.jim.xj.exception.XJException;
import org.jim.xj.util.Pagination;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.NutDao;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.repo.Base64;

public class AjaxReturn {

	private boolean ack;
	private Long timestamp;
	private Object data;
	private Object error;
	private Pagination pagination;
	private Long readtimestamp;
	private Object user;

	public AjaxReturn() {
		super();
	}

	public static AjaxReturn ok(Object data) {
		AjaxReturn json = new AjaxReturn();
		json.ack = true;
		json.timestamp = System.currentTimeMillis();
		json.data = data;
		return json;
	}

	public static AjaxReturn ok() {
		return ok(null);
	}

	public static AjaxReturn error(Throwable exception) {
		AjaxReturn json = new AjaxReturn();
		json.ack = false;
		json.timestamp = System.currentTimeMillis();
		NutMap map = new NutMap();
		if(exception instanceof DaoException){
			while( exception.getCause()!= null){
				exception = exception.getCause();
			}
		}
		String message = Strings.sBlank(exception.getMessage(), "服务器出错!");
		map.setv("name", "错误提示").setv("message", message);
		json.error = map;
		return json;
	}

	public static AjaxReturn error(String message) {
		return error(new XJException(message));
	}

	public boolean isAck() {
		return ack;
	}

	public void setAck(boolean ack) {
		this.ack = ack;
	}

	public Object getError() {
		return error;
	}

	public void setError(Object error) {
		this.error = error;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public AjaxReturn setPagination(Pagination pagination) {
		this.pagination = pagination;
		return this;
	}

	public Long getReadtimestamp() {
		return readtimestamp;
	}

	public void setReadtimestamp(Long readtimestamp) {
		this.readtimestamp = readtimestamp;
	}

	public Object getUser() {
		return user;
	}

	public void setUser(Object user) {
		this.user = user;
	}

}
