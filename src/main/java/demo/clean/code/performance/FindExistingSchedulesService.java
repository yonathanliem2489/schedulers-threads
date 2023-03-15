package demo.clean.code.performance;

import demo.clean.code.dao.api.SchedulesRepositoryCustom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class FindExistingSchedulesService {

  private final Scheduler scheduler;

  private final SchedulesRepositoryCustom schedulesRepositoryCustom;

  public FindExistingSchedulesService(Scheduler scheduler,
      SchedulesRepositoryCustom schedulesRepositoryCustom) {
    this.scheduler = scheduler;
    this.schedulesRepositoryCustom = schedulesRepositoryCustom;
  }

  public Mono<Void> handle(Optional<String> scheduleIds) {
    return Mono.fromCallable(() -> {
//          log.info("Bounded receive request");
//          try {
//            Thread.sleep(1000L);
//          } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//          }
//
//          log.info("Bounded request end");

          return true;
        })
        .subscribeOn(scheduler)
        .doOnNext(result -> log.info("extend Bounded request end"))
        .then()
        .then(schedulesRepositoryCustom
            .findExistingSchedules2(new ArrayList<>(Stream.of(scheduleIds.get().split(",")).collect(
                Collectors.toList())))
            .collectList()
            .elapsed()
            .doOnNext(res -> log.info("findExistingSchedules elapsed time {}, count schedules {}",
                res.getT1(), res.getT2().size()))
        .then());
  }
}