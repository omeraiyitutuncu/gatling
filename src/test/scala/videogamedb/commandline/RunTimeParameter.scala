package videogamedb.commandline

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.Console.println


class RunTimeParameter extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")

  def USERCOUNT: Int = System.getProperty("USERS", "5").toInt

  def RAMPDURATION: Int = System.getProperty("RAMP_DURATION", "10").toInt

  def TESTDURATION: Int = System.getProperty("TEST_DURATION", "30").toInt


  def getCustomFeeder(): ChainBuilder = {

        exec(http(requestName = "Get video game with id: ")
          .get("/videogame/2")
          .check(status.is(expected = 200)))
        .pause(duration = 1)
  }

  before(
    println(s"Running test with $USERCOUNT and $RAMPDURATION and $TESTDURATION" )
  )

  val scn = scenario("Basic custom feeder")
    .forever {
      exec(getCustomFeeder())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(users = USERCOUNT).during(RAMPDURATION)
    )
  ).protocols(httpProtocol)
    .maxDuration(duration = TESTDURATION)


}
