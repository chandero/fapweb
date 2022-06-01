package listeners

import net.sf.jasperreports.engine.fill.AsynchronousFillHandle
import net.sf.jasperreports.engine.fill.FillListener
import net.sf.jasperreports.engine.util.JRLoader
import net.sf.jasperreports.engine.JasperPrint

class JrListener(var pagina: Int) extends FillListener {

  @Override
  def pageUpdated(jasperPrint: JasperPrint, pageIndex: Int): Unit = {
    if (pageIndex > pagina) {
      pagina = pageIndex
      println("pagina listener: " + pagina)
    }
  }

  @Override
  def pageGenerated(jasperPrint: JasperPrint, pageIndex: Int): Unit = {
    println("pageGenerated " + pageIndex)
  }

  def getPagina(): Int = {
    pagina
  }

}
