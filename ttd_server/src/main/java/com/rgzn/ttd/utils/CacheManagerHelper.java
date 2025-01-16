package com.rgzn.ttd.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

/*
 * Ehcache 缓存管理对象单例
 * 磁盘 + 内存
 * */
public class CacheManagerHelper {
    private final String EHCAHE_PATH = "/ehcache.xml";
    private final String CACHE_NAME = "localCache";
    private CacheManager manager;
    private Cache cache;

    private CacheManagerHelper() {
        init();
    }

    private static class SingletonInstance {
        private static final CacheManagerHelper singleton = new CacheManagerHelper();
    }

    public static CacheManagerHelper getInstance() {
        return SingletonInstance.singleton;
    }

    /**
     * 每次开始使用缓存对象需要初始化
     */
    public void init() {
        manager = CacheManager.create(this.getClass().getResourceAsStream(EHCAHE_PATH));
        cache = manager.getCache(CACHE_NAME);
    }

    public void put(String cacheName,String key, Object value) {
        //cache = manager.getCache(cacheName);
        cache.put(new Element(key, value));
        flush();
    }
    public Object get(String cacheName,String key) {
        //cache = manager.getCache(cacheName);
        Element element = cache.get(key);
        return element != null ? element.getObjectValue() : null;
    }

    public void remove(String cacheName,String key) {
        cache = manager.getCache(cacheName);
        cache.remove(key);
        flush();
    }

    /**
     * 把key放入缓存中
     */
    public void put(String key, Object value) {
        cache.put(new Element(key, value));
        flush();
    }

    /**
     * 根据key获取缓存元素
     */
    public Object get(String key) {
        Element element = cache.get(key);
        return element != null ? element.getObjectValue() : null;
    }

    /**
     * 根据key移除缓存
     */
    public void remove(String key) {
        cache.remove(key);
        flush();
    }

    /**
     * 构建内存与磁盘的关系
     */
    public void flush() {
        cache.flush();
    }

    /**
     * 关闭缓存管理器
     */
    public void shutdown() {
        manager.shutdown();
    }
}
