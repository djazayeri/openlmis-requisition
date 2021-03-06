package org.openlmis.referencedata.web;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.jayway.restassured.RestAssured;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.junit.RamlMatchers;
import guru.nidi.ramltester.restassured.RestAssuredClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.Application;
import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Schedule;
import org.openlmis.referencedata.repository.PeriodRepository;
import org.openlmis.referencedata.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest("server.port:8080")
public class ScheduleControllerIntegrationTest {
  private static final String BASE_URL = System.getenv("BASE_URL");
  private static final String RAML_ASSERT_MESSAGE = "HTTP request/response should match RAML " 
      + "definition.";

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private PeriodRepository periodRepository;

  private RamlDefinition ramlDefinition;
  private RestAssuredClient restAssured;

  private Schedule schedule;
  private Period period;

  @Before
  public void setUp() {
    RestAssured.baseURI = BASE_URL;
    ramlDefinition = RamlLoaders.fromClasspath().load("api-definition-raml.yaml");
    restAssured = ramlDefinition.createRestAssured();

    cleanup();

    schedule = new Schedule();
    schedule.setCode("code");
    schedule.setName("schedule");
    schedule.setDescription("Test schedule");
    scheduleRepository.save(schedule);

    period = new Period();
    period.setName("period");
    period.setProcessingSchedule(schedule);
    period.setDescription("Test period");
    period.setStartDate(LocalDate.of(2016, 1, 1));
    period.setEndDate(LocalDate.of(2016, 2, 1));
    periodRepository.save(period);
  }

  @After
  public void cleanup() {
    periodRepository.deleteAll();
    scheduleRepository.deleteAll();
  }

  @Test
  public void testGetTotalDifference() {
    String response = restAssured.given()
        .pathParam("id", schedule.getId())
        .when()
        .get("/api/schedules/{id}/difference")
        .then()
        .statusCode(200)
        .extract().asString();

    assertTrue(response.contains("Period lasts 1 months and 0 days"));
    assertThat(RAML_ASSERT_MESSAGE , restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }
}
