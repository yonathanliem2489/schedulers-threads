package demo.clean.code.service;

import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;

@Slf4j
public class ParallelAndZipTest {

  private final Scheduler parallel = Schedulers.parallel();

  @Test
  public void submitSchedulersParallel_thenSuccess() {
    Mono.fromCallable(() -> {
      log.info("init process");
      return true;
    })
    .flatMap(res -> Flux.fromIterable(Arrays.asList(new TestObjectA("anji"),
            new TestObjectB("string"))) // request
        .parallel(2)
        .runOn(parallel)
        .groups()
        .flatMap(groupFlux -> groupFlux.flatMap(resGrp -> generateGrp(resGrp, resGrp.getClass())))
        .collectList()
        .flatMap(responses -> {
          AtomicReference<TestObjectA> resObjectA = new AtomicReference<>();
          AtomicReference<TestObjectB> resObjectB = new AtomicReference<>();

          responses.forEach(response -> {
            if(response.getClass().isAssignableFrom(TestObjectA.class)) {
              resObjectA.set((TestObjectA) response);
            }

            if(response.getClass().isAssignableFrom(TestObjectB.class)) {
              resObjectB.set((TestObjectB) response);
            }
          });

          log.info("resObjectA result {}", resObjectA.get());
          log.info("resObjectB result {}", resObjectB.get());

          return Mono.empty();
        })
    )
    .then(afterParallel())
    .block();
  }

  private <T> Mono<T> generateGrp(Serializable resGrp, Class<T> tClass) {

    if(tClass.isAssignableFrom(TestObjectA.class)) { // classify by object class
      return Mono.just(resGrp)
          .doOnSuccess(resMono -> log.info("start generateGrp {} process", tClass))
          .delayElement(Duration.ofMillis(3000L))
          .cast(tClass)
          .doOnSuccess(resMono -> log.info("end generateGrp {} process", tClass));
    }

    if(tClass.isAssignableFrom(TestObjectB.class)) { // classify by object class
      return Mono.just(resGrp)
          .doOnSuccess(resMono -> log.info("start generateGrp {} process", tClass))
          .delayElement(Duration.ofMillis(3000L))
          .cast(tClass)
          .doOnSuccess(resMono -> log.info("end generateGrp {} process", tClass));
    }

    return Mono.empty();
  }

  private Mono<Boolean> afterParallel() {
    return Mono.just(true).doOnSuccess(res -> log.info("mono after parallel process"))
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Test
  public void submitSchedulersMonoZip_thenSuccess() {
    Mono<Tuple4<Boolean, Boolean, Boolean, Boolean>> mono = Mono.fromCallable(() -> {
          log.info("init process");
          return true;
        })
        .flatMap(res -> Mono.zip(monoWithDelay(), monoWithDelay(), monoWithDelay(), monoWithDelay())
        );
    StepVerifier.create(mono).expectSubscription().thenAwait()
        .expectNextMatches(res -> {
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void submitSchedulersMultipleMono_thenSuccess() {
    Mono<Boolean> mono = Mono.fromCallable(() -> {
          log.info("init process");
          return true;
        })
        .flatMap(res -> monoWithDelay()
            .flatMap(res1 -> monoWithDelay()
                .flatMap(res2 -> monoWithDelay()
                    .flatMap(res3 -> monoWithDelay())))
        );
    StepVerifier.create(mono).expectSubscription().thenAwait()
        .expectNextMatches(res -> {
          return true;
        })
        .verifyComplete();
  }

  private Mono<Boolean> monoWithDelay() {
    return Mono.just(true)
        .doOnSuccess(resMono -> log.info("start monoWithDelay process"))
        .flatMap(dt -> outboundService())
        .doOnSuccess(resMono -> log.info("end monoWithDelay process"));
  }

  @Test
  public void submitSchedulersMonoZipWith_thenSuccess() {
    Mono<Tuple2<Boolean, Boolean>> mono = Mono.fromCallable(() -> {
          log.info("init process");
          return true;
        })
        .flatMap(res -> monoWithDelay()
            .zipWith(monoWithDelay(), (aa, bb) -> true)
            .zipWith(monoWithDelay())
            .subscribeOn(Schedulers.boundedElastic()));
    StepVerifier.create(mono).expectSubscription().thenAwait()
        .expectNextMatches(res -> {
          return true;
        })
        .verifyComplete();
  }

  @Getter
  @ToString
  @AllArgsConstructor
  public class TestObjectA implements Serializable {
    private String name;
  }

  @Getter
  @ToString
  @AllArgsConstructor
  public class TestObjectB implements Serializable {
    private String name;
  }

  private Mono<Boolean> outboundService() {
    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8510/zip-with-delay")
        .build();

    return webClient.get()
        .exchangeToMono(clientResponse -> Mono.just(true).doOnSuccess(rs -> log.info("receive response")));
  }
}
