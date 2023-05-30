package gatlingdemostoreapi;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DemostoreApiSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://demostore.gatling.io")
    .header("Cache-Control", "no-cache")
    .contentTypeHeader("application/json")
    .acceptHeader("application/json");

  private Map<CharSequence, String> authorizationHeaders = Map.ofEntries(
    Map.entry("authorization", "Bearer #{jwt}")
  );

  private ScenarioBuilder scn = scenario("DemostoreApiSimulation")
    .exec(
      http("List categories")
        .get("/api/category")
    )
    .pause(86)
    .exec(
      http("List products")
        .get("/api/product?category=7")
    )
    .pause(31)
    .exec(
      http("Get product")
        .get("/api/product/34")
    )
    .pause(43)
    .exec(
      http("Authenticate")
        .post("/api/authenticate")
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/authenticate-admin.json"))
        .check(jsonPath("$.token").saveAs("jwt"))
    )
    .pause(73)
    .exec(
      http("Update product")
        .put("/api/product/34")
        .headers(authorizationHeaders)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/update-product.json"))
    )
    .pause(36)
    .exec(
      http("Create product")
        .post("/api/product")
        .headers(authorizationHeaders)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/create-product.json"))
    )
    .pause(35)
    .exec(
      http("Update category")
        .put("/api/category/7")
        .headers(authorizationHeaders)
        .body(RawFileBody("gatlingdemostoreapi/demostoreapisimulation/update-category.json"))
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
