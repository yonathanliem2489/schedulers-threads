package demo.clean.code.schedulers;


import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
public class MemoryLeakService {
  private ArrayList<String> list = new ArrayList<>();

  public Mono<Void> handle() {
    return Mono.fromRunnable(() -> {
          log.info("MemoryLeak receive request");

          for (int i = 0; i < 10000; i++) {
            String data = new String("data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources data sources ");
            list.add(data);
          }

          list.clear(); // set clear to avoid memory leak

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          log.info("MemoryLeakService request end");
        })
        .then();
  }
}
