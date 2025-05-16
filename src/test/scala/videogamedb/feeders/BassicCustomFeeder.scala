package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class BassicCustomFeeder extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  val idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getCustomFeeder(): ChainBuilder = {
    repeat(times = 10) {
      feed(customFeeder)
        .exec(http(requestName = "Get video game with id: #{gameId}")
          .get("/videogame/#{gameId}")
          .check(status.is(expected = 200)))
        .pause(duration = 1)
    }
  }

  val scn = scenario("Basic custom feeder")
    .exec(getCustomFeeder())

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)


}
