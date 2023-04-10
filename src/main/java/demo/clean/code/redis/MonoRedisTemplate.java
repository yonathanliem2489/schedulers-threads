package demo.clean.code.redis;

import com.tiket.tix.bus.model.common.metric.Monitor;
import com.tiket.tix.bus.model.common.metric.utility.SubmitMetric;
import demo.clean.code.exception.BusinessLogicException;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoRedisTemplate {
  private static final Logger LOGGER = LoggerFactory.getLogger(MonoRedisTemplate.class);

  private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;


  @Value("${bus.shuttle.supply.redis.metric-status:true}")
  private boolean metricStatus;


  public MonoRedisTemplate(
                           ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
    this.reactiveRedisTemplate = reactiveRedisTemplate;
  }

  public Mono<Boolean> evict(String key) {
    return reactiveRedisTemplate
      .opsForValue()
      .delete(key)
      .onErrorResume(throwable -> Mono.error(new BusinessLogicException("SYSTEM_ERROR", "system error")))
      .defaultIfEmpty(false);
  }

  public Flux<Object> getSet(String key) {
    LOGGER.info("Read from redis, key: {}", key);
    return reactiveRedisTemplate
      .opsForSet()
      .members(key)
      .onErrorResume(throwable ->
        Flux.error(new BusinessLogicException("SYSTEM_ERROR", "system error"))
      ).defaultIfEmpty(Flux.error(new BusinessLogicException("DATA_NOT_EXIST", "data not exist")));
  }

  public Mono<Object> getCache(String key) {
    LOGGER.info("getCache from redis, key: {}", key);
    return reactiveRedisTemplate
      .opsForValue()
      .get(key)
      .onErrorResume(throwable -> Mono.just(false));
  }

  public Mono<Boolean> put(String key, Object value, Duration duration) {
    LOGGER.info("Put to redis, key: {} ttl: {} seconds", key, duration.getSeconds());
    return reactiveRedisTemplate
      .opsForValue()
      .set(key, value, duration)
        .doOnError(throwable -> LOGGER.error("error", throwable))
      .onErrorResume(throwable -> Mono.just(false));
  }

  public Mono<Boolean> isExist(String key){
    LOGGER.info("check if cache with key: {} is exists", key);
    return reactiveRedisTemplate.hasKey(key);
  }
}
