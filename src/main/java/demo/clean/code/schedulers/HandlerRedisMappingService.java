package demo.clean.code.schedulers;

import demo.clean.code.redis.MonoRedisTemplate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class HandlerRedisMappingService {

  private final MonoRedisTemplate monoRedisTemplate;

  private final Scheduler scheduler;

  private final Scheduler schedulerMapping;

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  public HandlerRedisMappingService(MonoRedisTemplate monoRedisTemplate,
      ReactiveMongoTemplate reactiveMongoTemplate, Scheduler scheduler, Scheduler schedulerMapping) {
    this.monoRedisTemplate = monoRedisTemplate;
    this.scheduler = scheduler;
    this.reactiveMongoTemplate = reactiveMongoTemplate;
    this.schedulerMapping = schedulerMapping;
  }

  public Mono<RedisMapping> handle(boolean isDelay) {
    return Mono.just("request")
        .doOnNext(res -> log.info("receive request, status delay {}", isDelay))
        .subscribeOn(scheduler) // toggle for test
        .flatMap(res -> {
          UUID uuid = UUID.randomUUID();
          RedisMapping redisMapping = RedisMapping.builder()
              .name(uuid.toString())
              .build();
          return monoRedisTemplate.getCache(uuid.toString())
              .map(resRedis -> redisMapping)
              .doOnNext(redisRes -> log.info("cache exist {}", redisRes))
              .switchIfEmpty(monoRedisTemplate.put(uuid.toString(), "abc", Duration.ofSeconds(60))
                  .doOnSuccess(resRedisPut -> log.info("result of redis put before set main thread"))
                  .publishOn(scheduler) // toggle for test
                  .doOnSuccess(resRedisPut -> log.info("result of redis put after set main thread"))
                  .flatMap(resPut -> reactiveMongoTemplate.save(redisMapping)
                      .doOnSuccess(resMongo -> log.info("save mongo"))
                      .flatMap(redisMappingRes -> reactiveMongoTemplate
                          .remove(Query.query(Criteria.where("_id").is(redisMappingRes.getId())), RedisMapping.class)
                          .thenReturn(redisMapping)
                      )
                  )
              );
        })
//        .flatMap(res -> {
//          UUID uuid = UUID.randomUUID();
//          RedisMapping redisMapping = RedisMapping.builder()
//              .name(uuid.toString())
//              .build();
//          return reactiveMongoTemplate.save(redisMapping)
//                      .doOnSuccess(resMongo -> log.info("save mongo"))
//                      .flatMap(redisMappingRes -> reactiveMongoTemplate
//                          .remove(Query.query(Criteria.where("_id")
//                              .is(redisMappingRes.getId())), RedisMapping.class)
//                          .thenReturn(redisMapping)
//              );
//        })
        .flatMap(redisMapping -> {

          if(isDelay) {
            try {
              Thread.sleep(10000L);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }

          return Mono.just(redisMapping);
        })
        .publishOn(scheduler) // toggle for test
        // next jangan sampe pake thread lettuce
//        .flatMap(redisMapping -> Mono.just(redisMapping).subscribeOn(schedulerMapping)) // set for expected manage schedulers
        .doOnSuccess(redisMapping -> log.info("success processing"));
  }

  @Document(
      collection = "redisMapping"
  )
  @Getter
  @ToString
  public static class RedisMapping {
    @Id
    private ObjectId id;

    private String name;

    @lombok.Builder(builderClassName = "Builder")
    public RedisMapping(ObjectId id, String name) {
      this.id = id;
      this.name = name;
    }
  }
}
