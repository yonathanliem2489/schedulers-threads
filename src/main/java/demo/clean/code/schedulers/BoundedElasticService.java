package demo.clean.code.schedulers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BoundedElasticService {

  private final Scheduler scheduler = Schedulers.boundedElastic();

  public Mono<Void> handle() {
    return Mono.fromRunnable(() -> {
          log.info("Bounded receive request");
          try {
            Thread.sleep(3000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        })
        .then()
        .subscribeOn(scheduler);
  }
}