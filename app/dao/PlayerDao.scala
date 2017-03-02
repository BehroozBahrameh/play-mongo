package dao

import javax.inject._
import play.modules.reactivemongo._
import scala.concurrent._
import models.PlayerModel
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.collections.bson._
import reactivemongo.bson._

@Singleton
class playerDao @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) {
  
  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection[BSONCollection]("player"))
  
  def insert(newPlayer: PlayerModel) : Future[WriteResult] = {
    collection flatMap ( _.insert(newPlayer.toBSON) )
  }
  
  def find(name: String) : Future[Option[PlayerModel]] = {
    val query = BSONDocument("name" -> name)
    collection flatMap ( _.find(query).one[PlayerModel] )
  }
  
}