package demo.clean.code.service.core;

import demo.clean.code.model.request.cart_service.CreateCartRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CoreConfiguration {

//  @Bean
////  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//  CreateCartService createCartService() {
//    return new CreateCartService(CreateCartRequest.builder().build());
//  }
}
