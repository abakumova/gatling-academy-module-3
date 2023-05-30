package gatlingdemostoreapi;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class DemostoreApiSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://demostore.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-GB,en;q=0.9,ru-UA;q=0.8,ru;q=0.7,uk-UA;q=0.6,uk;q=0.5,ru-RU;q=0.4,en-US;q=0.3,tr;q=0.2")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");

  private Map<CharSequence, String> headers_0 = Map.of("accept", "application/json");

  private Map<CharSequence, String> headers_3 = Map.ofEntries(
    Map.entry("Content-Type", "application/json"),
    Map.entry("Origin", "http://demostore.gatling.io"),
    Map.entry("accept", "application/json")
  );

  private Map<CharSequence, String> headers_4 = Map.ofEntries(
    Map.entry("Content-Type", "application/json"),
    Map.entry("Origin", "http://demostore.gatling.io"),
    Map.entry("accept", "application/json"),
    Map.entry("authorization", "Bearer #{jwt}")
  );

  private ScenarioBuilder scn = scenario("DemostoreApiSimulation")
    .exec(
      http("request_0")
        .get("/api/category")
        .headers(headers_0)
    )
    .pause(86)
    .exec(
      http("request_1")
        .get("/api/product?category=7")
        .headers(headers_0)
    )
    .pause(31)
    .exec(
      http("request_2")
        .get("/api/product/34")
    )
    .pause(43)
    .exec(
      http("request_3")
        .post("/api/authenticate")
        .headers(headers_3)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/0003_request.json"))
        .check(jsonPath("$.token").saveAs("jwt"))
    )
    .pause(73)
    .exec(
      http("request_4")
        .put("/api/product/34")
        .headers(headers_4)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/0004_request.json"))
    )
    .pause(36)
    .exec(
      http("request_5")
        .post("/api/product")
        .headers(headers_4)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/0005_request.json"))
    )
    .pause(35)
    .exec(
      http("request_6")
        .put("/api/category/7")
        .headers(headers_4)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/0006_request.json"))
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
