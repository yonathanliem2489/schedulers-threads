package demo.clean.code.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Mono;

public class MonoRedisTemplate {

  @Autowired
  private ObjectMapper mapper;

  private static final Logger LOGGER = LoggerFactory.getLogger(MonoRedisTemplate.class);

  private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

  private RedisTemplate<String, Object> redisTemplateCustom;

  @Value("${bus.shuttle.supply.redis.metric-status:true}")
  private boolean metricStatus;


  public MonoRedisTemplate(
                           ReactiveRedisTemplate<String, Object> reactiveRedisTemplate,
      RedisTemplate<String, Object> redisTemplateCustom) {
    this.reactiveRedisTemplate = reactiveRedisTemplate;
    this.redisTemplateCustom = redisTemplateCustom;
  }



  public <T> Mono<T> getCache(String key, Class<T> tClass) {
    LOGGER.info("getCache from redis, key: {}", key);
    return Mono.fromCallable(() -> redisTemplateCustom.opsForValue().get(key))
        .map(tClass::cast)
        .doOnSuccess(result -> LOGGER.info("jedis success getCache from redis, key: {}", key));
//    return reactiveRedisTemplate
//      .opsForValue()
//      .get(key)
//      .map(tClass::cast)
//      .doOnSuccess(result -> LOGGER.info("lettuce success getCache from redis, key: {}", key))
//      .onErrorResume(throwable -> Mono.error(new Exception(throwable.getMessage())));
  }

  public Mono<Boolean> put(String key, Object value, Duration duration) {
    LOGGER.info("Put to redis, key: {} ttl: {} seconds", key, duration.getSeconds());

    return Mono.fromRunnable(() -> redisTemplateCustom.opsForValue().set(key, value, duration))
        .doOnSuccess(result -> LOGGER.info("jedis success Put to redis, key: {} ttl: {} seconds", key, duration.getSeconds()))
        .doOnError(throwable -> LOGGER.error("error", throwable))
        .onErrorResume(throwable -> Mono.just(false))
        .thenReturn(true);

//    return reactiveRedisTemplate
//      .opsForValue()
//      .set(key, value, duration)
//      .doOnSuccess(result -> LOGGER.info("lettuce success Put to redis, key: {} ttl: {} seconds", key, duration.getSeconds()))
//        .doOnError(throwable -> LOGGER.error("error", throwable))
//      .onErrorResume(throwable -> Mono.just(false));
  }

}
