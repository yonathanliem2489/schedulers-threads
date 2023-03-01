package demo.clean.code.schedulers;

import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ParallelService {

    private final Scheduler scheduler = Schedulers.parallel();

    public Mono<Void> handle() {
      return Mono.fromRunnable(() -> {
            log.info("parallel receive request");
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