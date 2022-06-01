package services

import javax.inject._
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future

@Singleton
class ApplicationThread @Inject()(statusValidator: StatusValidatorThread, appLifecycle: ApplicationLifecycle) {
    
    statusValidator.ciclo()

    appLifecycle.addStopHook { () =>
        Future.successful(())
    }
}