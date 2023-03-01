package demo.clean.code.service;

import demo.clean.code.service.DefaultTrackingService.Request;
import demo.clean.code.service.DefaultTrackingService.Response;
import reactor.core.publisher.Mono;

public class DefaultTrackingService extends BaseTrackingService<Request, Response> {

  @Override
  protected Mono<Response> perform(Request request) {
    return null;
  }


  public static class Request {}

  public static class Response {}
}
