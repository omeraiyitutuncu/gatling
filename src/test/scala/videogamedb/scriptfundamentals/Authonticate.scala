package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class Authonticate extends Simulation {

  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")
    .contentTypeHeader(value = "application/json")

  def authonticate: ChainBuilder = {
    exec(
      http(requestName = "Authonticate")
        .post(url = "/authenticate")
        .body(StringBody(string = "{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
        .check(jsonPath(path = "$.token").saveAs("jwtToken"))
    )
  }

  def createNewGame(): ChainBuilder = {
    exec(
      http(requestName = "Create new game")
        .post(url = "/videogame")
        .header("Authorization", s"Bearer #{jwtToken}")

        .body(StringBody(
          "{\n  \"category\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}"
        ))
    .check(status.is(expected = 200)))

  }


  val scn = scenario(name = "authenticate")
    .exec(authonticate)
    .exec(createNewGame())

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
