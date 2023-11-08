package startup

import javax.inject.Inject

class Startup @Inject()(ciclo:CicloAplicarLiquidacion) {
    println("Startup was executed")
    startup()

    def startup() {
        println("cicloAplicarLiquidacion was executed")
        new Thread(ciclo).start()
    }
}
