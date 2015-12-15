package sopra.grenoble.jiraLoader.jira.dao.metadatas.utils;

import java.util.HashMap;

/**
 * @author cmouilleron
 * Generic class to keep in memory element loaded from JIRA
 *
 * @param <T>
 */
public abstract class MetadataGenLoader<T> {
	
	private HashMap<String, T> loadedHashElt = new HashMap<>();
	
	public void addElement(String key, T element) {
		this.loadedHashElt.put(key, element);
	}
	
	public T getElement(String key) {
		return this.loadedHashElt.get(key);
	}
	
	public void cleanAllElements() {
		this.loadedHashElt.clear();
	}
	
	public int countElements() {
		return this.loadedHashElt.size();
	}
	
	public abstract void loadElements();
}
