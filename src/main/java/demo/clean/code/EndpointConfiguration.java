package demo.clean.code;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiket.tix.bus.model.common.models.dao.Schedule.AvailableTrip;
import demo.clean.code.dao.api.SchedulesRepository;
import demo.clean.code.dao.api.SchedulesRepositoryCustom;
import demo.clean.code.model.BaseResponse;
import demo.clean.code.model.RedBusSeatLayoutResponse;
import demo.clean.code.model.bus.sync.order.OrderData;
import demo.clean.code.model.bus.sync.order.OrderData.OrderCartDetailResponse;
import demo.clean.code.model.request.cart_service.CreateCartRequest;
import demo.clean.code.model.response.AvailableTripsResponse;
import demo.clean.code.model.response.LocationPointResponse;
import demo.clean.code.model.response.LocationPointsData;
import demo.clean.code.schedulers.config.SchedulersUtils;
import demo.clean.code.service.CartServiceImpl;
import demo.clean.code.service.DefaultVariableService;
import demo.clean.code.service.api.CartService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
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


  @Bean
  RouterFunction<ServerResponse> timeoutEndpoint() {
    DefaultVariableService defaultVariableService = new DefaultVariableService();

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/timeout"))
        .and(accept(MediaType.APPLICATION_JSON));

    HandlerFunction<ServerResponse> handlerFunction = request -> Mono.fromCallable(() -> {

      log.info("request incoming");

      Thread.sleep(30000);

      return true;
    })
    .then(ServerResponse.ok().build().doOnSuccess(result -> log.info("process success - end")));

    return route(requestPredicate, handlerFunction);
  }

  @Autowired
  private SchedulesRepository schedulesRepository;

  @Autowired
  private SchedulesRepositoryCustom schedulesRepositoryCustom;

//  @Bean
//  RouterFunction<ServerResponse> findExistingSchedulesEndpoint() {
//
//    String threadName = "findExistingSchedulesEndpoint";
//    Supplier<Scheduler> schedulerSupplier =
//        () -> Schedulers.newBoundedElastic(3000, 50,
//            threadName, 30, false);
//
//
//    RequestPredicate requestPredicate = method(HttpMethod.GET)
//        .and(path("/schedules/findExistingSchedules"))
//        .and(accept(MediaType.APPLICATION_JSON));
//
//    FindExistingSchedulesService findExistingSchedulesService =
//        new FindExistingSchedulesService(SchedulersUtils
//            .boundedElastic(threadName, schedulerSupplier), schedulesRepositoryCustom);
//
//    HandlerFunction<ServerResponse> handlerFunction = request ->
//          findExistingSchedulesService.handle(request.queryParam("scheduleIds"))
//        .then(ServerResponse.ok().build());
//
//    return route(requestPredicate, handlerFunction);
//  }

  @Bean
  RouterFunction<ServerResponse> createCartServiceEndpoint() {

    CartService cartService = new CartServiceImpl();

    String threadName = "createCartServiceEndpoint";

    RequestPredicate requestPredicate = method(HttpMethod.POST)
        .and(path("/create-cart"))
        .and(accept(MediaType.APPLICATION_JSON));

    HandlerFunction<ServerResponse> handlerFunction = request ->
        request.bodyToMono(CreateCartRequest.class)
            .flatMap(cartService::create)
            .then(ServerResponse.ok().build()).subscribeOn(SchedulersUtils
                .boundedElastic(threadName, 3000, 50, 30, false));

    return route(requestPredicate, handlerFunction);
  }

  @Autowired
  @Qualifier("customAvailableTripsBusSupplyEndpoint")
  private Scheduler customAvailableTripsBusSupplyEndpoint = Schedulers.elastic();

  @Bean
  RouterFunction<ServerResponse> availableTripsBusSupplyEndpoint() {

    CartService cartService = new CartServiceImpl();

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/rest/v2/availabletrips"))
        .and(accept(MediaType.APPLICATION_JSON));

    String threadName = "availableTripsBusSupplyEndpoint";

    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8510/zip-with-delay")
        .build();

    AvailableTripsService availableTripsService = new AvailableTripsService(webClient,
        SchedulersUtils.boundedElastic(threadName, 500, 10,
             30, false)
//    customAvailableTripsBusSupplyEndpoint
    );

    HandlerFunction<ServerResponse> handlerFunction = request -> availableTripsService.handle()
        .flatMap(availableTripsResponse -> ServerResponse.ok()
            .bodyValue(availableTripsResponse)
//            .map(res -> {
//              try {
//                Thread.sleep(1000L);
//              } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//              }
//
//              return res;
//            })
            .doOnSuccess(av -> log.info("after delay"))
        );

    return route(requestPredicate, handlerFunction);
  }


  @Bean
  RouterFunction<ServerResponse> bpdpDetailsBusSupplyEndpoint() throws JsonProcessingException {
    // http://localhost:8183/rest/v2/bpdpDetails?id=1076686544540097200

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/rest/v2/bpdpDetails"))
        .and(accept(MediaType.APPLICATION_JSON));

    String threadName = "bpdpDetailsBusSupplyEndpoint";

    String resp = "{\"boardingPoints\":[{\"id\":1103588,\"name\":\"Blora\",\"address\":\"Jl. Blora No. 30 RT.2/RW.6 RT.2/RW.6, RT.2/RW.6, Dukuh Atas, Menteng, Kec. Menteng, Kota Jakarta Pusat, Daerah Khusus Ibukota Ja\",\"landmark\":\"Pasteur Trans Blora\",\"contactnumber\":\"022230501234\",\"locationName\":\"Jl. Blora No. 30 RT.2/RW.6 RT.2/RW.6, RT.2/RW.6, Dukuh Atas, Menteng, Kec. Menteng, Kota Jakarta Pusat, Daerah Khusus Ibukota Ja\"},{\"id\":1103572,\"name\":\"Kuningan\",\"address\":\"Jl. H. R. Rasuna Said No.2, RT.2/RW.5, Karet Kuningan, Kecamatan Setiabudi, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta \",\"landmark\":\"Plaza Festival Kuningan\",\"contactnumber\":\"02230501234\",\"locationName\":\"Jl. H. R. Rasuna Said No.2, RT.2/RW.5, Karet Kuningan, Kecamatan Setiabudi, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta \"},{\"id\":1103576,\"name\":\"Jatiwaringin\",\"address\":\"SPBU Pertamina - Jl. Jatiwaringin Raya No. 4 A\",\"landmark\":\"Spbu Pertamina   Jl. Jatiwaringin Raya No. 4 A\",\"contactnumber\":\"022230501234\",\"locationName\":\"SPBU Pertamina - Jl. Jatiwaringin Raya No. 4 A\"}],\"droppingPoints\":[{\"id\":1103570,\"name\":\"Dipatiukur\",\"address\":\" Jl. Dipati Ukur No.100, Lebakgede, Kecamatan Coblong, Kota Bandung, Jawa Barat 40251, Indonesia\",\"landmark\":\"Rumah Makan Pondok Kapau\",\"contactnumber\":\"02230501234\",\"locationName\":\" Jl. Dipati Ukur No.100, Lebakgede, Kecamatan Coblong, Kota Bandung, Jawa Barat 40251, Indonesia\"}]}";
    ObjectMapper objectMapper = new ObjectMapper();
    LocationPointResponse locationPointResponse = objectMapper.readValue(resp, LocationPointResponse.class);



    HandlerFunction<ServerResponse> handlerFunction = request -> ServerResponse.ok()
        .bodyValue(locationPointResponse.toBuilder()
            .droppingPoints(locationPointResponse.getDroppingPoints().stream().map(drop -> drop.toBuilder()

                .build()).collect(
                Collectors.toList()))
            .build())
        .subscribeOn(SchedulersUtils.boundedElastic(threadName, 500, 10,
             30, false));

    return route(requestPredicate, handlerFunction);
  }



  private class AvailableTripsService {

    private final Scheduler scheduler;

    private final WebClient webClient;

    public AvailableTripsService(WebClient webClient, Scheduler scheduler) {
      this.scheduler = scheduler;
      this.webClient = webClient;
    }

    public Mono<AvailableTripsResponse> handle() {
      AvailableTripsResponse availableTripsResponse;
      ObjectMapper objectMapper = new ObjectMapper();
      String resp = "{\"availableTrips\":[{\"id\":1076686544540097200,\"travels\":\"Cititrans\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle Toyota HiAce\",\"busTypeId\":101,\"operator\":20362,\"routeId\":1076686500000097200,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":405,\"arrivalTime\":645,\"maxSeatsPerTicket\":3,\"journeyDuration\":240,\"boardingTimes\":[{\"bpId\":353037,\"bpName\":\"Kuningan\",\"location\":\"Kuningan\",\"time\":405,\"prime\":false,\"contactNumber\":\"WA : 62 811-1794-1234\"}],\"droppingTimes\":[{\"bpId\":352981,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":645,\"prime\":false,\"contactNumber\":\"02179171717\"}],\"availableSeats\":8,\"fares\":[160000],\"fareDetails\":[{\"baseFare\":160000,\"totalFare\":160000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":160000,\"baseFare\":160000,\"sourceFare\":160000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540094500,\"travels\":\"Cititrans\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle Toyota HiAce\",\"busTypeId\":101,\"operator\":20362,\"routeId\":1076686500000094500,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":405,\"arrivalTime\":645,\"maxSeatsPerTicket\":3,\"journeyDuration\":240,\"boardingTimes\":[{\"bpId\":352982,\"bpName\":\"Fatmawati\",\"location\":\"Fatmawati\",\"time\":405,\"prime\":false,\"contactNumber\":\"62 811-1794-1234\"}],\"droppingTimes\":[{\"bpId\":352822,\"bpName\":\"Dipatiukur\",\"location\":\"Dipatiukur\",\"time\":645,\"prime\":false,\"contactNumber\":\"02179171717 / 081299888565\"}],\"availableSeats\":8,\"fares\":[160000],\"fareDetails\":[{\"baseFare\":160000,\"totalFare\":160000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":160000,\"baseFare\":160000,\"sourceFare\":160000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540094500,\"travels\":\"Cititrans\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle Toyota HiAce\",\"busTypeId\":101,\"operator\":20362,\"routeId\":1076686500000094500,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":405,\"arrivalTime\":645,\"maxSeatsPerTicket\":3,\"journeyDuration\":240,\"boardingTimes\":[{\"bpId\":352982,\"bpName\":\"Fatmawati\",\"location\":\"Fatmawati\",\"time\":405,\"prime\":false,\"contactNumber\":\"62 811-1794-1234\"}],\"droppingTimes\":[{\"bpId\":352981,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":645,\"prime\":false,\"contactNumber\":\"02179171717\"}],\"availableSeats\":8,\"fares\":[160000],\"fareDetails\":[{\"baseFare\":160000,\"totalFare\":160000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":160000,\"baseFare\":160000,\"sourceFare\":160000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540085600,\"travels\":\"Jackal holidays\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17869,\"routeId\":1076686500000085600,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":510,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":120,\"boardingTimes\":[{\"bpId\":257192,\"bpName\":\"Semanggi\",\"location\":\"Semanggi\",\"time\":510,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"droppingTimes\":[{\"bpId\":278258,\"bpName\":\"Dipatiukur\",\"location\":\"Dipatiukur\",\"time\":630,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"availableSeats\":8,\"fares\":[115000],\"fareDetails\":[{\"baseFare\":115000,\"totalFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":115000,\"baseFare\":115000,\"sourceFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540096900,\"travels\":\"Jackal holidays\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17869,\"routeId\":1076686500000096900,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":510,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":120,\"boardingTimes\":[{\"bpId\":257192,\"bpName\":\"Semanggi\",\"location\":\"Semanggi\",\"time\":510,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"droppingTimes\":[{\"bpId\":257193,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":630,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"availableSeats\":8,\"fares\":[115000],\"fareDetails\":[{\"baseFare\":115000,\"totalFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":115000,\"baseFare\":115000,\"sourceFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540092500,\"travels\":\"Jackal holidays\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17869,\"routeId\":1076686500000092500,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":510,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":120,\"boardingTimes\":[{\"bpId\":257189,\"bpName\":\"Blora\",\"location\":\"Blora\",\"time\":510,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"droppingTimes\":[{\"bpId\":278258,\"bpName\":\"Dipatiukur\",\"location\":\"Dipatiukur\",\"time\":630,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"availableSeats\":8,\"fares\":[115000],\"fareDetails\":[{\"baseFare\":115000,\"totalFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":115000,\"baseFare\":115000,\"sourceFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540101200,\"travels\":\"Jackal holidays\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17869,\"routeId\":1076686500000101200,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":510,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":120,\"boardingTimes\":[{\"bpId\":257189,\"bpName\":\"Blora\",\"location\":\"Blora\",\"time\":510,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"droppingTimes\":[{\"bpId\":257193,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":630,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"availableSeats\":8,\"fares\":[115000],\"fareDetails\":[{\"baseFare\":115000,\"totalFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":115000,\"baseFare\":115000,\"sourceFare\":115000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540095200,\"travels\":\"Jackal holidays\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17869,\"routeId\":1076686500000095200,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":510,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":120,\"boardingTimes\":[{\"bpId\":363096,\"bpName\":\"Jatiwaringin\",\"location\":\"Jatiwaringin\",\"time\":510,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"droppingTimes\":[{\"bpId\":279532,\"bpName\":\"Dipatiukur\",\"location\":\"Dipatiukur\",\"time\":630,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"availableSeats\":8,\"fares\":[105500],\"fareDetails\":[{\"baseFare\":105500,\"totalFare\":105500,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":105500,\"baseFare\":105500,\"sourceFare\":105500,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540095200,\"travels\":\"Jackal holidays\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17869,\"routeId\":1076686500000095200,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":510,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":120,\"boardingTimes\":[{\"bpId\":363096,\"bpName\":\"Jatiwaringin\",\"location\":\"Jatiwaringin\",\"time\":510,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"droppingTimes\":[{\"bpId\":257193,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":630,\"prime\":false,\"contactNumber\":\"(021) 50600678\"}],\"availableSeats\":8,\"fares\":[105500],\"fareDetails\":[{\"baseFare\":105500,\"totalFare\":105500,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":105500,\"baseFare\":105500,\"sourceFare\":105500,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540111200,\"travels\":\"Daytrans\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17867,\"routeId\":1076686500000111200,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":480,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":150,\"boardingTimes\":[{\"bpId\":5539972,\"bpName\":\"ARTHA GADING\",\"location\":\"ARTHA GADING\",\"time\":480,\"prime\":false,\"contactNumber\":\"\"}],\"droppingTimes\":[{\"bpId\":257216,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":630,\"prime\":false,\"contactNumber\":\"081617806767\"}],\"availableSeats\":12,\"fares\":[125000],\"fareDetails\":[{\"baseFare\":125000,\"totalFare\":125000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":125000,\"baseFare\":125000,\"sourceFare\":125000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540108800,\"travels\":\"Daytrans\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17867,\"routeId\":1076686500000108800,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":450,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":180,\"boardingTimes\":[{\"bpId\":257213,\"bpName\":\"Tebet\",\"location\":\"Tebet\",\"time\":450,\"prime\":false,\"contactNumber\":\"0855-8586-767\"}],\"droppingTimes\":[{\"bpId\":257201,\"bpName\":\"Cihampelas\",\"location\":\"Cihampelas\",\"time\":630,\"prime\":false,\"contactNumber\":\"628551356767\"}],\"availableSeats\":12,\"fares\":[135000],\"fareDetails\":[{\"baseFare\":135000,\"totalFare\":135000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":135000,\"baseFare\":135000,\"sourceFare\":135000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true},{\"id\":1076686544540111200,\"travels\":\"Daytrans\",\"vehicleType\":\"BUS\",\"busType\":\"Shuttle\",\"busTypeId\":101,\"operator\":17867,\"routeId\":1076686500000111200,\"doj\":\"2023-03-13T00:00:00.000+0000\",\"departureTime\":495,\"arrivalTime\":630,\"maxSeatsPerTicket\":3,\"journeyDuration\":135,\"boardingTimes\":[{\"bpId\":5451187,\"bpName\":\"SPBU RAWAMANGUN\",\"location\":\"SPBU RAWAMANGUN\",\"time\":495,\"prime\":false,\"contactNumber\":\"\"}],\"droppingTimes\":[{\"bpId\":257216,\"bpName\":\"Pasteur\",\"location\":\"Pasteur\",\"time\":630,\"prime\":false,\"contactNumber\":\"081617806767\"}],\"availableSeats\":12,\"fares\":[125000],\"fareDetails\":[{\"baseFare\":125000,\"totalFare\":125000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"operatorServiceChargePercentage\":0,\"operatorServiceChargeAbsolute\":0,\"partialFare\":0,\"remainingFare\":0,\"seatFares\":[{\"fare\":125000,\"baseFare\":125000,\"sourceFare\":125000,\"serviceTaxPercentage\":0,\"serviceTaxAbsolute\":0,\"passenger_type\":\"ALL\"}]}],\"cancellationPolicy\":\"0:-1:100:0;\",\"partialCancellationAllowed\":false,\"idProofRequired\":false,\"mTicketEnabled\":true,\"currencyType\":\"IDR\",\"mandatoryFieldsRequiredForBookingSet\":[\"PHONE\",\"EMAILID\",\"NAME\"],\"useConfiguredMandatoryFields\":false,\"liveTrackingAvailable\":false,\"zeroCancellationTime\":0,\"tatkalTime\":0,\"categoryDetails\":{\"categories\":[]},\"seatLayoutDisabled\":false,\"quickBookingEnabled\":false,\"leftHandDrive\":true,\"eitherOneMandatoryIdType\":[],\"allMandatoryIdType\":[],\"additionalPassengerPreferenceEnabled\":false,\"boardingDroppingEnabled\":false,\"source\":27355,\"destination\":27410,\"exactSearch\":true,\"boAccount\":10011117,\"boConfig\":{\"account\":10011111,\"minFareDiscountRate\":5,\"maxFareDiscountRate\":10,\"percentDiscount\":true,\"quickBookDiscountActive\":false,\"quickBookDiscountPermissionByBO\":false,\"passengerTypeList\":[]},\"busAmenities\":[\"USB port for charger\",\"M-ticket\"],\"fareFacilitiesDetail\":[],\"ac\":true,\"nonAC\":false,\"seater\":true,\"sleeper\":false,\"AC\":true}]}";
      try {



        AvailableTripsResponse atr = objectMapper.readValue(resp, AvailableTripsResponse.class);

        List<AvailableTrip> availableTrips = new ArrayList<>();
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());


        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());
        availableTrips.addAll(atr.getAvailableTrips());




        availableTripsResponse = AvailableTripsResponse.builder()
            .availableTrips(availableTrips)
            .build();

      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }

      return Mono.just(availableTripsResponse)
//          .doOnSuccess(res -> log.info("before get webClient"))
//          .flatMap(response -> webClient.get().exchangeToMono(clientResponse -> Mono.just(availableTripsResponse)))
//          .doOnSuccess(res -> log.info("after get webClient"))
          .subscribeOn(scheduler);
    }
  }

  @Bean
  RouterFunction<ServerResponse> busOrderSyncEndpoint() {

    CartService cartService = new CartServiceImpl();

    String threadName = "availableTripsBusSupplyEndpoint";

    RequestPredicate requestPredicate = method(HttpMethod.POST)
        .and(path("/tix-order-go/v1/create-order"))
        .and(accept(MediaType.APPLICATION_JSON));


    BaseResponse<OrderData> baseResponse = new BaseResponse<>();

    baseResponse.setCode("SUCCESS");

      int min = 1;
      int max = 10000;
      int randomNumber = (int) (Math.random() * (max - min + 1) + min);

    baseResponse.setData(OrderData.builder()
            .orderId((long) randomNumber)
            .accountId("123123123")
            .lang("ID")
            .orderCartDetails(Collections.singletonList(OrderCartDetailResponse.builder()

                .build()))
        .build());

    HandlerFunction<ServerResponse> handlerFunction = request ->
        ServerResponse.ok()
            .bodyValue(baseResponse)
            .subscribeOn(SchedulersUtils
                .boundedElastic(threadName, 500, 50,
                     30, false));

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
