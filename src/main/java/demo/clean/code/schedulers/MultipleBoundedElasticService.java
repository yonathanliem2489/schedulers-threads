package demo.clean.code.schedulers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class MultipleBoundedElasticService {

  private final Scheduler scheduler = Schedulers.boundedElastic();

  public Mono<Void> handle() {
    return Mono.fromCallable(() -> {
          log.info("Bounded 1 receive request");
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          return true;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(result -> Mono.fromCallable(() -> {
          log.info("Bounded 2 receive request");
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          return true;
        }).subscribeOn(Schedulers.boundedElastic()))
        .flatMap(result -> Mono.fromRunnable(() -> {
          log.info("Bounded 3 receive request");
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }).subscribeOn(Schedulers.boundedElastic()))
        .then()
        .subscribeOn(scheduler);
  }
}