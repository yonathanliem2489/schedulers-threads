package demo.clean.code.schedulers;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class TiketThreadsService {


  public TiketThreadsService() {
  }

  public Mono<Void> handle() {
    return Mono.fromRunnable(() -> {
      log.info("tiket thread receive request");
      try {
        Thread.sleep(3000L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      log.info("tiket thread end process");
    }).then();
  }

}
