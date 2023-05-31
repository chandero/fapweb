package startup

import scala.util.control.Breaks._
import javax.inject.Inject
import models.CreditoRepository

class CicloAplicarLiquidacion @Inject()(service: CreditoRepository) extends Runnable {
    override def run() {
        println("CicloAplicarLiquidacion was executed")
         breakable {
            while (true) {
                service.aplicarLiquidacionPendiente()
                Thread.sleep(60000)
            }
        }
    }
}