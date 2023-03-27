package demo.clean.code;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import demo.clean.code.model.factory.NamingThreadFactory;
import demo.clean.code.schedulers.BoundedElasticService;
import demo.clean.code.schedulers.CustomCachedBoundedElasticService;
import demo.clean.code.schedulers.CustomCachedParallelService;
import demo.clean.code.schedulers.CustomThreadsService;
import demo.clean.code.schedulers.LinkedBlockingThreadsService;
import demo.clean.code.schedulers.MemoryLeakService;
import demo.clean.code.schedulers.MultipleBoundedElasticService;
import demo.clean.code.schedulers.NewBoundedElasticService;
import demo.clean.code.schedulers.NewParallelService;
import demo.clean.code.schedulers.NewSingleService;
import demo.clean.code.schedulers.ParallelService;
import demo.clean.code.schedulers.ParallelUsingNewBoundedService;
import demo.clean.code.schedulers.SingleService;
import demo.clean.code.schedulers.TiketThreadsService;
import demo.clean.code.schedulers.config.SchedulersUtils;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.tiket.tix.common.spring.reactor")
public class SchedulersEndpointConfiguration {

  @Bean
  RouterFunction<ServerResponse> boundedElasticEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-bounded-elastic"))
        .and(accept(MediaType.APPLICATION_JSON));

    BoundedElasticService boundedElasticService = new BoundedElasticService();
    HandlerFunction<ServerResponse> handlerFunction = request -> boundedElasticService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> multipleBoundedElasticEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-multiple-bounded-elastic"))
        .and(accept(MediaType.APPLICATION_JSON));

    MultipleBoundedElasticService multipleBoundedElasticService = new MultipleBoundedElasticService();
    HandlerFunction<ServerResponse> handlerFunction = request -> multipleBoundedElasticService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Autowired
  @Qualifier("tiketElasticServiceSchedulers")
  private Scheduler tiketElasticServiceSchedulers = Schedulers.elastic();

  @Bean
  RouterFunction<ServerResponse> tiketThreadEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-tiket-thread"))
        .and(accept(MediaType.APPLICATION_JSON));

    TiketThreadsService tiketThreadsService =
        new TiketThreadsService();
    HandlerFunction<ServerResponse> handlerFunction = request -> tiketThreadsService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build()).subscribeOn(tiketElasticServiceSchedulers);

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> customCachedBoundedElasticEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-custom-cached-bounded"))
        .and(accept(MediaType.APPLICATION_JSON));

    String threadName = "customCachedThreadBounded";
    Supplier<Scheduler> schedulerSupplier =
        () -> Schedulers.newBoundedElastic(1000, 50,
            new DefaultThreadFactory(threadName, false, Thread.NORM_PRIORITY),
            30);

    CustomCachedBoundedElasticService customCachedBoundedElasticService =
        new CustomCachedBoundedElasticService(SchedulersUtils
            .boundedElastic(threadName, schedulerSupplier));
    HandlerFunction<ServerResponse> handlerFunction = request -> customCachedBoundedElasticService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  /**
   * FIXING live thread can not evicted
   * @return
   */
  @Bean
  RouterFunction<ServerResponse> customCachedBoundedParallelEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-custom-cached-parallel"))
        .and(accept(MediaType.APPLICATION_JSON));

    String threadName = "customCachedThreadParallel";
    Supplier<Scheduler> schedulerSupplier =
        () -> Schedulers.newParallel(threadName, 300, false);

    CustomCachedParallelService customCachedParallelService =
        new CustomCachedParallelService(SchedulersUtils
            .parallel(threadName, schedulerSupplier));
    HandlerFunction<ServerResponse> handlerFunction = request -> customCachedParallelService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> customThreadEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-custom-thread"))
        .and(accept(MediaType.APPLICATION_JSON));

    Duration pollingInterval = Duration.ofMillis(100L);
    Duration pollingTimeout = Duration.ofMillis(60 * 1000L);

    int corePoolSize = 0;
    int maxPoolSize = 2000;

    double maxPoolSizeLimitByPercent = 50;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
        30L, TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new DefaultThreadFactory("customThreadPoolSynchronousQueue", false, 1));


    CustomThreadsService customThreadsService =
        new CustomThreadsService(executor, pollingInterval, pollingTimeout, maxPoolSizeLimitByPercent);
    HandlerFunction<ServerResponse> handlerFunction = request -> customThreadsService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> customThreadByLinkedBlockingEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-custom-thread-linked-blocking"))
        .and(accept(MediaType.APPLICATION_JSON));

    Duration pollingInterval = Duration.ofMillis(100L);
    Duration pollingTimeout = Duration.ofMillis(60 * 1000L);

    int corePoolSize = 20;
    int maxPoolSize = 100;
    int maxQueue = 900000;

    double maxPoolSizeLimitByPercent = 50;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
        30L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(maxQueue),
        new NamingThreadFactory("customThreadPoolLinkedBlocking")
    );

    LinkedBlockingThreadsService linkedBlockingThreadsService =
        new LinkedBlockingThreadsService(executor, pollingInterval, pollingTimeout, maxPoolSizeLimitByPercent);
    HandlerFunction<ServerResponse> handlerFunction = request -> linkedBlockingThreadsService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> parallelUsingNewBoundedEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-parallel-new-bounded"))
        .and(accept(MediaType.APPLICATION_JSON));

    ParallelUsingNewBoundedService parallelUsingNewBoundedService = new ParallelUsingNewBoundedService();
    HandlerFunction<ServerResponse> handlerFunction = request -> parallelUsingNewBoundedService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }


  @Bean
  RouterFunction<ServerResponse> boundedNewElasticEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-new-bounded-elastic"))
        .and(accept(MediaType.APPLICATION_JSON));

    NewBoundedElasticService newBoundedElasticService = new NewBoundedElasticService();
    HandlerFunction<ServerResponse> handlerFunction = request -> newBoundedElasticService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> parallelEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-parallel"))
        .and(accept(MediaType.APPLICATION_JSON));

    ParallelService parallelService = new ParallelService();
    HandlerFunction<ServerResponse> handlerFunction = request -> parallelService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }


  @Bean
  RouterFunction<ServerResponse> newParallelEndpoint(
      @Value("${demo.clean.code.new-parallel.pool-size:1000}") int poolSize
  ) {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-new-parallel"))
        .and(accept(MediaType.APPLICATION_JSON));

    NewParallelService newParallelService = new NewParallelService();
    HandlerFunction<ServerResponse> handlerFunction = request -> newParallelService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }


  @Bean
  RouterFunction<ServerResponse> singleEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-single"))
        .and(accept(MediaType.APPLICATION_JSON));

    SingleService singleService = new SingleService();
    HandlerFunction<ServerResponse> handlerFunction = request -> singleService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> newSingleEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-newSingle"))
        .and(accept(MediaType.APPLICATION_JSON));

    NewSingleService newSingleService = new NewSingleService();
    HandlerFunction<ServerResponse> handlerFunction = request -> newSingleService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }

  @Bean
  RouterFunction<ServerResponse> memoryLeakEndpoint() {

    RequestPredicate requestPredicate = method(HttpMethod.GET)
        .and(path("/test-memory-leak"))
        .and(accept(MediaType.APPLICATION_JSON));

    String threadName = "memoryLeakThread";
    Supplier<Scheduler> schedulerSupplier =
        () -> Schedulers.newBoundedElastic(1000, 50,
            new DefaultThreadFactory(threadName, false, Thread.NORM_PRIORITY),
            30);

    MemoryLeakService memoryLeakService =
        new MemoryLeakService(SchedulersUtils
            .boundedElastic(threadName, schedulerSupplier));
    HandlerFunction<ServerResponse> handlerFunction = request -> memoryLeakService.handle()
        .then(ServerResponse.status(HttpStatus.OK).build());

    return route(requestPredicate, handlerFunction);
  }
}
