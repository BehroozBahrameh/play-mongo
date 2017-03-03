package dao

import javax.inject._
import play.modules.reactivemongo._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._
import models.PlayerModel
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.collections.bson._
import reactivemongo.bson._
import reactivemongo.api.commands.UpdateWriteResult

@Singleton
class playerDao @Inject()(val reactiveMongoApi: ReactiveMongoApi) {
  
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection[BSONCollection]("player"))
  
  def createNewPlayer(player: PlayerModel) : Future[UpdateWriteResult] = {
    val query = BSONDocument("name" -> player.name)
    val update = BSONDocument("$setOnInsert" -> player)
    collection flatMap ( _.update(query,update,upsert=true) )
  }
  
  def find(name: String) : Future[Option[PlayerModel]] = {
    val query = BSONDocument("name" -> name)
    collection flatMap ( _.find(query).one[PlayerModel] )
  }
  
}