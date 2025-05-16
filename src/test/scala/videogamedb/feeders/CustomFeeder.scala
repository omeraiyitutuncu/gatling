package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

class CustomFeeder extends Simulation {
  val httpProtocol = http.baseUrl(url = "https://videogamedb.uk:443/api")
    .acceptHeader(value = "application/json")
    .contentTypeHeader(value = "application/json")

  val idNumbers = (1 to 10).iterator
  val rnd = new Random()

  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int = 10) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate = LocalDate.now(), random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }


  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString()),
    "releaseDate" -> getRandomDate(random = rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> (s"Category- ${randomString()}"),
    "rating" -> (s"Rating- ${randomString()}")
  ))


  def authonticate: ChainBuilder = {
    exec(
      http(requestName = "Authonticate")
        .post(url = "/authenticate")
        .body(StringBody(string = "{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
        .check(jsonPath(path = "$.token").saveAs("jwtToken"))
    )
      .exec { session => println(session("jwtToken").as[String]); session }
      .pause(duration = 1)
  }

  def createNewGame(): ChainBuilder = {
    repeat(times = 10) {
      feed(customFeeder)
        .exec(http(requestName = "Create new game")
          .post(url = "/videogame")
          .header("Authorization", "Bearer #{jwtToken}")
          .body(ElFileBody(filePath = "bodies/newGameTemplate.json")).asJson
//          .body(StringBody(
//            "{\n  \"category\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}"
//          ))
          .check(bodyString.saveAs(key = "responseBody")))

        .exec { session => println(session("responseBody").as[String]); session }
        .pause(duration = 1)
    }
  }


  val scn = scenario("Basic custom feeder")
    .exec(authonticate)
    .exec(createNewGame())


  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)


}
