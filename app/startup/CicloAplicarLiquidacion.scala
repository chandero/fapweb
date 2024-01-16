package startup

import scala.util.control.Breaks._
import javax.inject.Inject
import models.CreditoRepository
import models.EspecialRepository

class CicloAplicarLiquidacion @Inject()(service: CreditoRepository, sEspecial: EspecialRepository) extends Runnable {
    override def run() {
        println("CicloAplicarLiquidacion was executed")
         breakable {
            while (true) {
                println("CicloAplicarLiquidacion is running")
                sEspecial.enviarCorreosLey2300()                
                service.aplicarLiquidacionPendiente()
                Thread.sleep(300000)
            }
        }
    }
}