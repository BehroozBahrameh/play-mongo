package controllers
import javax.inject._

import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo._
import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import service.PlayerService

import scala.concurrent.{ExecutionContext, Future}
import models.PlayerModel
import models.NewPlayer
import models.UpdatePlayerScore

@Singleton
class PlayerController @Inject() (playerService: PlayerService) extends Controller {
  
  val transformer: Reads[JsObject] =
    Reads.jsPickBranch[JsString](__ \ "name") and
      Reads.jsPickBranch[JsString](__ \ "session") reduce

  val updateScoreTransformer: Reads[JsObject] =
    Reads.jsPickBranch[JsString](__ \ "playerId") and
      Reads.jsPickBranch[JsString](__ \ "session") and
        Reads.jsPickBranch[JsNumber](__ \ "score") and
          Reads.jsPickBranch[JsNumber](__ \ "timeStamp") reduce
  
  def create = Action.async(parse.json) { request =>
       request.body.transform(transformer) match {
         case JsSuccess(player, _) => playerService.createNewPlayer(NewPlayer.fromJson(player)) map { res => Ok(res) }
         case _ => Future.successful(BadRequest("Invalid JSON"))
  } }
  
  def findByName(name: String) = Action.async {
    playerService.findPlayer(name) map { res => Ok(res) }
  }

  def updateScore() = Action.async(parse.json){request =>
    request.body.transform(updateScoreTransformer) match {
      case JsSuccess(player, _) => playerService.updateScore(UpdatePlayerScore.fromJson(player)) map { res => Ok(res)}
      case _ => Future.successful(BadRequest("Invalid JSON"))
    }
  }
}