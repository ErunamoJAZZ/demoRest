package controllers

import play.api._
import model._
import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import play.api.libs.json._
import scala.util._
import play.api.libs.functional.syntax._

object Application extends Controller {

  //Instancia de las Tablas para las querys
  val Users = TableQuery[UsersTable]
  val Pets = TableQuery[PetsTable]

  //conversores implicitos de Objetos a json
  implicit val json_user = Json.format[User]
  implicit val json_pets = Json.format[Pet]

  def index = Action {
    Ok(views.html.index(""))
  }

  /**
   * ***************************
   * * ACIONES PARA EL USUARIO **
   * ***************************
   */
  def newUser = DBAction(parse.tolerantJson) { implicit req =>
    //Valida la estructura del Json
    req.body.validate[User].fold(
      valid = (user => {
        //Se inserta el usuario.
        val user_id = Try((Users returning Users.map(_.id)) += user)

        //Según el resultado de la inserción, responde.
        user_id match {
          case Success(id) => Ok(Json.obj("user_id" -> id))
          case Failure(e) => {
            //play.Logger.error("Error insertando un usuario.",e)
            NotAcceptable(Json.obj(
              "err" -> "No se pudo insertar el usuario.",
              "msg" -> e.getMessage()))
          }
        }
      }),
      invalid = (e => BadRequest(Json.obj("err" -> "Invalid Json"))))
  }

  def getUser_id(user_id: Int) = DBAction { implicit req =>
    //Se escribe un query, usando un for por comprención.
    val q = for {
      u <- Users
      if u.id === user_id
    } yield u

    //se toma el primer elemento (los querys deben pensarse como listas).
    // y se manda denvuelto dentro de un Try
    compositor_userGet(Try(q.first))
  }

  def getUser_username(username: String) = DBAction { implicit req =>
    //Se escribe un query, usando un for por comprención.
    val q = for {
      u <- Users
      if u.username === username
    } yield u

    //se toma el primer elemento (los querys deben pensarse como listas).
    // y se manda denvuelto dentro de un Try
    compositor_userGet(Try(q.first))
  }

  private def compositor_userGet(user: Try[User]): Result = {
    //Según el resultado de la consulta, responde.
    user match {
      case Success(u) => Ok(Json.obj("user" -> u))
      case Failure(e) => {
        //play.Logger.error("Error buscando un usuario.",e)
        NotAcceptable(Json.obj(
          "err" -> "No se pudo encontrar el usuario.",
          "msg" -> e.getMessage()))
      }
    }
  }

  def deleteUser(user_id: Int) = DBAction { implicit req =>
    val q = Users.where(u => u.id === user_id)

    Try(q.delete) match {
      case Success(id) => Ok(Json.obj("status" -> id))
      case Failure(e) => {
        NotAcceptable(Json.obj(
          "err" -> "No se pudo eliminar el usuario.",
          "msg" -> e.getMessage()))
      }
    }
  }
  /**
   * ************************
   * * ACIONES PARA MASCOTA **
   * ************************
   */

  def newPet = DBAction(parse.tolerantJson) { implicit req =>
    //Valida la estructura del Json
    req.body.validate[Pet].fold(
      valid = (pet => {
        val pet_id = Try((Pets returning Pets.map(_.id)) += pet)

        //Según el resultado de la inserción, responde.
        pet_id match {
          case Success(id) => Ok(Json.obj("pet_id" -> id))
          case Failure(e) => {
            NotAcceptable(Json.obj(
              "err" -> "No se pudo insertar la mascota.",
              "msg" -> e.getMessage()))
          }
        }
      }),
      invalid = (e => BadRequest(Json.obj("err" -> "Invalid Json"))))
  }

  def getPet_id(pet_id: Int) = DBAction { implicit req =>
    //Se escribe un query, usando un for por comprención.
    val q = for {
      u <- Pets
      if u.id === pet_id
    } yield u

    //se toma el primer elemento (los querys deben pensarse como listas).
    Try(q.first) match {
      case Success(u) => Ok(Json.obj("pet" -> u))
      case Failure(e) => {
        //play.Logger.error("Error buscando un usuario.",e)
        NotAcceptable(Json.obj(
          "err" -> "No se pudo encontrar la mascota.",
          "msg" -> e.getMessage()))
      }
    }
  }

  def deletePet(pet_id: Int) = DBAction { implicit req =>
    val q = Pets.where(u => u.id === pet_id)

    Try(q.delete) match {
      case Success(id) => Ok(Json.obj("status" -> id))
      case Failure(e) => {
        NotAcceptable(Json.obj(
          "err" -> "No se pudo eliminar la mascota.",
          "msg" -> e.getMessage()))
      }
    }
  }

  def updatePetLocation(pet_id: Int) = DBAction(parse.tolerantJson) { implicit req =>

    val customReads: Reads[(Long, Long)] =
      (JsPath \ "lon").read[Long] and
        (JsPath \ "lat").read[Long] tupled

    //Se valida el json y el usuario, y se responde segun el caso.
    customReads.reads(req.body).fold(valid = (location => {

      val q = for {
        u <- Pets
        if u.id === pet_id
      } yield (u.longitud, u.latitud)

      val updating = Try(q.update(location))
      //Según el resultado de la inserción, responde.
      updating match {
        case Success(id) => Ok(Json.obj("pet_id" -> id))
        case Failure(e) => {
          NotAcceptable(Json.obj(
            "err" -> "No se pudo insertar la mascota.",
            "msg" -> e.getMessage()))
        }
      }
    }),
      invalid = (e => BadRequest(Json.obj("err" -> "Invalid Json"))))

  }

  /**
   * ****************
   * * COSAS UTILES **
   * ****************
   */
  /*
   * Acciones para la creación y eliminación de la base de datos.
   * 'drop' para borrar, 'create' para crear.
   */
  def dbActions(caso: String = "") = DBAction { implicit req =>
    caso match {
      case "drop" => {
        Users.ddl.drop
        Pets.ddl.drop
        Ok("RIP  (╥_╥)")
      }
      case _ => {
        Users.ddl.create
        Pets.ddl.create
        Ok("( ಠ◡ಠ)  All done")
      }
    }
  }
  
  /*
   * El api es innaccesible
   */
  def inaccesible(any_path: String) = Action {
    ServiceUnavailable(Json.obj("err" -> ("API '"+any_path+"' no existe,")))
  }

}