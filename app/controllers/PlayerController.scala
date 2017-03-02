package controllers
import javax.inject._

import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import service.PlayerService

import scala.concurrent.{ExecutionContext, Future}
import models.PlayerModel
import models.NewPlayer

@Singleton
class PlayerController @Inject() (playerService: PlayerService)(implicit exec: ExecutionContext) extends Controller {
  
  val transformer: Reads[JsObject] =
    Reads.jsPickBranch[JsString](__ \ "name") and
      Reads.jsPickBranch[JsString](__ \ "session") reduce
  
  def create = Action.async(parse.json) { request =>
       request.body.transform(transformer) match {
         case JsSuccess(player, _) => playerService.createNewPlayer(NewPlayer.fromJson(player)) map { res => Ok(res) }
         case _ => Future.successful(BadRequest("Invalid JSON"))
  } }
  
  def findByName(name: String) = Action.async {
    playerService.findPlayer(name) map { res => Ok(res) }
  }
}