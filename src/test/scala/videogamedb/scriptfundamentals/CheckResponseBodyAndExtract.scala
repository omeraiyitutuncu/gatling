package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  val scn = scenario(name = "Check with Json Path")

    .exec(http(requestName = "Get specific game")
      .get("/videogame/1")
      .check(jsonPath(path = "$.name").is("Resident Evil 4")))

    .exec(http(requestName = "Get all video games")
      .get("/videogame")
      .check(jsonPath(path = "$[1].id").saveAs(key = "gameId")))
    .exec { session => println(session); session }

    .exec(http(requestName = "Get specific game")
      .get("/videogame/#{gameId}")
      .check(jsonPath("$.name").is("Gran Turismo 3"))
      .check(bodyString.saveAs(key = "responseBody"))
    )
    .exec { session => println(session("responseBody").as[String]); session }


  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)
}
