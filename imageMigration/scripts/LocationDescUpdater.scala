import java.net.ConnectException
import java.sql.Connection
import java.sql.DriverManager
import scala.actors.Actor
import scala.util.parsing.json.JSON

/**
 * libraryDependencies ++= Seq(
 * "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1"
 * )
 */
/*
 * to run this as scala script updateLocalityAndSubrbDesc method should be invoked
 */
LocationDescUpdater.updateLocalityAndSubrbDesc()

import java.util.Date
case object Exit
case object Inform
case class LocationDetails(val locationId: Int, var descriptionApi: String, var updateQueryTemplate: String, val connection: Connection)
class CC[T] { def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T]) }
case object GenericJsonMap extends CC[Map[String, Any]]

object LocationDescUpdater extends Actor {
  val apiServer = "http://localhost:8080/"
  val localityDescApi = apiServer + "data/v1/entity/locality/%d/description"
  val suburbDescApi = apiServer + "data/v1/entity/suburb/%d/description"
  var connection: Connection = null;
  val localityDescUpdateQuery = "UPDATE cms.locality L SET L.description=\"%s\" WHERE L.locality_id=%d"
  val localityIdSelectQuery = "SELECT L.locality_id as location_id FROM cms.locality L left join cms.table_attributes TA ON (TA.attribute_name = 'DESC_CONTENT_FLAG' and L.locality_id = TA.table_id and TA.table_name = 'locality') where (TA.attribute_value is null or TA.attribute_value = 0) and L.STATUS='Active'"

  val suburbDescUpdateQuery = "SELECT S.suburb_id as suburb_id FROM cms.suburb S left join cms.table_attributes TA ON (TA.attribute_name = 'DESC_CONTENT_FLAG' and S.suburb_id = TA.table_id and TA.table_name = 'suburb') where (TA.attribute_value is null or TA.attribute_value = 0) and S.STATUS='Active';"
  val suburbIdSelectQuery = "UPDATE cms.suburb S SET S.description=\"%s\" WHERE S.suburb_id=%d"

  def updateLocalityAndSubrbDesc() {
    Class.forName("com.mysql.jdbc.Driver")
    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/proptiger?user=root&password=root")
    LocationDescUpdater.start
    LocationDescUpdater ! "locality"
  }

  def act() {
    var completedCount = 0
    var totResources: Int = 0
    var startTime = new Date()
    loop {
      react {
        case "locality" => totResources = locationDescUpdate(localityIdSelectQuery, localityDescUpdateQuery, this)
        case Inform =>
          completedCount = completedCount.+(1);
          if (completedCount >= totResources) {
            //tell this actor to exit as all childs have informed
            this ! Exit
          }
        case Exit =>
          println("total time taken to update " + completedCount + " "+((new Date().getTime - startTime.getTime)) / 1000 +" seconds")
          exit
      }
    }
  }

  def locationDescUpdate(selectQuery: String, updateQuery: String, parent: Actor): Int = {
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(selectQuery)
    var counter = 0;
    while (resultSet.next()) {
      val localityId = resultSet.getInt("location_id");
      val finalUrl = localityDescApi.replace("%d", localityId + "");
      val locationUpdateActor = new LocationDetailActor(parent)
      locationUpdateActor.start
      locationUpdateActor ! new LocationDetails(localityId, finalUrl, updateQuery, connection)
      locationUpdateActor ! Exit
      counter = counter.+(1)
    }
    println("Total locality to update=" + counter)
    return counter
  }
}

/**
 * Location detail actor which have api and update query, it will update for corresponding location id
 * @author rajeev-engg-lp
 *
 */
class LocationDetailActor(val parent: Actor) extends Actor {
  def act() {
    loop {
      react {
        case LocationDetails(localityId, finalUrl, updateQuery, connection) => fetchDescAndUpdate(localityId, finalUrl, updateQuery, connection)
        case Exit => parent ! Inform; exit;
        case _ => println("\nSome garbage message for Actor!!"); this ! Exit
      }
    }
    def fetchDescAndUpdate(locationId: Int, descriptionApi: String, updateQueryTemplate: String, connection: Connection): Unit = {
      try {
        var result = scala.io.Source.fromURL(descriptionApi).getLines.mkString
        val jsonRes = JSON.parseFull(result)
        jsonRes match {
          case Some(GenericJsonMap(map)) => {
            if (map.get("statusCode").get.equals("2XX")) {
              val updateStatement = connection.createStatement()
              updateStatement.execute(updateQueryTemplate.replace("%d", locationId + "").replace("%s", map.get("data").get.toString));
              updateStatement.close()
              print("." + locationId)
            } else {
              println("\nError in response locality id " + locationId + ", " + map.get("error").get)
            }
          }
          case other => println("\nUnknown data structure: " + other)
        }
      } catch {
        case ce: ConnectException =>
          println("\nEither server is down or hitting wrong URL");
        case e: Throwable => println("\nSome error for location id: " + locationId, e);
      }
      //ask actor to exit
      this ! Exit;
    }
  }
}

