package demo.clean.code.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.instrument.web.WebFluxSleuthOperators;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class DefaultVariableService {

  Logger logger = LoggerFactory.getLogger(DefaultVariableService.class);


  private Profile profile;

  private ConcurrentHashMap<String, Profile> mapProfile;

  public Mono<Void> handle(String name, int age) {

    logger.info("retrieve request name {}, age {}", name, age);

    return Mono.fromCallable(() -> {

          Profile profile2 = Profile.builder()
              .name(name)
              .age(age)
              .localDateTime(LocalDateTime.now())
              .build();

          this.profile = profile2;

          ConcurrentHashMap<String, Profile> mapProfile2 = new ConcurrentHashMap<>();
          mapProfile2.put(name, profile2);
          this.mapProfile = mapProfile2;

          return profile;
        })
        .map(profile1 -> {
          // 500 milisecond is process time to get outbound, data mongo, redis etc
          try {
            Thread.sleep(2000L);
          } catch (InterruptedException e) {

            throw new RuntimeException(e);
          }

          return profile1;
        })
        .then(buildResponse(name, age))
        .doOnNext(result -> WebFluxSleuthOperators
            .withSpanInScope(SignalType.ON_NEXT, signal ->
                logger.info("Hello from simple [{}]", signal.get())))
        .subscribeOn(Schedulers.newSingle("modifiersVariable"));
  }

  private Mono<Void> buildResponse(String name, int age) {
//    logger.info("request name {} age {}, dateTime {} "
//            + "variable result name {}, age {}",
//        name, age,
//        Objects.nonNull(this.profile) ? this.profile.getLocalDateTime() : null,
//        Objects.nonNull(this.profile) ? this.profile.getName() : null,
//        Objects.nonNull(this.profile) ? this.profile.getAge() : null);


    logger.info("request map name {} age {}, dateTime {} "
            + "variable result name {}, age {}",
        name, age,
        Objects.nonNull(this.mapProfile) ? this.mapProfile.get(name).getLocalDateTime() : null,
        Objects.nonNull(this.mapProfile) ? this.mapProfile.get(name).getName() : null,
        Objects.nonNull(this.mapProfile) ? this.mapProfile.get(name).getAge() : null);

    return Mono.empty();
  }

  @Value
  private static class Profile {
    private String name;

    private int age;

    private LocalDateTime localDateTime;

    @lombok.Builder(builderClassName = "Builder")
    public Profile(String name, int age, LocalDateTime localDateTime) {
      this.name = name;
      this.age = age;
      this.localDateTime = localDateTime;
    }
  }

}
