package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class LoadSimlutaions extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  def getAllVideoGames: ChainBuilder = {
    exec(http(requestName = "Get all video games")
      .get("/videogame")
      .check(status.is(expected = 200)))
  }

  def getSpecificGames(id:Int): ChainBuilder = {
    exec(http(requestName = "Get specific games")
      .get(s"/videogame/$id")
      .check(status.in(expected = 200 to 210)))
  }


  val scn = scenario("Video game DB - 3 calls")
    .exec(getAllVideoGames)
    .pause(duration = 5)
    .exec(getSpecificGames(2))
    .pause(duration = 5)
    .exec(getAllVideoGames)



  setUp(
    scn.inject(
      nothingFor(5),                                    // This means there will be a pause for 5 seconds before any users are injected.
      atOnceUsers(users = 5),                           // Injects 5 users immediately at the start.
      rampUsers(users = 10).during(5),                  // Gradually injects 10 users over a period of 5 seconds.
      constantUsersPerSec(20).during(15),               // Injects users at a constant rate of 20 users per second for 15 seconds.
      constantUsersPerSec(20).during(15).randomized,    // Similar to the previous step, but the injection rate is randomized around 20 users per second for 15 seconds.
      rampUsersPerSec(10).to(20).during(10),            // Gradually increases the injection rate from 10 users per second to 20 users per second over a period of 10 seconds.
      rampUsersPerSec(10).to(20).during(10).randomized, // Similar to the previous step, but the ramp-up rate is randomized.
      stressPeakUsers(1000).during(20)                  // Injects a peak load of 1000 users over a period of 20 seconds, simulating a stress test.

    )
  ).protocols(httpProtocol)


}
