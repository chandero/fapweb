package utilities

import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.PDPageContentStream
import scala.collection.JavaConverters._
import java.io.IOException
import org.apache.pdfbox.util.Matrix

class Paragraph(private val x: Float, private val y: Float, private val text: String) {
  private var width = 500
  private var font = PDType1Font.HELVETICA
  private var fontSize = 10
  private var color = 0

  def withWidth(width: Int): Paragraph = {
    this.width = width
    this
  }

  def withFont(font: PDType1Font, fontSize: Int): Paragraph = {
    this.font = font
    this.fontSize = fontSize
    this
  }

  def withColor(color: Int): Paragraph = {
    this.color = color
    this
  }

  def getColor: Int = color

  def getX: Float = x

  def getY: Float = y

  def getWidth: Int = width

  def getText: String = text

  def getFont: PDType1Font = font

  def getFontSize: Int = fontSize

  def getFontHeight: Float = {
        font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize
    }

  @throws(classOf[IOException])
  def getLines: List[String] = {
    val split = text.split("(?<=\\W)")
    val possibleWrapPoints = new Array[Int](split.length)
    possibleWrapPoints(0) = split(0).length
    for (i <- 1 until split.length) {
      possibleWrapPoints(i) = possibleWrapPoints(i - 1) + split(i).length
    }
    var start = 0
    var end = 0
    val result = scala.collection.mutable.ListBuffer.empty[String]
    for (i <- possibleWrapPoints) {
      val width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize
      if (start < end && width > this.width) {
        result += text.substring(start, end)
        start = end
      }
      end = i
    }
    // Last piece of text
    result += text.substring(start)
    result.toList
  }
}

class ParagraphWriter(out: PDPageContentStream) {
  @throws(classOf[IOException])
  def write(paragraph: Paragraph): Unit = {
/*     out.beginText()
 */    //out.appendRawCommands(paragraph.getFontHeight + " TL\n")
    out.setFont(paragraph.getFont, paragraph.getFontSize)
    out.setTextMatrix(Matrix.getTranslateInstance(paragraph.getX, paragraph.getY))
    out.setStrokingColor(paragraph.getColor)
    val lines = paragraph.getLines.asJava
    val iterator = lines.iterator
    while (iterator.hasNext) {
      out.showText(iterator.next.trim)
      if (iterator.hasNext) {
        out.newLineAtOffset(0, -paragraph.getFontHeight)
        //out.appendRawCommands("T*\n")
      }
    }
    /* out.endText() */
  }
}
