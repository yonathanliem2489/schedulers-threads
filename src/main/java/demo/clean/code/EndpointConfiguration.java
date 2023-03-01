package demo.clean.code;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.clean.code.model.RedBusSeatLayoutResponse;
import demo.clean.code.service.DefaultVariableService;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Configuration
public class EndpointConfiguration {

  private final Scheduler tripDetailsScheduler = Schedulers.newBoundedElastic(2000, 100,
      "tripDetailsScheduler", 60);

  @Bean
  RouterFunction<ServerResponse> getSeatLayout() throws JsonProcessingException {
    DefaultVariableService defaultVariableService = new DefaultVariableService();

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/rest/v2/tripdetails"))
        .and(accept(MediaType.APPLICATION_JSON));

    ObjectMapper objectMapper = new ObjectMapper();
    String resp = "{\"seats\":[{\"name\":\"CD\",\"row\":3,\"column\":0,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":0,\"baseFare\":0,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"available\":false,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0,\"seatType\":\"SEATER\"},{\"name\":\"CB\",\"row\":4,\"column\":0,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":0,\"baseFare\":0,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"available\":false,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0,\"seatType\":\"SEATER\"},{\"name\":\"1A\",\"row\":4,\"column\":1,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"1B\",\"row\":3,\"column\":1,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"1C\",\"row\":1,\"column\":1,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"1D\",\"row\":0,\"column\":1,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":false,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"2A\",\"row\":4,\"column\":2,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"2B\",\"row\":3,\"column\":2,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"2C\",\"row\":1,\"column\":2,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"2D\",\"row\":0,\"column\":2,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"3A\",\"row\":4,\"column\":3,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"3B\",\"row\":3,\"column\":3,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"3C\",\"row\":1,\"column\":3,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"3D\",\"row\":0,\"column\":3,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"4A\",\"row\":4,\"column\":4,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"4B\",\"row\":3,\"column\":4,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"4C\",\"row\":1,\"column\":4,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"4D\",\"row\":0,\"column\":4,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"5A\",\"row\":4,\"column\":5,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"5B\",\"row\":3,\"column\":5,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"5C\",\"row\":1,\"column\":5,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"5D\",\"row\":0,\"column\":5,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"6A\",\"row\":4,\"column\":6,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"6B\",\"row\":3,\"column\":6,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"6C\",\"row\":1,\"column\":6,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"6D\",\"row\":0,\"column\":6,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"7A\",\"row\":4,\"column\":7,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"7B\",\"row\":3,\"column\":7,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"7C\",\"row\":1,\"column\":7,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"7D\",\"row\":0,\"column\":7,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"8C\",\"row\":1,\"column\":8,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"},{\"name\":\"8D\",\"row\":0,\"column\":8,\"zIndex\":0,\"length\":1,\"width\":1,\"fare\":290800.00,\"baseFare\":290800.00,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0.00,\"available\":true,\"ladiesSeat\":false,\"operatorServiceChargePercent\":0,\"operatorServiceChargeAbsolute\":0.00,\"seatType\":\"SEATER\"}],\"busAmenities\":[],\"categoryDetails\":{\"categories\":[]},\"departureTime\":410,\"arrivalTime\":990,\"currencyType\":\"IDR\",\"agentCurrency\":\"IDR\"}";
    RedBusSeatLayoutResponse redBusSeatLayoutResponse = objectMapper.readValue(resp, RedBusSeatLayoutResponse.class);

    HandlerFunction<ServerResponse> handlerFunction = request -> Mono.fromCallable(() -> {
          log.info("start request");
          Thread.sleep(5000L);
          return redBusSeatLayoutResponse;
        })
        .flatMap(response -> ServerResponse.ok().bodyValue(response))
        .doOnNext(res -> log.info("finish request"))
        .subscribeOn(tripDetailsScheduler);

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> getLocationPoint() {
    DefaultVariableService defaultVariableService = new DefaultVariableService();

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/clean-code/variable"))
        .and(accept(MediaType.APPLICATION_JSON));

    HandlerFunction<ServerResponse> handlerFunction = request -> defaultVariableService.handle(
                getParams(String.class, request.queryParam("name")),
            getParams(Integer.class, request.queryParam("age")))
        .then(ServerResponse.ok().build());

    return route(requestPredicate, handlerFunction);
  }

  protected <T> T getParams(Class<T> tClass, Optional<String> optionalParams) {

    if(tClass.isAssignableFrom(Float.class)) {
      return optionalParams.map(s -> tClass.cast(Float.parseFloat(s)))
          .orElseGet(() -> tClass.cast(null));
    }

    if(tClass.isAssignableFrom(Integer.class)) {
      return optionalParams.map(s -> tClass.cast(Integer.parseInt(s)))
          .orElseGet(() -> tClass.cast(null));
    }

    if(tClass.isAssignableFrom(String.class)) {
      return optionalParams.map(s -> tClass.cast(optionalParams.get()))
          .orElse(null);
    }

    if(tClass.isAssignableFrom(Boolean.class)) {
      return optionalParams.map(s -> tClass.cast(Boolean.parseBoolean(optionalParams.get())))
          .orElseGet(() -> tClass.cast(null));
    }

    if(tClass.isAssignableFrom(BigDecimal.class)) {
      return optionalParams.map(s -> tClass.cast(BigDecimal.valueOf(Double.parseDouble(optionalParams.get()))))
          .orElseGet(() -> tClass.cast(null));
    }

    return null;
  }
}
