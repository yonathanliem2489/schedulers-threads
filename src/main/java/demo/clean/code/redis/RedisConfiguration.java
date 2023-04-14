package demo.clean.code.redis;

import static io.lettuce.core.protocol.ProtocolVersion.RESP2;

import com.tiket.tix.bus.model.common.metric.utility.SubmitMetric;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;
import io.lettuce.core.resource.ClientResources;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisConfigurationProperties.class)
public class RedisConfiguration {

  @Autowired
  private RedisConfigurationProperties redisConfigurationProperties;


  @Bean
  public RedisStandaloneConfiguration redisStandaloneConfiguration() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(redisConfigurationProperties.getHost());
    redisStandaloneConfiguration.setPort(redisConfigurationProperties.getPort());
    Optional.ofNullable(redisConfigurationProperties.getPassword())
        .ifPresent(redisStandaloneConfiguration::setPassword);
    return redisStandaloneConfiguration;
  }

  @Bean
  public ClientOptions clientOptions(){
    return ClientOptions.builder()
        .protocolVersion(RESP2)
        .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS)
        .autoReconnect(true)
        .build();
  }

  @Bean
  LettucePoolingClientConfiguration lettucePoolConfig(ClientOptions options, ClientResources dcr){
    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    poolConfig.setMaxIdle(redisConfigurationProperties.getMaxIdle());
    poolConfig.setMaxTotal(redisConfigurationProperties.getMaxTotal());
    poolConfig.setMinIdle(redisConfigurationProperties.getMinIdle());
    return LettucePoolingClientConfiguration.builder()
        .poolConfig(poolConfig)
        .clientOptions(options)
        .clientResources(dcr)
        .build();
  }

  @Bean
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
      LettucePoolingClientConfiguration lettucePoolConfig) {
    return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
  }

  @Bean
  public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
      ResourceLoader resourceLoader) {
    JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(
        Objects.requireNonNull(resourceLoader.getClassLoader()));

    RedisSerializationContext<String, Object> serializationContext =
        RedisSerializationContext
            .<String, Object>newSerializationContext()
            .key(new StringRedisSerializer())
            .value(jdkSerializer)
            .hashKey(jdkSerializer)
            .hashValue(jdkSerializer)
            .build();

    return new ReactiveRedisTemplate<>(
        reactiveRedisConnectionFactory,
        serializationContext
    );
  }

  @Bean
  @Primary
  JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
    return new JedisConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplateCustom(JedisConnectionFactory jedisConnectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory);
    template.setKeySerializer(RedisSerializer.string());
    template.setValueSerializer(RedisSerializer.string());


    return template;
  }

  @Bean
  public MonoRedisTemplate monoRedisTemplate(
    ReactiveRedisTemplate<String, Object> reactiveRedisTemplate,
      RedisTemplate<String, String> redisTemplateCustom){
    return new MonoRedisTemplate(reactiveRedisTemplate, redisTemplateCustom);
  }
}
