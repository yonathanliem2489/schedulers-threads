package demo.clean.code.schedulers;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class MonoDeferService {

  private static List<Integer> staticNumbers = Arrays.asList(100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110);

  private static Scheduler scheduler;

  public MonoDeferService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Mono<Boolean> withDefer(int num) {
    return Flux.fromIterable(staticNumbers)
        .filter(res -> res==num)
        .doOnNext(res -> log.info("withDefer number is match"))
        .switchIfEmpty(Mono.defer(() -> monoWithDelay(num)
            .doOnSuccess(res -> log.info("is withDefer"))))
        .collectList()
        .thenReturn(true)
        .subscribeOn(scheduler);
  }

  private Mono<Integer> monoWithDelay(int num) {
    return Mono.just(num);
  }

  public Mono<Boolean> withoutDefer(int num) {
    return Flux.fromIterable(staticNumbers)
        .filter(res -> res==num)
        .doOnNext(res -> log.info("withoutDefer number is match"))
        .switchIfEmpty(monoWithDelay(num)
            .doOnSuccess(res -> log.info("is withoutDefer")))
        .collectList()
        .thenReturn(true)
        .subscribeOn(scheduler);
  }
}
