package org.jim.xj.util;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//并发任务
public class FutureSession<K, V> {

	private ExecutorService executor = Executors.newCachedThreadPool();
	private Map<K, Future<V>> futures = new ConcurrentHashMap<K, Future<V>>();

	public V get(K key, Callable<V> call) throws InterruptedException,
			ExecutionException {
		Future<V> f = futures.get(key);
		if (f == null) {
			f = executor.submit(call);
			futures.put(key, f);
		}
		V result = null;
		try {
			result = f.get();
		} finally {
			futures.remove(key);
		}
		return result;
	}
}
