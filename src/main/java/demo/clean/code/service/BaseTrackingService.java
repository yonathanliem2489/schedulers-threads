package demo.clean.code.service;

import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;


public abstract class BaseTrackingService<T, V> {

  public Mono<T> handleMono(Mono<T> mono, String entityId, boolean metricStatus) {
    return Mono.defer(() -> {
      long startTime = System.nanoTime();
      return mono.doOnError((throwable) -> {
        this.submitLogMetric(entityId, startTime, SignalType.ON_ERROR, metricStatus);
      }).doOnSuccess((order) -> {
        this.submitLogMetric(entityId, startTime, SignalType.ON_COMPLETE, metricStatus);
      });
    });
  }

  private void submitLogMetric(String entityId, long startTime, SignalType signalType, boolean metricStatus) {
    if (Boolean.TRUE.equals(metricStatus)) {
      ErrorCode errorCode = ErrorCode.SUCCEED;
      if (SignalType.ON_ERROR.equals(signalType)) {
        errorCode = ErrorCode.FAILED;
      }

      long elapsedTime = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime,
          TimeUnit.NANOSECONDS);
      System.out.println("entityId ".concat(entityId)
          .concat(" elapsedTime ").concat(String.valueOf(elapsedTime))
          .concat(" errorCode ").concat(errorCode.name()));
    }

  }

  protected abstract Mono<V> perform(T request);

  public enum ErrorCode {
    SUCCEED, FAILED
  }
}
