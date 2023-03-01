package demo.clean.code.schedulers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class CustomCachedBoundedElasticService {

  private final Scheduler scheduler;

  public CustomCachedBoundedElasticService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Mono<Void> handle() {
    return Mono.fromRunnable(() -> {
          log.info("Bounded receive request");
          try {
            Thread.sleep(10000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          log.info("Bounded request end");
        })
        .then()
        .subscribeOn(scheduler);
  }
}