package utilities

import java.util.ArrayList
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.PDPageContentStream

import java.awt.Color
import java.io.ByteArrayOutputStream

import java.{util => ju}
import java.text.SimpleDateFormat
import java.io.ByteArrayInputStream


class PdfCreator {

    def pazYSalvoCreator(_id_colocacion: String, _listDeudor: ju.ArrayList[ju.HashMap[String, Object]]): Array[Byte] = {
        val _documento = new PDDocument()
        val _pagina = new PDPage()
        _documento.addPage(_pagina)
        val _contenido = new PDPageContentStream(_documento, _pagina)
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = new ju.Date()
        val _fuente = PDType1Font.HELVETICA_BOLD
        val _fuente2 = PDType1Font.HELVETICA
        val _fuente3 = PDType1Font.HELVETICA_BOLD_OBLIQUE
        val _fuente4 = PDType1Font.HELVETICA_OBLIQUE
        val _fuente5 = PDType1Font.TIMES_ROMAN
        val _fuente6 = PDType1Font.TIMES_BOLD
        val _fuente7 = PDType1Font.TIMES_ITALIC
        val _fuente8 = PDType1Font.COURIER
 
        val _color = new Color(0, 0, 0)
        val _color2 = new Color(255, 255, 255)
        val _color3 = new Color(255, 0, 0)
        val _color4 = new Color(0, 0, 255)

        val _ancho = _pagina.getMediaBox().getWidth()
        val _alto = _pagina.getMediaBox().getHeight()

        val _ancho2 = _ancho - 80
        val _alto2 = _alto - 80

        val leading = 20
        val fontHeight = 12

        _contenido.beginText()
        _contenido.setFont(_fuente5, 12)
        _contenido.setNonStrokingColor(_color)
        _contenido.newLineAtOffset(40, _alto - 40)
        _contenido.showText("EL SUSCRITO REPRESENTANTE LEGAL")
        _contenido.endText()

        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setNonStrokingColor(_color)
        _contenido.newLineAtOffset(40, _alto - 60)
        _contenido.showText("DE LA FUNDACION APOYO")
        _contenido.endText()

        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setNonStrokingColor(_color)
        _contenido.newLineAtOffset(40, _alto - 80)
        _contenido.showText("NIT. 804.015.942-5")
        _contenido.endText()

        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setNonStrokingColor(_color)
        _contenido.newLineAtOffset(40, _alto - 100)
        _contenido.showText("CERTIFICA QUE:")
        _contenido.endText()

        var yCordinate = _pagina.getCropBox().getUpperRightY() - 30
        var startX = _pagina.getCropBox().getLowerLeftX() + 30
        var endX = _pagina.getCropBox().getUpperRightX() - 30

        _contenido.moveTo(startX, yCordinate)
        _contenido.lineTo(endX, yCordinate)
        _contenido.stroke()
        yCordinate = yCordinate - leading

        _contenido.close()

        println("PDF creado:" + _documento.toString())

        val os = new ByteArrayOutputStream()
        _documento.save(os)
        _documento.close()
        os.toByteArray()
    }
}

object PdfCreator extends PdfCreator