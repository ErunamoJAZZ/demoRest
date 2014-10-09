package controllers

import play.api.Play.current
import play.api.libs.ws.WS
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

/*
 * Push de android.
 */
object push {
  val gcm_url = "https://android.googleapis.com/gcm/send"
  val gcm_authorization = "key= ... " //TODO Authorization:key=AIzaSyB-1uEai2WiUapxCs2Q0GZYzPu7Udno5aA
  //Content-Type: application/json

  /*
   * Función de notificaciones Push en Android.
   * 
   * registration_ids:lista de strings
   * collapse_key: un string
   * data: otro objeto json con todo lo que se quiera mandar.
   * delay_while_idle: booleano
   * time_to_live: Segundos
   */
  def sendGCMto(reg_ids: Seq[String], datos: JsValue, collapse: String = "demo_default") = {
    val dato_json = Json.obj(
      "registration_ids" -> reg_ids,
      "collapse_key" -> collapse, //un key para mensajes repetidos
      "data" -> datos)

    WS.url(gcm_url)
      .withHeaders(
        "Content-Type" -> "application/json",
        "Authorization" -> gcm_authorization)
      .post(dato_json).map {
        response =>
          {
            //Respuesta de cuando se logra enviar el Push 
            play.Logger.info("]> Notificación de android (GCM). Respuesta: " + response.status)
            play.Logger.info("]> " + response.body)
          }
      }
  }

  /*
   * Función que envía a cualquiera  
   */
  //TODO

}
