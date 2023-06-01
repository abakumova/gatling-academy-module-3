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

  private static Map<CharSequence, String> authorizationHeaders = Map.ofEntries(
    Map.entry("authorization", "Bearer #{jwt}")
  );

  private static class Authentication {

    private static ChainBuilder authenticate = exec(http("Authenticate")
            .post("/api/authenticate")
            .body(StringBody("{\"username\": \"admin\",\"password\": \"admin\"}"))
            .check(status().is(200))
            .check(jsonPath("$.token").saveAs("jwt")));
  }

  private static class Categories {
    private static ChainBuilder list =
            exec(http("List categories")
                    .get("/api/category")
                    .check(jsonPath("$[?(@.id == 6)].name").is("For Her")));

    private static ChainBuilder update =
            exec(http("Update category")
                    .put("/api/category/7")
                    .headers(authorizationHeaders)
                    .body(StringBody("{\"name\": \"Everyone\"}"))
                    .check(jsonPath("$.name").is("Everyone")));
  }

  private ScenarioBuilder scn = scenario("DemostoreApiSimulation")
    .exec(Categories.list)
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
    .exec(Authentication.authenticate)
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
    .exec(Categories.update);

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
