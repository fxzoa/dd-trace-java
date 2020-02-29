package datadog.trace.agent.tooling;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * no null keys nor null values are permitted
 *
 * @param <K>
 * @param <V>
 */
@Slf4j
public final class WeakCache<K, V> {
  public static final int CACHE_CONCURRENCY =
      Math.max(8, Runtime.getRuntime().availableProcessors());

  public static <K, V> WeakCache<K, V> newWeakCache() {
    return new WeakCache(
        CacheBuilder.newBuilder()
            .weakKeys()
            .concurrencyLevel(CACHE_CONCURRENCY)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build());
  }

  public static <K, V> WeakCache<K, V> newWeakCache(final long maxSize) {
    return new WeakCache(
        CacheBuilder.newBuilder()
            .weakKeys()
            .maximumSize(maxSize)
            .concurrencyLevel(CACHE_CONCURRENCY)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build());
  }

  private final Cache<K, V> cache;

  private WeakCache(final Cache<K, V> cache) {
    this.cache = cache;
  }

  /** @return null if key is not present */
  public V getIfPresent(final Object key) {
    return cache.getIfPresent(key);
  }

  public V getIfPresentOrCompute(final K key, final Callable<? extends V> loader) {
    final V v = cache.getIfPresent(key);
    if (v != null) {
      return v;
    }
    try {
      return cache.get(key, loader);
    } catch (ExecutionException e) {
      log.error("Can't get value from cache", e);
    }
    return null;
  }

  public V get(final K key, final Callable<? extends V> loader) {
    try {
      return cache.get(key, loader);
    } catch (ExecutionException e) {
      log.error("Can't get value from cache", e);
    }
    return null;
  }

  public void put(final K key, final V value) {
    cache.put(key, value);
  }
}
