package service

import dao.playerDao
import javax.inject._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import models.PlayerModel
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import scala.concurrent.Future
import models.NewPlayer

@Singleton
class PlayerService @Inject() (playerDao: playerDao){
  
  def createNewPlayer(newPlayer: NewPlayer) : Future[JsObject] = {
    playerDao.find(newPlayer.name) flatMap { _ match {
      case Some(player) => Future(Json.obj("error" -> "Player already exists."))
      case None => 
        val player = PlayerModel.fromNewPlayer(newPlayer)
        playerDao.createNewPlayer(player) map { res => res.nModified match {
          case 0 => Json.obj("error" -> "Player already exists")
          case _ => player.toJson }
        }
    } }
  }
  
  def findPlayer(name: String) : Future[JsObject] = {
    playerDao.find(name) map { _ match {
      case Some(player) => player.toJson
      case None => Json.obj("error" -> "Player not found")
    } }
  }
  
}