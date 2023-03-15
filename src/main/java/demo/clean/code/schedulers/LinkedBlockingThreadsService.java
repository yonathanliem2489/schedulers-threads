package demo.clean.code.schedulers;

import static java.time.LocalDateTime.now;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class LinkedBlockingThreadsService {

  private final ThreadPoolExecutor executor;

  private final Duration pollingInterval;

  private final Duration pollingTimeout;

  private final double maxPoolSizeLimitByPercent;

  public LinkedBlockingThreadsService(ThreadPoolExecutor executor, Duration pollingInterval,
      Duration pollingTimeout, double maxPoolSizeLimitByPercent) {
    this.executor = executor;
    this.pollingInterval = pollingInterval;
    this.pollingTimeout = pollingTimeout;
    this.maxPoolSizeLimitByPercent = maxPoolSizeLimitByPercent;
  }

  public Mono<Void> handle() {
    String uuid = UUID.randomUUID().toString();
    log.info("linked blocking thread receive request, uuid {}", uuid);
    return Mono.fromRunnable(() -> {
      try {
        Thread.sleep(2000L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      log.info("linked blocking thread end process, uuid {}", uuid);
    }).then().subscribeOn(Schedulers.fromExecutor(executor));

  }
}
