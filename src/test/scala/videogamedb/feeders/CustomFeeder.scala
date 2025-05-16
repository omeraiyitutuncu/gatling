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

  val idNumbers = (1 to 1000).iterator
  val rnd = new Random()

  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int = 10) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate = LocalDate.now(), random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val customFeeder = Iterator.continually {
    val id = Random.nextInt(10000)

    Map(
      "gameId" -> id,
      "name" -> s"Game - ${randomString()}",
      "releaseDate" -> getRandomDate(random = rnd),
      "reviewScore" -> rnd.nextInt(100),
      "category" -> s"Category - ${randomString()}",
      "rating" -> s"Rating - ${randomString()}"
    )
  }
  println("@@@@@@@@@@@@@@@@@@@@@@@@")
  println(customFeeder.next())
  println("@@@@@@@@@@@@@@@@@@@@@@@@")

  def authonticate: ChainBuilder = {
    exec(
      http(requestName = "Authonticate")
        .post(url = "/authenticate")
        .body(StringBody(string = "{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
        .check(jsonPath(path = "$.token").saveAs("jwtToken"))
    )
  }

  def createNewGame(): ChainBuilder = {
    repeat(times = 10) {
      feed(customFeeder)
        .exec(http(requestName = "Create new game")
          .post(url = "/videogame")
          .header("Authorization", "Bearer #{jwtToken}")
          .body(ElFileBody(filePath = "bodies/newGameTemplate.json")).asJson
          .check(bodyString.saveAs(key = "responseBody")))

        .exec { session => println(session("responseBody").as[String]); session }
        .pause(duration = 1)
    }
  }

  def createNewGame2(): ChainBuilder = {
    repeat(times = 10) {
      feed(customFeeder)
        .exec(http(requestName = "Create new game")
          .post(url = "/videogame")
          .header("Authorization", "Bearer #{jwtToken}")
          .body(StringBody(
            """{
    "id": #{gameId},
    "name": "#{name}",
    "releaseDate": "#{releaseDate}",
    "reviewScore": #{reviewScore},
    "category": "#{category}",
    "rating": "#{rating}"
  }"""
          )).asJson.check(bodyString.saveAs(key = "responseBody")))

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
