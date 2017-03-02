package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import play.api.libs.json._
import reactivemongo.bson._

case class NewPlayer(name: String, session: String, firstSeen: Option[DateTime])

object NewPlayer {
  def fromJson(json: JsObject) : NewPlayer = NewPlayer((json \ "name").as[String], (json \ "session").as[String], Some(DateTime.now()))
}

case class PlayerModel(_id: BSONObjectID, name: String, session: String, firstSeen: Option[DateTime]) {
  def id = _id.stringify
  def toJson : JsObject = Json.obj("id" -> id, "name" -> name, "session" -> session, "firstSeen" -> firstSeen.toString())
  def toBSON : BSONDocument = {
    val firstSeenBson = firstSeen match {
      case Some(seen) => BSONDocument("firstSeen" -> new BSONDateTime(seen.getMillis))
      case None => BSONDocument.empty
    }
    BSONDocument("_id" -> _id, "name" -> name, "session" -> session) ++ firstSeenBson
  }
}

object PlayerModel {

  import play.modules.reactivemongo.json.BSONFormats._
  implicit object PlayerBSONReader extends BSONDocumentReader[PlayerModel] {
    def read(doc: BSONDocument) : PlayerModel = fromBSON(doc)
  }
  
  implicit object PlayerBSONWriter extends BSONDocumentWriter[PlayerModel] {
    def write(model: PlayerModel) : BSONDocument = toBSON(model)
  }
  
  def fromNewPlayer(newPlayer : NewPlayer) : PlayerModel = {
    PlayerModel(BSONObjectID.generate(),newPlayer.name,newPlayer.session,newPlayer.firstSeen)
  }
  
  def fromBSON(doc: BSONDocument) : PlayerModel = PlayerModel(doc.getAs[BSONObjectID]("_id").get,doc.getAs[String]("name").get,doc.getAs[String]("session").get,doc.getAs[BSONDateTime]("firstSeen") map (bs => new DateTime(bs.value)))
  
  def toBSON(model: PlayerModel) : BSONDocument = {
    val firstSeenBson = model.firstSeen match {
      case Some(seen) => BSONDocument("firstSeen" -> new BSONDateTime(seen.getMillis))
      case None => BSONDocument.empty
    }
    BSONDocument("_id" -> model._id, "name" -> model.name, "session" -> model.session) ++ firstSeenBson
  }
}
