package demo.clean.code.schedulers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import demo.clean.code.redis.MonoRedisTemplate;
import java.io.Serializable;
import java.time.Duration;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class HandlerRedisMappingService {

  private final Scheduler boundedElastic = Schedulers.boundedElastic();

  private final MonoRedisTemplate monoRedisTemplate;

  private final Scheduler scheduler;

  private final Scheduler schedulerMapping;

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  private final WebClient webClient;

  public HandlerRedisMappingService(WebClient webClient, MonoRedisTemplate monoRedisTemplate,
      ReactiveMongoTemplate reactiveMongoTemplate, Scheduler scheduler, Scheduler schedulerMapping) {
    this.webClient = webClient;
    this.monoRedisTemplate = monoRedisTemplate;
    this.scheduler = scheduler;
    this.reactiveMongoTemplate = reactiveMongoTemplate;
    this.schedulerMapping = schedulerMapping;
  }

  public Mono<Void> handle(boolean isDelay) {

    return Flux.range(0, 2000)
        .flatMap(req -> {
          UUID uuid = UUID.randomUUID();
          RedisMapping redisMapping = RedisMapping.builder()
              .name(uuid.toString())
              .build();
          return monoRedisTemplate.put(uuid.toString(), redisMapping, Duration.ofSeconds(30))
              .doOnSuccess(resRedisPut -> log.info("after call webclient - result of redis put before set main thread"))
//            .publishOn(boundedElastic) // toggle for test
              .doOnSuccess(resRedisPut -> log.info("after call webclient - result of redis put after set main thread"))
              .flatMap(resRedisPut -> monoRedisTemplate.getCache(uuid.toString(), RedisMapping.class)
                  .doOnSuccess(resRedisGet -> log.info("redis after call get {}", resRedisGet)));
        })
        .collectList()
        .then()


//    return Mono.just("request")
//        .doOnNext(res -> log.info("receive request, status delay {}", isDelay))
////        .subscribeOn(scheduler) // toggle for test
//        .flatMap(res -> {
//          return monoRedisTemplate.getCache(uuid.toString())
//              .publishOn(scheduler) // toggle for test
//              .map(resRedis -> redisMapping)
//              .doOnNext(redisRes -> log.info("cache exist {}", redisRes))
//              .switchIfEmpty(reactiveMongoTemplate.save(redisMapping)
//                      .doOnSuccess(resMongo -> log.info("save mongo"))
//                      .flatMap(redisMappingRes -> reactiveMongoTemplate
//                          .remove(Query.query(Criteria.where("_id").is(redisMappingRes.getId())), RedisMapping.class)
//                          .then(monoRedisTemplate.put(uuid.toString(), "abc", Duration.ofSeconds(60))
//                              .doOnSuccess(resRedisPut -> log.info("result of redis put before set main thread"))
//                               .publishOn(scheduler) // toggle for test
//                              .doOnSuccess(resRedisPut -> log.info("result of redis put after set main thread")))
//                          .thenReturn(redisMapping)
//                      )
//              );
//        })
//        .flatMap(redisMapping -> {
//          if(isDelay) {
//
//            log.info("before call web client");
//            return webClient.get().exchangeToMono(clientResponse -> Mono.just(""))
//                .then(monoRedisTemplate.put(uuid.toString(), "abc", Duration.ofSeconds(60))
//                    .doOnSuccess(resRedisPut -> log.info("after call webclient - result of redis put before set main thread"))
//                    .publishOn(scheduler) // toggle for test
//                    .doOnSuccess(resRedisPut -> log.info("after call webclient - result of redis put after set main thread")))
//                .then(Mono.just(redisMapping));
//          }
//
//          return Mono.just(redisMapping);
//        })
        // next jangan sampe pake thread lettuce
//        .flatMap(redisMapping -> Mono.just(redisMapping).subscribeOn(schedulerMapping)) // set for expected manage schedulers
        .doOnSuccess(mapping -> log.info("success processing"));
  }


  @Getter
  @ToString
  @EqualsAndHashCode
  public static class RedisMapping implements Serializable {

    @JsonProperty("name")
    private String name;

    @JsonCreator
    @lombok.Builder(builderClassName = "Builder")
    public RedisMapping(@JsonProperty("name") String name) {
      this.name = name;
    }
  }
}
