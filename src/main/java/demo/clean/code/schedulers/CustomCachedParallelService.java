package demo.clean.code.schedulers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class CustomCachedParallelService {

  private final Scheduler scheduler;

  public CustomCachedParallelService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Mono<Void> handle() {
    return Flux.range(0, 20)
        .parallel(10)
        .runOn(scheduler)
        .groups()
        .flatMapDelayError(groupedFlux -> groupedFlux
                .flatMap(tripId -> Mono.fromRunnable(() -> {
                      log.info("cached Parallel receive request");
                      try {
                        Thread.sleep(3000L);
                      } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                      }

                      log.info("cached Parallel end process");
                    })
                ),
            10, 10
        )
        .collectList()
        .then();
  }
}