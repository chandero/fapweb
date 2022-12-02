package controllers

import java.io.File

import com.google.inject.Inject
import com.typesafe.config.{Config, ConfigFactory}
import play.api.mvc._

class FrontController @Inject() (cc: ControllerComponents) extends AbstractController(cc){
  def index = Action {
    Ok("<span>FapWebBackend is running....</span>")
  }
}
