package org.jim.xj.util;

public abstract class LazyLoadObject {

	private Object data;
	
	private long lastTime =-1;
	
	private Object lock = new Object();
	
	private boolean load = false;
	
	protected abstract Object load() throws Exception;
	
	@SuppressWarnings("unchecked")
	public synchronized <T>T getData() throws Exception{
		if(isOutDate())
			synchronized (lock) {
				try{
					data = load();
				} catch(Exception e){
					data = e;
				}
				load =true;
			}

		if(data instanceof Exception) {
			throw (Exception)data;
		}
		return (T)data;
	}
	protected boolean isOutDate(){
		return (lastTime>-1 &&  System.currentTimeMillis()> lastTime) || !load;
	}


	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getLastTime() {
		return lastTime;
	}
	
}
