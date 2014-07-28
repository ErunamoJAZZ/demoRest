package model;

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction._

case class Pet(
    id: Option[Int], 
    dueno: Int,
    nombre: String,
    edad: Int,
    longitud: Long,
    latitud: Long)

/* Table mapping
*/
class PetsTable(tag: Tag) extends Table[Pet](tag, "mascota") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def dueno = column[Int]("dueno")
  def nombre = column[String]("nombre")
  def edad = column[Int]("edad")
  def longitud = column[Long]("longitud")
  def latitud = column[Long]("latitud")

  def fk_dueno = foreignKey("FK_DUENO", dueno, TableQuery[UsersTable])(_.id, Cascade, Cascade)
  
  def * = (id.?, dueno, nombre ,edad ,longitud ,  latitud) <> (Pet.tupled, Pet.unapply _)
}
