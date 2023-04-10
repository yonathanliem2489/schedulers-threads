package demo.clean.code;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import demo.clean.code.schedulers.MonoDeferService;
import demo.clean.code.schedulers.config.SchedulersUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Scheduler;

@Configuration
public class MonoEndpointConfiguration {

  @Bean
  Scheduler monoDeferEndpointScheduler() {
    String threadName = "monoDeferEndpointScheduler";
    return SchedulersUtils
        .boundedElastic(threadName, 1000, 50, 30, false);
  }

  @Bean
  RouterFunction<ServerResponse> monoWithDeferEndpoint(Scheduler monoDeferEndpointScheduler) {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/mono-with-defer"))
        .and(accept(MediaType.APPLICATION_JSON));



    MonoDeferService monoDeferService = new MonoDeferService(monoDeferEndpointScheduler);
    HandlerFunction<ServerResponse> handlerFunction = request -> monoDeferService.withDefer(getParamsNumber(request))
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> monoWithoutDeferEndpoint(Scheduler monoDeferEndpointScheduler) {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/mono-without-defer"))
        .and(accept(MediaType.APPLICATION_JSON));

    MonoDeferService monoDeferService = new MonoDeferService(monoDeferEndpointScheduler);
    HandlerFunction<ServerResponse> handlerFunction = request -> monoDeferService.withoutDefer(getParamsNumber(request))
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  private int getParamsNumber(ServerRequest request) {
    return Integer.parseInt(request.queryParam("number").orElse("0"));
  }
}
