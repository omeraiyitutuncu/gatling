package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class Repeat extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")


  def getAllVideoGames: ChainBuilder = {
    repeat(times = 3) {
      exec(http(requestName = "Get all video games")
        .get("/videogame")
        .check(status.is(expected = 200)))
    }
  }

  def getSpesificGames: ChainBuilder = {
    repeat(times = 5, counterName = "counter") {

      exec(http(requestName = "Get specific games with id :#{counter}")
        .get(s"/videogame/#{counter}")
        .check(status.in(expected = 200 to 210)))
    }
  }

  val scn = scenario(name = "Code reuse")
    .exec(getAllVideoGames)
    .pause(duration = 5)
    .exec(getSpesificGames)
    .pause(duration = 5)
    .repeat(times = 2){
      getAllVideoGames
    }

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)
}
