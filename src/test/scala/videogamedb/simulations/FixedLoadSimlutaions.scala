package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class FixedLoadSimlutaions extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  def getAllVideoGames: ChainBuilder = {
    exec(http(requestName = "Get all video games")
      .get("/videogame")
      .check(status.is(expected = 200)))
  }

  def getSpecificGames(id: Int): ChainBuilder = {
    exec(http(requestName = "Get specific games")
      .get(s"/videogame/$id")
      .check(status.in(expected = 200 to 210)))
  }


  val scn = scenario("Video game DB - 3 calls")
    .forever {

      exec(getAllVideoGames)
        .pause(duration = 5)
        .exec(getSpecificGames(2))
        .pause(duration = 5)
        .exec(getAllVideoGames)
    }


  setUp(
    scn.inject(
      nothingFor(5), // This means there will be a pause for 5 seconds before any users are injected.
      atOnceUsers(users = 10), // Injects 5 users immediately at the start.
      rampUsers(users = 20).during(30), // Gradually injects 10 users over a period of 5 seconds.
    ).protocols(httpProtocol)
  ).maxDuration(duration = 60)


}
