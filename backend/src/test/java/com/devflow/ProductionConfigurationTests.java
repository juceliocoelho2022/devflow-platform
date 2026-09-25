package com.devflow;

import com.devflow.config.ProductionGuard;
import com.devflow.config.AdminBootstrap;
import com.devflow.model.*;
import com.devflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductionConfigurationTests {
  @Test void demoSeederIsAbsentInProductionEvenIfDevelopmentIsAlsoSelected(){
    for(String profiles:new String[]{"prod","dev,prod"}) {
      new org.springframework.boot.test.context.runner.ApplicationContextRunner()
        .withPropertyValues("spring.profiles.active="+profiles)
        .withUserConfiguration(com.devflow.config.DataSeeder.class)
        .run(context->assertThat(context).doesNotHaveBean(com.devflow.config.DataSeeder.class));
    }
  }
  @Test void bootstrapIsDisabledByDefault(){
    new org.springframework.boot.test.context.runner.ApplicationContextRunner()
      .withUserConfiguration(AdminBootstrap.class)
      .run(context->assertThat(context).doesNotHaveBean(AdminBootstrap.class));
  }
  MockEnvironment valid(){
    return new MockEnvironment().withProperty("app.jwt.secret","a-production-test-key-with-at-least-32-bytes")
      .withProperty("app.cors-origin","https://app.example.com")
      .withProperty("spring.datasource.password","production-db-password");
  }
  @Test void acceptsExplicitProductionSettings(){assertThatCode(()->new ProductionGuard(valid())).doesNotThrowAnyException();}
  @Test void rejectsDevelopmentProfileCombination(){
    var env=valid();env.setActiveProfiles("dev","prod");
    assertThatThrownBy(()->new ProductionGuard(env)).isInstanceOf(IllegalStateException.class);
  }
  @Test void rejectsMissingShortAndDemonstrationKeys(){
    for(String key:new String[]{"","short","devflow-enterprise-local-secret-key-2026","change-this-secret-with-at-least-32-characters"})
      assertThatThrownBy(()->new ProductionGuard(valid().withProperty("app.jwt.secret",key))).isInstanceOf(IllegalStateException.class);
  }
  @Test void rejectsWildcardAndInsecureCors(){
    for(String origin:new String[]{"","*","http://app.example.com","https://*.example.com"})
      assertThatThrownBy(()->new ProductionGuard(valid().withProperty("app.cors-origin",origin))).isInstanceOf(IllegalStateException.class);
  }
  @Test void rejectsDemonstrationDatabasePassword(){
    assertThatThrownBy(()->new ProductionGuard(valid().withProperty("spring.datasource.password","devflow"))).isInstanceOf(IllegalStateException.class);
  }
  @Test void bootstrapCreatesHashedAdminOnlyOnEmptyDatabase() throws Exception {
    var users=mock(UserRepository.class);var encoder=new BCryptPasswordEncoder();
    new AdminBootstrap(users,encoder,"Owner","owner@example.com","a-long-initial-password").run();
    var captor=ArgumentCaptor.forClass(User.class);verify(users).save(captor.capture());
    assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
    assertThat(encoder.matches("a-long-initial-password",captor.getValue().getPassword())).isTrue();
  }
  @Test void bootstrapNeverOverwritesExistingUsers() throws Exception {
    var users=mock(UserRepository.class);when(users.count()).thenReturn(1L);
    new AdminBootstrap(users,new BCryptPasswordEncoder(),"","","").run();
    verify(users,never()).save(any());
  }
  @Test void bootstrapRejectsWeakCredentials(){
    var users=mock(UserRepository.class);
    assertThatThrownBy(()->new AdminBootstrap(users,new BCryptPasswordEncoder(),"Owner","owner@example.com","admin123").run()).isInstanceOf(IllegalStateException.class);
    verify(users,never()).save(any());
  }
}
