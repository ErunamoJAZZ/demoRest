package model;

import play.api.db.slick.Config.driver.simple._

case class User(
  id: Option[Int],
  username: String,
  nombre: String,
  android_id: String)

/* Table mapping
*/
class UsersTable(tag: Tag) extends Table[User](tag, "usuario") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def nombre = column[String]("nombre")
  def android_id = column[String]("android_id")

  def idx = index("usuario_unico", username, unique = true)

  def * = (id.?, username, nombre, android_id) <> (User.tupled, User.unapply _)
}