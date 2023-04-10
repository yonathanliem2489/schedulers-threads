package demo.clean.code.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

@Slf4j
public class SwitchIfEmptyTest {

  private boolean isActive = true;

  @Test
  public void filterTest() {

    int num = 1;
    Mono.deferContextual(context ->
        Mono.just(isActive)
            .filter(Boolean::booleanValue)
            .flatMap(active -> mono(num)
                .doOnSuccess(res -> log.info("mono executed when filter true")))
            .switchIfEmpty(mono(num)
                .doOnSuccess(res -> log.info("mono executed when filter false")))
            .map(res -> {
              log.info("result {}", res);
              return res;
            })
    ).block();
  }

  private Mono<Integer> mono(int num) {
    return Mono.just(num);
  }
}
