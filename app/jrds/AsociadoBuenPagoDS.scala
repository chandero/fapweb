package jrds

import scala.collection.mutable.ListBuffer
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JRField

import models.AsociadoBuenPago

import java.util.{HashMap, Map}

class AsociadoBuenPagoDS(listData: ListBuffer[AsociadoBuenPago])
    extends JRDataSource {

  var i = -1

  override def next(): Boolean = {
    i += 1
    i < listData.length
  }

  override def getFieldValue(jrField: JRField): AnyRef = {
    jrField.getName match {
      case "primer_apellido"  => listData(i).primer_apellido
      case "segundo_apellido" => listData(i).segundo_apellido
      case "nombre"           => listData(i).nombre.get
      case "documento"        => listData(i).documento.get
      case "telefono"         => listData(i).telefono.get
      case "dias"             => listData(i).dias
      case _                  => None
    }
  }
}
