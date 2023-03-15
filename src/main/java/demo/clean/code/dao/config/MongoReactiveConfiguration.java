package demo.clean.code.dao.config;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import demo.clean.code.dao.api.SchedulesRepositoryCustom;
import demo.clean.code.dao.impl.SchedulesRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "demo.clean.code.dao")
public class MongoReactiveConfiguration extends AbstractReactiveMongoConfiguration {

  @Bean
  SchedulesRepositoryCustom schedulesRepositoryCustom(ReactiveMongoTemplate reactiveMongoTemplate,
      MongoTemplate mongoTemplate) {
    SchedulesRepositoryImpl impl = new SchedulesRepositoryImpl();
//    impl.setReactiveMongoTemplate(reactiveMongoTemplate);
//    impl.setMongoTemplate(mongoTemplate);
    return impl;
  }

  @Value("${spring.data.mongodb.uri}")
  private String mongodbUri;

  @Value("${spring.data.mongodb.database}")
  private String databaseName;

  @Override
  public MongoClient reactiveMongoClient() {
    ConnectionString connectionString = new ConnectionString(mongodbUri);
    return MongoClients.create(connectionString);
  }

  @Override
  protected String getDatabaseName() {
    return databaseName;
  }
}
