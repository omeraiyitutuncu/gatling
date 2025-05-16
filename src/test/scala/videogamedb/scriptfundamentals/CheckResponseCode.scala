package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class CheckResponseCode extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  val scn = scenario("Video game DB - 3 calls")

    .exec(http("Get all video games")
      .get("/videogame")
    .check(status.is(expected = 200)))
    .pause(duration = 5)

    .exec(http(requestName = "Get specific name")
      .get("/videogame/1")
    .check(status.in(expected = 200 to 210)))
    .pause(1, 10)

    .exec(http(requestName = "Get specific name")
      .get("/videogame/1")
      .check(status.not(expected = 404),status.not(expected = 500))
    )
    .pause(duration = 3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
