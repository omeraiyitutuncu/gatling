package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")


  val csvFeeder = csv("data/gameCsvFile.csv").random
  val csvFeeder_2 = csv("data/gameCsvFile.csv").circular

  def getSpesificGames(id: Int): ChainBuilder = {
    repeat(times = 10) {
      feed(csvFeeder)

        .exec { session =>
          println("Feeding data: " + session("gameName").as[String])
          session
        }

        .exec(http(requestName = "Get specific games: gameName")
          .get(s"/videogame/#{gameId}")
          .check(status.in(expected = 200 to 210))
          .check(jsonPath(path = "$.name").is(expected = "#{gameName}")))
        .pause(duration = 1)

    }
  }

  val scn = scenario(name = "authenticate")
    .exec(getSpesificGames(id = 2))


  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)
}
