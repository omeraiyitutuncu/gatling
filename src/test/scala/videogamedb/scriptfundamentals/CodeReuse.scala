package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CodeReuse extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")


  def getAllVideoGames: ChainBuilder = {
    exec(http(requestName = "Get all video games")
      .get("/videogame")
      .check(status.is(expected = 200)))
  }

  def getSpesificGames(id:Int): ChainBuilder = {
    exec(http(requestName = "Get specific games")
      .get(s"/videogame/$id")
      .check(status.in(expected = 200 to 210)))
  }

  val scn = scenario(name = "Code reuse")
    .exec(getAllVideoGames)
    .pause(duration = 5)
    .exec(getSpesificGames(1))
    .pause(duration = 5)
    .exec(getAllVideoGames)


  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)
}
