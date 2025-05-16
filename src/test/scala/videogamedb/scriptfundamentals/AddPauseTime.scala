package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class AddPauseTime extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  val scn = scenario("Video game DB - 3 calls")

    .exec(http("Get all video games")
      .get("/videogame"))
    .pause(duration = 5)

    .exec(http(requestName = "Get specific name")
      .get("/videogame/1"))
    .pause(1, 10)

    .exec(http(requestName = "Get specific name")
      .get("/videogame/1"))
    .pause(duration = 3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)


}
