package dto

import play.api.libs.json._
import play.api.libs.json.JodaReads
import play.api.libs.json.JodaWrites
import play.api.libs.functional.syntax._

import org.joda.money.Money

case class SimuladorDto(linea: Long, valor: Money, plazo: Int)