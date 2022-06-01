package services

class StatusValidatorThread {
  implicit private val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  private var notprocessing = true
  ciclo()
  
  def ciclo() = {
    // loop infinito de actualización de estado mientras llega el fin
    if (notprocessing) {
      println("Iniciando Thread Ciclo Proceso")
      val thread = new Thread {
            override def run {
              while (true) {
                println("Ejecutando Ciclo Proceso")
                Thread.sleep(21600000)
                //Thread.sleep(60000)
              }
            }
      }
      notprocessing = false
      thread.start()
    }
  }
}