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
public class CustomThreadsService {

  @Value("${demo.clean.code.custom-threads.capacity:200}")
  private int capacity;
  @Value("${demo.clean.code.custom-threads.corePoolSize:100}")
  private int corePoolSize;
  @Value("${demo.clean.code.custom-threads.maxPoolSize:50}")
  private int maxPoolSize;
  @Value("${demo.clean.code.custom-threads.keepAliveTime:5000}")
  private long keepAliveTime;

  private final ThreadPoolExecutor executor;

  private final Duration pollingInterval;

  private final Duration pollingTimeout;

  private final double maxPoolSizeLimitByPercent;

  public CustomThreadsService(ThreadPoolExecutor executor, Duration pollingInterval,
      Duration pollingTimeout, double maxPoolSizeLimitByPercent) {
    this.executor = executor;
    this.pollingInterval = pollingInterval;
    this.pollingTimeout = pollingTimeout;
    this.maxPoolSizeLimitByPercent = maxPoolSizeLimitByPercent;
  }

  public Mono<Void> handle() {
    String uuid = UUID.randomUUID().toString();
    log.info("custom thread receive request, uuid {}", uuid);
//    return Mono.fromRunnable(() -> {
//      try {
//        Thread.sleep(2000L);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//      log.info("custom thread end process, uuid {}", uuid);
//    }).then().subscribeOn(Schedulers.fromExecutor(executor));

    return managementThreads(Mono.fromRunnable(() -> {
      try {
        Thread.sleep(2000L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      log.info("custom thread end process, uuid {}", uuid);
    }).then().subscribeOn(Schedulers.fromExecutor(executor)));
  }


  public <T> Mono<T> managementThreads(Mono<T> mono) {
    int maxPoolSizeLimit = (int) ((maxPoolSizeLimitByPercent / 100) * executor.getMaximumPoolSize());

    return Mono.fromCallable(executor::getActiveCount)
        .filter(activeCount -> activeCount < maxPoolSizeLimit)
        .repeatWhenEmpty(counterStream -> counterStream
            .delayElements(pollingInterval)
            .flatMap(pollCounter -> Mono.subscriberContext()
                .flatMap(subscribeContext -> Mono.<Long>create(counterSink -> {
                  if(subscribeContext.get(LocalDateTime.class).isAfter(LocalDateTime.now())) {
                    log.info("waiting check available thread, counter {}", pollCounter);
                    counterSink.success(pollCounter);
                  }
                  else {
                    counterSink.error(new IllegalAccessError("error timeout"));
                  }
                }))
            )
        )
        .flatMap(activeCount -> mono)
        .subscriberContext(context -> context.put(LocalDateTime.class, now().plus(pollingTimeout)));
  }
}
