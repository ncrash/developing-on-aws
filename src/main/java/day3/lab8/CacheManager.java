package day3.lab8;// Copyright 2017 Amazon Web Services, Inc. or its affiliates. All rights reserved.


import net.spy.memcached.MemcachedClient;

// The CacheManager class implements the lazy caching approach to populate the cache and retrieve items from cache
public class CacheManager {

  public static final String CLUSTER_CONFIG_ENDPOINT = "qls-el-1ojq14qoqfxau.ywu7e8.cfg.apne1.cache.amazonaws.com";

  public static final int CLUSTER_CONFIG_ENDPOINT_PORT = 11211;

  public static MemcachedClient memcachedClient = null;

  public static void main(String[] args) throws Exception {
    String drugName = args[0];
    init();
    String pharmaInfo = getPharmaInfo(drugName);
    System.out.printf(
        "Retrieved pharmaceutical information for drug %s: %s%n", drugName, pharmaInfo);
  }

  public static void init() throws Exception {
    // Sets up the DynamoDB table with sample data about pharmaceutical usage
    DynamoDBManager.init();
    memcachedClient = newMemcachedClient();
  }

  /*
   * Returns pharmaceutical usage information for the given drug.
   * Attempts to retrieve item from cache.
   * If the item is not found in cache, retrieves the information from DynamoDB and updates cache.
   */
  public static String getPharmaInfo(String drugName) {
    String pharmaInfoStr = null;
    Object pharmaInfo = getCacheItem(drugName);
    if (pharmaInfo == null) {
      pharmaInfo = DynamoDBManager.getPharmaInfo(drugName);
      setCacheItem(drugName, pharmaInfo);
    }
    if (pharmaInfo != null) {
      pharmaInfoStr = pharmaInfo.toString();
    }
    return pharmaInfoStr;
  }

  /**
   * Create an instance of the MemcachedClient class
   *
   * @return    MemcachedClient instance
   */
  private static MemcachedClient newMemcachedClient() {
    return Solution.newMemcachedClient(CLUSTER_CONFIG_ENDPOINT, CLUSTER_CONFIG_ENDPOINT_PORT);
  }

  /**
   * Retrieve value from cache for the given key
   *
   * @param key   Key of the value to get
   * @return      Value of the key
   */
  private static Object getCacheItem(String key) {
    return Solution.getCacheItem(memcachedClient, key);
  }

  /**
   * Store the given key-value pair in cache
   *
   * @param key     Key of the value to set
   * @param value   The value of the key to set
   */
  private static void setCacheItem(String key, Object value) {
    Solution.setCacheItem(memcachedClient, key, value);
  }
}
