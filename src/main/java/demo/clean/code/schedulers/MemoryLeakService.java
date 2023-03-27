package demo.clean.code.schedulers;


import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class MemoryLeakService {
//  private ArrayList<String> list = new ArrayList<>();

  private ConcurrentHashMap<String, String> senders = new ConcurrentHashMap<>();

  private final Scheduler scheduler;

  public MemoryLeakService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Mono<Void> handle() {
    return Mono.fromRunnable(() -> {
          log.info("MemoryLeak receive request");

          for (int i = 0; i < 10000; i++) {
            String data = new String("data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources ");
//            list.add(data);

//            int minimum = 1;
//            int maximum = 10;
//            int range = maximum - minimum + 1;
//            int randomNum = minimum + (int)(Math.random() * range);
//            senders.put(String.valueOf(randomNum), data);

            senders.put(UUID.randomUUID().toString(), data);
          }

//          list.clear(); // set clear to avoid memory leak

//          senders.clear();

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          log.info("MemoryLeakService request end");
        })
        .then()
        .subscribeOn(scheduler);
  }
}
