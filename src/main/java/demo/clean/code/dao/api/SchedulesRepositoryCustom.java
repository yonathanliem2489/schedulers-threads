package demo.clean.code.dao.api;

import com.tiket.tix.bus.model.common.models.dao.Schedule;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author irvanahadi
 */
public interface SchedulesRepositoryCustom {

  Flux<Schedule> findExistingSchedules(List<String> scheduleIds);

  Flux<Schedule> findExistingSchedules2(List<String> scheduleIds);

  Mono<Schedule> upsert(Schedule schedule);
}
