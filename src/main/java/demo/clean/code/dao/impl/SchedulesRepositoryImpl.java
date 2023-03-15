package demo.clean.code.dao.impl;

import com.tiket.tix.bus.model.common.constant.ScheduleFields;
import com.tiket.tix.bus.model.common.models.dao.Schedule;
import demo.clean.code.dao.api.SchedulesRepositoryCustom;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author irvanahadi
 */
@Slf4j
@Repository
public class SchedulesRepositoryImpl implements SchedulesRepositoryCustom {

  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @Autowired
  private MongoTemplate mongoTemplate;

//  @Autowired
//  public void setMongoTemplate(MongoTemplate mongoTemplate) {
//    this.mongoTemplate = mongoTemplate;
//  }
//
//  @Autowired
//  public void setReactiveMongoTemplate(
//      ReactiveMongoTemplate reactiveMongoTemplate) {
//    this.reactiveMongoTemplate = reactiveMongoTemplate;
//  }

  @Override
  public Flux<Schedule> findExistingSchedules(List<String> scheduleIds) {
    MatchOperation match = Aggregation.match(Criteria.where("schedule_id").in(scheduleIds));
    ProjectionOperation projection = Aggregation.project("schedule_id", "last_fetch");

    TypedAggregation<Schedule> aggregation = Aggregation.newAggregation(Schedule.class, match, projection);
    return reactiveMongoTemplate.aggregate(aggregation, Schedule.class);
  }


  @Override
  public Flux<Schedule> findExistingSchedules2(List<String> scheduleIds) {
    MatchOperation match = Aggregation.match(Criteria.where("schedule_id").in(scheduleIds));
    ProjectionOperation projection = Aggregation.project("schedule_id", "last_fetch");

    TypedAggregation<Schedule> aggregation = Aggregation.newAggregation(Schedule.class, match, projection);
    return Flux.fromIterable(mongoTemplate.aggregate(aggregation, Schedule.class).getMappedResults());
  }

  @Override
  public Mono<Schedule> upsert(Schedule schedule) {
    Update update = new Update();
    update.set(ScheduleFields.LAST_FETCH, new Date());
    update.set(ScheduleFields.TRIPS, schedule.getAvailableTrips());

    update.setOnInsert(ScheduleFields.SCHEDULE_ID, schedule.getScheduleId());
    update.setOnInsert(ScheduleFields.VENDOR, schedule.getVendor());

    Query query = new Query();
    query.addCriteria(Criteria.where(ScheduleFields.SCHEDULE_ID).is(schedule.getScheduleId()));

    return reactiveMongoTemplate.upsert(query, update, Schedule.class).map(result -> schedule);
  }
}
