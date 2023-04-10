package demo.clean.code.schedulers.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class SchedulersUtils {

  public static Scheduler boundedElastic(String prefixName, int threadCap, int queueTaskCap,
      int ttlSeconds, boolean daemon) {
    return cache(prefixName, () -> Schedulers.newBoundedElastic(threadCap, queueTaskCap, prefixName,
        ttlSeconds, daemon));
  }

  private static CachedScheduler cache(String key, Supplier<Scheduler> supplier) {
    return new CachedScheduler(key, supplier.get());
  }
}
