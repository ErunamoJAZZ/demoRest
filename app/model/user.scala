package model;

import play.api.db.slick.Config.driver.simple._

case class User(
    id: Option[Int], 
    username: String, 
    nombre: String)

/* Table mapping
*/
class UsersTable(tag: Tag) extends Table[User](tag, "usuario") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def nombre = column[String]("nombre")
  
  def idx = index("usuario_unico", username, unique = true)
  

  def * = (id.?, username, nombre ) <> (User.tupled, User.unapply _)
}