package jrds

import scala.collection.mutable.ListBuffer
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JRField

import models.LibroCajaDiario

import java.util.{HashMap, Map}

class LibroCajaDiarioDS(listData: Seq[LibroCajaDiario]) extends JRDataSource {

  var i = -1

  override def next(): Boolean = {
    i += 1
    i < listData.length
  }

  override def getFieldValue(jrField: JRField): AnyRef = {
    jrField.getName match {
      case "DIA" =>
        listData(i).dia match {
          case Some(dia) => Int.box(dia)
          case None      => Int.box(0)
        }
      case "CODIGO"  => listData(i).codigo.get
      case "NOMBRE"  => listData(i).nombre.get
      case "DEBITO"  => listData(i).debito.get.bigDecimal
      case "CREDITO" => listData(i).credito.get.bigDecimal
      case _         => None
    }
  }
}
