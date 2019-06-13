package httpclient.restassured.example.problemexample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

/** */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UrlControllerTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private WebApplicationContext webApplicationContext;

  @LocalServerPort private int port;

  final String id = "id_with;_semicolon";
  final String encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8.name());

  public UrlControllerTest() throws UnsupportedEncodingException {}

  @Before
  public void initialiseRestAssuredMockMvcWebApplicationContext() {
    RestAssured.port = port;
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    RestAssured.registerParser("text/plain", Parser.TEXT);
  }

  @Test
  public void sendEncodedId_withSpringRestTemplate_shouldGetTheSameBack() throws Exception {
    // Encoded id without URI's reserved characters based on https://tools.ietf.org/html/rfc3986#section-2.2
    assertThat(
            restTemplate.getForObject(
                "http://localhost:{port}/idecho/{id}", String.class, port, encodedId))
        .contains(encodedId);
    // => Works
  }

  @Test
  public void sendEncodedId_withRestAssured_shouldGetTheSameBack() {
    // The same with RestAssured
    Response response =
        RestAssured.given()
            .pathParam("id", encodedId)
            .when()
            .get("/idecho" + "/{id}")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(encodedId))
            .extract()
            .response();
    // => Works as expected
  }


  @Test
  public void sendId_withRestAssured_shouldgetTheSameBack() {
    // By default, RestAssured is encoding the URL path so plain "id" should work
    Response response =
        RestAssured.given()
            .pathParam("id", id)
            .when()
            .get("/idecho" + "/{id}")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(id))
            .extract()
            .response();
    // Unfortunately no luck here with apache's httpclient 4.5.8
    //java.lang.AssertionError: 1 expectation failed.
    //Response body doesn't match expectation.
    //Expected: "id_with;_semicolon"
    //Actual: id_with

  }

  @Test
  public void sendEncododedId_withRestAssured_shouldgetTheSameBack() {
    // Lets disable RestAssured's encoding and encode the string by ourselves
    Response response =
        RestAssured.given()
            .urlEncodingEnabled(false)
            .pathParam("id", encodedId)
            .when()
            .get("/idecho" + "/{id}")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(id))
            .extract()
            .response();

    // Still no luck, the semicolon divides the path again:
    // java.lang.AssertionError: 1 expectation failed.
    // Response body doesn't match expectation.
    // Expected: "id_with;_semicolon"
    //  Actual: id_with
  }
}
