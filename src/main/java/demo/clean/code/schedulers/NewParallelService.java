package demo.clean.code.schedulers;

import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class NewParallelService {

  private final Scheduler scheduler = Schedulers
      .newParallel("newParallel", 1000, true);


  public Mono<Void> handle() {
    return Mono.fromRunnable(() -> {
          log.info("newParallel receive request");
          try {
            Thread.sleep(3000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          log.info("newParallel end process");
        })
        .then()
        .subscribeOn(scheduler);
  }
  }