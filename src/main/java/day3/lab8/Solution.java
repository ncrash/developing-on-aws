package day3.lab8;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.

import net.spy.memcached.MemcachedClient;

import java.net.InetSocketAddress;

public class Solution {

  public static MemcachedClient newMemcachedClient(String host, int port) {
    MemcachedClient memcachedClient = null;
    try {
      memcachedClient = new MemcachedClient(new InetSocketAddress(host, port));
    } catch (Exception e) {
      // Do nothing
    }
    return memcachedClient;
  }

  public static Object getCacheItem(MemcachedClient memcachedClient, String key) {
    Object valueFromCache = null;
    valueFromCache = memcachedClient.get(key);
    return valueFromCache;
  }

  public static void setCacheItem(MemcachedClient memcachedClient, String key, Object value) {
    memcachedClient.set(key, 3600, value);
  }
}
