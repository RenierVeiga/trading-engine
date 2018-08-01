package Entities;

import java.util.HashMap;

public abstract class Subject<T> {
	protected HashMap<String, T> observerMap = new HashMap<String, T>();

	public void register(String symbol, T observer) {
		if (!observerMap.containsKey(symbol)) {
			observerMap.put(symbol, observer);
		}
	}

	public void unregister(String symbol) {
		observerMap.remove(symbol);
	}

	public abstract void updateObservers();

	public HashMap<String, T> getObserverMap() {
		return observerMap;
	}

}
