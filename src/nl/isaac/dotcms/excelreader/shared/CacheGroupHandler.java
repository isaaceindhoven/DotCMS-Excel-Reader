package nl.isaac.dotcms.excelreader.shared;
/**
* ExcelReader by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2011 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.util.Map;
import java.util.Map.Entry;

import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.util.Logger;
/**
 * Class that handles the dotCMS cache. It uses an ItemHandler<T> to retrieve items that aren't stored in the cache yet
 * 
 * @author xander
 *
 * @param <T>
 */
public class CacheGroupHandler<T> {
	private String groupName;
	protected ItemHandler<T> itemHandler;
	
	public CacheGroupHandler(String groupName, ItemHandler<T> itemHandler) {
		this.groupName = groupName;
		this.itemHandler = itemHandler;
	}
	
	public T get(String key) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		Object o = null;
		
		if(!itemHandler.isChanged(key)) {
			try {
				o = cache.get(key, groupName);
			} catch (DotCacheException e) {
				Logger.info(this.getClass(), String.format("DotCacheException for Group '%s', key '%s', message: %s", groupName, key, e.getMessage()));
			}
		}
		
		if(o == null) {
			T t = itemHandler.get(key);
			put(key, t);
			return t;
		} else {
			return (T)o;
		}
	}
	
	public void put(String key, T t) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.put(key, t, groupName);
	}
	
	/**
	 * Updates the given key by calling the itemhandler's get method
	 */
	public void updateWithItemHandler(String key) {
		remove(key);
		put(key, itemHandler.get(key));
	}
	
	public void remove(String key) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.remove(key, groupName);
	}
	
	public void fillInitialCache() {
		removeAll();
		Map<String, T> initialCache = itemHandler.getInitialCache();
		for(Entry<String, T> entry: initialCache.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public void removeAll() {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.flushGroup(groupName);
	}
	
}
