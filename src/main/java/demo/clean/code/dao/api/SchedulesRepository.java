package demo.clean.code.dao.api;

import com.tiket.tix.bus.model.common.models.dao.Schedule;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author irvanahadi
 */
@Repository
public interface SchedulesRepository extends ReactiveMongoRepository<Schedule, String> {
  Mono<Schedule> findByScheduleId(String scheduleId);
}
