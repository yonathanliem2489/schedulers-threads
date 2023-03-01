package demo.clean.code.schedulers.config;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class SchedulersUtils {

  static AtomicReference<CachedScheduler> CACHED_BOUNDED_ELASTIC = new AtomicReference<>();

  static AtomicReference<CachedScheduler> CACHED_PARALLEL        = new AtomicReference<>();

  public static Scheduler boundedElastic(String prefixName, Supplier<Scheduler> schedulerSupplier) {
    return cache(CACHED_BOUNDED_ELASTIC, prefixName, schedulerSupplier);
  }

  public static Scheduler parallel(String prefixName, Supplier<Scheduler> schedulerSupplier) {
    return cache(CACHED_PARALLEL, prefixName, schedulerSupplier);
  }

  private static CachedScheduler cache(AtomicReference<CachedScheduler> reference, String key, Supplier<Scheduler> supplier) {
    CachedScheduler s = reference.get();
    if (s != null) {
      return s;
    }
    s = new CachedScheduler(key, supplier.get());
    if (reference.compareAndSet(null, s)) {
      return s;
    }
    //the reference was updated in the meantime with a cached scheduler
    //fallback to it and dispose the extraneous one
    s._dispose();
    return reference.get();
  }
}
