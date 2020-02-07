package jrds

import scala.collection.mutable.ListBuffer
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JRField

import models.LibroCajaDiario

import java.util.{ HashMap, Map }

class LibroCajaDiarioDS(listData: ListBuffer[LibroCajaDiario]) extends JRDataSource {

    var i = -1

    override def next(): Boolean = {
        i += 1
        i < listData.length
      }

    override def getFieldValue(jrField: JRField): AnyRef = {
        jrField.getName match {
          case "dia" => listData(i).dia
          case "codigo" ⇒ listData(i).codigo.get.substring(0,4)
          case "nombre" ⇒ listData(i).nombre.get
          case "debito" ⇒ listData(i).debito.get.bigDecimal
          case "credito" ⇒ listData(i).credito.get.bigDecimal
          case _ ⇒ None
        }
      }
}