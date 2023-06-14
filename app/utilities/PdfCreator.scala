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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.util.Matrix
import java.awt.geom.Point2D
import org.joda.time.DateTime


class PdfCreator {

    private var _contenido: PDPageContentStream = null

    def pazYSalvoCreator(_id_colocacion: String, _listDeudor: ju.ArrayList[ju.HashMap[String, Object]]): Array[Byte] = {
        val _documento = new PDDocument()
        val _pagina = new PDPage()
        _documento.addPage(_pagina)
        var fontSize: Int = 14
        _contenido = new PDPageContentStream(_documento, _pagina)
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

        val leading: Float = 1.5f * fontSize
        val fontHeight = 12

        val pdImage = PDImageXObject.createFromFile(s"${System.getProperty("user.dir")}/resources/image/logofull.png", _documento);

        _contenido.drawImage(pdImage, 50, 700, 200, 80);

        var _offset = new Point2D.Float(0, 80)
        addCenteredText("EL SUSCRITO REPRESENTANTE LEGAL", _fuente, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 100)
        addCenteredText("DE LA FUNDACION APOYO", _fuente, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 120)
        addCenteredText("NIT. 804.015.942-5", _fuente, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 140)
        addCenteredText("CERTIFICA QUE:", _fuente, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 160)
        println("offset: " + _offset)
        var yCordinate = _pagina.getMediaBox().getHeight() - 180
        var startX = _pagina.getCropBox().getLowerLeftX() + 50
        var endX = _pagina.getCropBox().getUpperRightX() - 30        
        _contenido.moveTo(startX, yCordinate)
        _contenido.lineTo(endX, yCordinate)
        _contenido.stroke()        

        _offset = new Point2D.Float(180, _pagina.getMediaBox().getHeight() - 200)
        _contenido.beginText()
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("NOMBRE")
        _contenido.endText()
        _offset = new Point2D.Float(360, _pagina.getMediaBox().getHeight() - 200)
        _contenido.beginText()
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("DOCUMENTO")
        _contenido.endText()        
        _offset = new Point2D.Float(510, _pagina.getMediaBox().getHeight() - 200)
        _contenido.beginText()
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("TIPO")
        _contenido.endText()        

        // ciclo para imprimir los deudores
        _offset = new Point2D.Float(50, _pagina.getMediaBox().getHeight() - 220)
        val _it = _listDeudor.iterator()
        while (_it.hasNext) { val _d = _it.next()
            _contenido.beginText()
            _contenido.setFont(_fuente, 10)
            _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
            _contenido.showText(_d.get("nombre").toString())
            _contenido.endText()
            _offset = new Point2D.Float(370, _offset.y)
            _contenido.beginText()
            _contenido.setFont(_fuente2, 10)
            _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
            _contenido.showText(_d.get("documento").toString())
            _contenido.endText()
            _offset = new Point2D.Float(490, _offset.y)
            _contenido.beginText()
            _contenido.setFont(_fuente2, 10)
            _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
            _contenido.showText(_d.get("tipo").toString())
            _contenido.endText()
            _offset = new Point2D.Float(50, _offset.y - 15)
        }
        _offset = new Point2D.Float(50, _offset.y - 20)
        
        _contenido.beginText()
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        var _texto = "Se encuentran a "
        _contenido.showText(_texto)
        _offset = new Point2D.Float(50 + (_fuente2.getStringWidth(_texto) / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Paz y Salvo")
        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth("Paz y Salvo") / 1000 * 12),_offset.y)
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(" por todo concepto en la obligación número ")
        _offset = new Point2D.Float(_offset.x + (_fuente2.getStringWidth(" por todo concepto en la obligación número ") / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_id_colocacion)
        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth(_id_colocacion) / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(" en la")
        _offset = new Point2D.Float(50, _offset.y - 15)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Fundación Apoyo.")
        _offset = new Point2D.Float(50,_offset.y - 30)
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("En razón a la cancelación total de la referida obligación número ")
        _offset = new Point2D.Float(50 + (_fuente2.getStringWidth("En razón a la cancelación total de la referida obligación número ") / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_id_colocacion + ",")
        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth(_id_colocacion + ",") / 1000 * 10) + 12,_offset.y)
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(" nuestra entidad ")
        _contenido.endText()
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        val _texto1 = "realiza los trámites correspondientes para la actualización de datos registrados en las "
        val _texto2 = "Centrales de Riesgo, aclarando que el manejo del histórico y de permanencia en la base es "
        val _texto3 = "responsabilidad directa de la Central de Riesgo y que la información sobre Paz y Salvo"
        val _texto4 = "se refiere únicamente al producto o servicio referido, establecido en esta entidad."
        _contenido.setFont(_fuente2, 12)
        // _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto1)
        addJustifyText(_texto1, _fuente2, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        //_contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto2)
        addJustifyText(_texto2, _fuente2, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        //_contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto3)
        addJustifyText(_texto3, _fuente2, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        // _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto4)
        addJustifyText(_texto4, _fuente2, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 60)
        _contenido.beginText()
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Se expide en Bucaramanga el día " + Utility.fechaatextosindiasemana(Some(new DateTime())).toLowerCase + ".")
        _offset = new Point2D.Float(50 ,_offset.y - 45)
        _contenido.setFont(_fuente2, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Cordialmente,")
        _contenido.endText()
        val pdImage2 = PDImageXObject.createFromFile(s"${System.getProperty("user.dir")}/resources/image/FirmaEliecer.png", _documento);

        _offset = new Point2D.Float(50 ,_offset.y - 120)
        _contenido.drawImage(pdImage2, 50, _offset.y, 210, 120);

        _offset = new Point2D.Float(50 , 640)
        addCenteredText("Cra 20 N° 36-06 Ofc. 405 y 407 Edificio Sagrada Familia- Centro Bucaramanga – Santander", _fuente2, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 655)
        addCenteredText("Teléfonos 3162854212 - 3176801924 – 3173836208", _fuente2, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 670)
        addCenteredText("E-mail: fap@fundacionapoyo.com", _fuente2, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 685)
        addCenteredText("CONSTRUYENDO FUTURO", _fuente2, 10, _contenido, _pagina, _offset)


        _contenido.close()

        println("PDF creado:" + _documento.toString())

        val os = new ByteArrayOutputStream()
        _documento.save(os)
        _documento.close()
        os.toByteArray()
    }

    def referenciaComercialCreator(_id_persona: String, _id_de: String, _nombre: String, _fecha_vinculacion: DateTime, _listCredito: ju.ArrayList[ju.HashMap[String, AnyRef]]): Array[Byte] = {
        val _documento = new PDDocument()
        val _pagina = new PDPage()
        _documento.addPage(_pagina)
        var fontSize: Int = 14
        _contenido = new PDPageContentStream(_documento, _pagina)
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = new ju.Date()
        val _fuenteN = PDType1Font.HELVETICA_BOLD
        val _fuente = PDType1Font.HELVETICA
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

        val leading: Float = 1.5f * fontSize
        val fontHeight = 12

        val pdImage = PDImageXObject.createFromFile(s"${System.getProperty("user.dir")}/resources/image/logofull.png", _documento);

        _contenido.drawImage(pdImage, 50, 700, 200, 80);

        var _offset = new Point2D.Float(0, 80)
        addCenteredText("FUNDACION APOYO", _fuenteN, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 100)
        addCenteredText("NIT. 804.015.942-5", _fuenteN, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 120)
        addCenteredText("CERTIFICA QUE:", _fuenteN, 13, _contenido, _pagina, offset = _offset)
        _offset = new Point2D.Float(0, 140)

        val _texto01 = _nombre + ","
        _offset = new Point2D.Float(60, _pagina.getMediaBox().getHeight() - 200)
        _contenido.beginText()
        _contenido.setFont(_fuenteN, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_texto01)
        _contenido.endText()

        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth(_texto01) / 1000 * 12) + 5,_offset.y)
        val _texto02 = "con documento de identidad número " + _id_persona
        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_texto02)
        _contenido.endText()

        _offset = new Point2D.Float(50, _offset.y - 20)
        val _texto03 = "de " + _id_de + ", ha estado vinculado a la "
        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_texto03)
        _contenido.endText()

        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth(_texto03) / 1000 * 12) + 5,_offset.y)
        val _texto04 = "Fundación Apoyo,"
        _contenido.beginText()
        _contenido.setFont(_fuenteN, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_texto04)
        _contenido.endText()

        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth(_texto04) / 1000 * 12) + 5,_offset.y)
        val _texto05 = "desde el " + Utility.fechaatextosindiasemana(Some(_fecha_vinculacion)) + "."
        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_texto05)
        _contenido.endText()

        _offset = new Point2D.Float(50, _offset.y - 20)
        val _texto06 = "A la fecha es titular de las siguientes obligaciones activas."
        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(_texto05)
        _contenido.endText()

/*         println("offset: " + _offset)
        var yCordinate = _pagina.getMediaBox().getHeight() - 180
        var startX = _pagina.getCropBox().getLowerLeftX() + 50
        var endX = _pagina.getCropBox().getUpperRightX() - 30        
        _contenido.moveTo(startX, yCordinate)
        _contenido.lineTo(endX, yCordinate)
        _contenido.stroke() */

        _offset = new Point2D.Float(60, _pagina.getMediaBox().getHeight() - 200)
        _contenido.beginText()
        _contenido.setFont(_fuenteN, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Número de Obligación")
        _contenido.endText()
        _offset = new Point2D.Float(260, _pagina.getMediaBox().getHeight() - 200)
        _contenido.beginText()
        _contenido.setFont(_fuenteN, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Saldo")
        _contenido.endText()                      

        // ciclo para imprimir los deudores
        _offset = new Point2D.Float(50, _pagina.getMediaBox().getHeight() - 220)
        val _it = _listCredito.iterator()
        while (_it.hasNext) { val _d = _it.next()
            _contenido.beginText()
            _contenido.setFont(_fuente, 10)
            _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
            _contenido.showText(_d.get("id_colocacion").toString())
            _contenido.endText()
            _offset = new Point2D.Float(370, _offset.y)
            _contenido.beginText()
            _contenido.setFont(_fuente, 10)
            _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
            _contenido.showText(_d.get("saldo").toString())
            _contenido.endText()
            _offset = new Point2D.Float(50, _offset.y - 15)
        }
        _offset = new Point2D.Float(50, _offset.y - 20)
        
        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        var _texto = "Se encuentran a "
        _contenido.showText(_texto)
        _offset = new Point2D.Float(50 + (_fuente.getStringWidth(_texto) / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Paz y Salvo")
        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth("Paz y Salvo") / 1000 * 12),_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(" por todo concepto en la obligación número ")
        _offset = new Point2D.Float(_offset.x + (_fuente.getStringWidth(" por todo concepto en la obligación número ") / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(" en la")
        _offset = new Point2D.Float(50, _offset.y - 15)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Fundación Apoyo.")
        _offset = new Point2D.Float(50,_offset.y - 30)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("En razón a la cancelación total de la referida obligación número ")
        _offset = new Point2D.Float(50 + (_fuente.getStringWidth("En razón a la cancelación total de la referida obligación número ") / 1000 * 12) + 5,_offset.y)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText(" nuestra entidad ")
        _contenido.endText()
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        val _texto1 = "realiza los trámites correspondientes para la actualización de datos registrados en las "
        val _texto2 = "Centrales de Riesgo, aclarando que el manejo del histórico y de permanencia en la base es "
        val _texto3 = "responsabilidad directa de la Central de Riesgo y que la información sobre Paz y Salvo"
        val _texto4 = "se refiere únicamente al producto o servicio referido, establecido en esta entidad."
        _contenido.setFont(_fuente, 12)
        // _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto1)
        addJustifyText(_texto1, _fuente, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        //_contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto2)
        addJustifyText(_texto2, _fuente, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        //_contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto3)
        addJustifyText(_texto3, _fuente, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 15)
        // _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        //_contenido.showText(_texto4)
        addJustifyText(_texto4, _fuente, 12, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 ,_offset.y - 60)
        _contenido.beginText()
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Se expide en Bucaramanga el día " + Utility.fechaatextosindiasemana(Some(new DateTime())).toLowerCase + ".")
        _offset = new Point2D.Float(50 ,_offset.y - 45)
        _contenido.setFont(_fuente, 12)
        _contenido.setTextMatrix(Matrix.getTranslateInstance(_offset.x, _offset.y))
        _contenido.showText("Cordialmente,")
        _contenido.endText()
        val pdImage2 = PDImageXObject.createFromFile(s"${System.getProperty("user.dir")}/resources/image/FirmaEliecer.png", _documento);

        _offset = new Point2D.Float(50 ,_offset.y - 120)
        _contenido.drawImage(pdImage2, 50, _offset.y, 210, 120);

        _offset = new Point2D.Float(50 , 640)
        addCenteredText("Cra 20 N° 36-06 Ofc. 405 y 407 Edificio Sagrada Familia- Centro Bucaramanga – Santander", _fuente, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 655)
        addCenteredText("Teléfonos 3162854212 - 3176801924 – 3173836208", _fuente, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 670)
        addCenteredText("E-mail: fap@fundacionapoyo.com", _fuente, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 685)
        addCenteredText("CONSTRUYENDO FUTURO", _fuente, 10, _contenido, _pagina, _offset)


        _contenido.close()

        println("PDF creado:" + _documento.toString())

        val os = new ByteArrayOutputStream()
        _documento.save(os)
        _documento.close()
        os.toByteArray()
    }


    def createFapPage(_documento: PDDocument): PDPage = {
        val _pagina = new PDPage(PDRectangle.LETTER)
        val _fuente2 = PDType1Font.HELVETICA
        _contenido = new PDPageContentStream(_documento, _pagina)
        _documento.addPage(_pagina)
        val pdImage = PDImageXObject.createFromFile(s"${System.getProperty("user.dir")}/resources/image/logofull.png", _documento);
        _contenido.drawImage(pdImage, 50, 700, 200, 80);
        var _offset = new Point2D.Float(50 , 640)
        addCenteredText("Cra 20 N° 36-06 Ofc. 405 y 407 Edificio Sagrada Familia- Centro Bucaramanga – Santander", _fuente2, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 655)
        addCenteredText("Teléfonos 3162854212 - 3176801924 – 3173836208", _fuente2, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 670)
        addCenteredText("E-mail: fap@fundacionapoyo.com", _fuente2, 10, _contenido, _pagina, _offset)
        _offset = new Point2D.Float(50 , 685)
        addCenteredText("CONSTRUYENDO FUTURO", _fuente2, 10, _contenido, _pagina, _offset)
        _pagina
    } 

    def addCenteredText(text: String, font: PDFont, fontSize: Int, _contenido: PDPageContentStream,  page: PDPage, offset: Point2D.Float): Unit = {
        var leading: Float = 1.5f * fontSize

        val titleWidth:Float = font.getStringWidth(text) / 1000 * fontSize;
        val titleHeight:Float = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        val startX:Float = (page.getMediaBox().getWidth() - titleWidth) / 2;
        val startY:Float = page.getMediaBox().getHeight() - 30 - titleHeight;

        _contenido.beginText()
        _contenido.setFont(font, fontSize)
        _contenido.newLineAtOffset(startX, page.getMediaBox().getHeight() - offset.y - 30)
        _contenido.showText(text)
        _contenido.endText()
    }

    def addCenteredPageText(text: String, font: PDFont, fontSize: Int, content: PDPageContentStream,  page: PDPage, offset: Point2D.Float): Unit = {
        var leading: Float = 1.5f * fontSize

        content.setFont(font, fontSize)
        content.beginText()
    // Rotate the text according to the page orientation
        var pageIsLandscape: Boolean = isLandscape(page)
        var pageCenter: Point2D.Float = getCenter(page)

    // We use the text's width to place it at the center of the page
        var stringWidth: Float = getStringWidth(text, font, fontSize)
        if (pageIsLandscape) {
            var textX:Float = pageCenter.x - stringWidth / 2F + offset.x
            var textY:Float = pageCenter.y - offset.y
        // Swap X and Y due to the rotation
            content.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, textY, textX))
        } else {
            var textX: Float = pageCenter.x - stringWidth / 2F + offset.x
            var textY: Float = pageCenter.y + offset.y
            content.setTextMatrix(Matrix.getTranslateInstance(textX, textY))
        }

        content.showText(text)
        content.newLineAtOffset(0, -leading)
        content.endText()
    }

    def addJustifyText(text: String, font: PDFont, fontSize: Int, content: PDPageContentStream,  page: PDPage, offset: Point2D.Float): Unit = {
        content.setFont(font, fontSize)
        content.beginText()
        val preCharSpacing = 0.1F
        var leading: Float = 1.5f * fontSize;
        var charSpacing = 0F
        var mediabox: PDRectangle = page.getMediaBox()
        var margin:Float = 50
        var width:Float = mediabox.getWidth() - 2*margin
        var size:Float = fontSize * font.getStringWidth(text) / 1000
        var free:Float = width - size
        if (free > 0)
        {
            charSpacing = free / (text.length() - 1)
        }
        content.setTextMatrix(Matrix.getTranslateInstance(offset.x, offset.y))
        content.setCharacterSpacing(charSpacing)
        content.showText(text)
        content.setCharacterSpacing(preCharSpacing)
        content.endText()
    }

    def isLandscape(page: PDPage): Boolean = {
        var rotation = page.getRotation()
        var isLandscape = false
        if (rotation == 90 || rotation == 270) {
            isLandscape = true
        } else if (rotation == 0 || rotation == 360 || rotation == 180) {
            isLandscape = false
        } else {
            // LOG.warn("Can only handle pages that are rotated in 90 degree steps. This page is rotated {} degrees. Will treat the page as in portrait format", rotation);
            isLandscape = false;
        }
        isLandscape
    }

    def getCenter(page: PDPage): Point2D.Float = {
        var pageSize: PDRectangle = page.getMediaBox()
        var rotated: Boolean = isLandscape(page)
        var pageWidth: Float = rotated match { case true => pageSize.getHeight() case false => pageSize.getWidth() }
        var pageHeight: Float = rotated match { case true => pageSize.getWidth() case false => pageSize.getHeight() }

        new Point2D.Float(pageWidth / 2F, pageHeight / 2F)
}

    def getStringWidth(text: String, font: PDFont, fontSize: Int) = {
        font.getStringWidth(text) * fontSize / 1000F
    }
    
}

object PdfCreator extends PdfCreator