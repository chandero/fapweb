package utilities

import java.util.HashMap
import javax.inject.Inject

import java.io.InputStream
import java.io.OutputStream
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream

import play.api.Environment
import play.api.mvc._

import scala.io.Source
import scala.collection.JavaConverters._

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.model.datastorage.migration.VariablePrepare

import org.docx4j.Docx4J
import org.docx4j.convert.out.microsoft_graph.DocxToPdfExporter
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.plutext.msgraph.convert.DocxToPdfConverter
import org.plutext.msgraph.convert.graphsdk.DocxToPdfConverterLarge
import org.plutext.msgraph.convert.AuthConfig

import org.apache.poi.openxml4j.util.ZipSecureFile

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph

import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;

import java.io.File
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import com.deepoove.poi.XWPFTemplate

class DocxGenerator {
  def generateDocxFileFromTemplate(
      template: String,
      data: HashMap[String, String]
  ): Array[Byte] = {

    var path = "/public/template/"
    var tis: InputStream = getClass.getResourceAsStream(path + template)
    var wpmlp: WordprocessingMLPackage = WordprocessingMLPackage.load(tis)

    var documentPart: MainDocumentPart = wpmlp.getMainDocumentPart()
    VariablePrepare.prepare(wpmlp);
    documentPart.variableReplace(data);

    var file = new File("/tmp/test.docx")
    var os: ByteArrayOutputStream = new ByteArrayOutputStream()
    var target = new BufferedOutputStream(new FileOutputStream(file))
    try wpmlp.save(target)
    finally target.close
    wpmlp.save(os)
    os.toByteArray
  }

  def generateDocxFileFromTemplate2(
      template: String,
      data: HashMap[String, Object]
  ): Array[Byte] = {
    var path = System.getProperty("user.dir") + "/resources/"
    var docx_file: String = path + template
    var os: ByteArrayOutputStream = new ByteArrayOutputStream()
    XWPFTemplate
      .compile(docx_file)
      .render(data)
      .write(os)
    os.toByteArray()
  }

  def mergeDocuments(doc_main: Array[Byte], doc_merge: Array[Byte]) = {
    var doc = new XWPFDocument(new ByteArrayInputStream(doc_main))
    var docToMerge = new XWPFDocument(new ByteArrayInputStream(doc_merge))
    var pos: Int = doc.getParagraphs().size() - 1
    var paragraph = doc.createParagraph();
    paragraph.setPageBreak(true)
    doc.setParagraph(paragraph, pos)
    for (par: XWPFParagraph <- docToMerge.getParagraphs().asScala.toList) {
      doc.createParagraph()
      pos += 1
      doc.setParagraph(par, pos)
    }
    var out = new ByteArrayOutputStream()
    doc.write(out)
    out.toByteArray
  } // end of mergeDocuments

  def convertDocxToPdf(docx: Array[Byte]) = {
    var document = new XWPFDocument(new ByteArrayInputStream(docx))
    var options = PdfOptions.create()
    var out = new ByteArrayOutputStream()
    PdfConverter.getInstance().convert(document, out, options)
    document.close()
    out.close()
    out.toByteArray()
  }
}

//  doc: XWPFDocument = new XWPFDocument(new ByteArrayInputStream(byteData));

object DocxGenerator extends DocxGenerator
