package services

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.QueueOfferResult.{Dropped, Enqueued, Failure, QueueClosed}
import akka.stream.scaladsl.{Source, SourceQueueWithComplete}
import akka.stream.{Materializer, OverflowStrategy}

import play.api.libs.json._
import play.api.libs.EventSource.Event

import scala.collection.mutable
import scala.concurrent.Future

import models.ProcessEvent

object ProcessingProgressTrackerEvent {

  implicit private val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit private val system: ActorSystem = ActorSystem("ProcessingProgressTracker")
  // implicit private val materializer: Materializer = Materializer()

  private val sourceOfStatusesPerSession = mutable.HashMap.empty[String, Source[ProcessEvent, NotUsed]]
  private val sourceQueueOfStatusesPerSession = mutable.HashMap.empty[String, SourceQueueWithComplete[ProcessEvent]]
  private val sourceOfAlivePerSession = mutable.ListBuffer.empty[String]
  private var notsending = true

  def getSourceOfStatuses(sessionId: String): Option[Source[ProcessEvent, NotUsed]] = {
    sourceOfStatusesPerSession.get(sessionId)
  }
  // it's configured to keep the source open for 30s (reusing it for consecutive requests)
  def createSourceOfStatuses(sessionId: String): Source[ProcessEvent, NotUsed] = {
    val initialSourceOfStatuses = Source.queue[ProcessEvent](100, OverflowStrategy.dropHead)
    val sourceQueueOfStatuses: (SourceQueueWithComplete[ProcessEvent], Source[ProcessEvent, NotUsed]) =
      initialSourceOfStatuses.preMaterialize()
    sourceQueueOfStatusesPerSession.update(sessionId, sourceQueueOfStatuses._1)
    sourceOfStatusesPerSession.update(sessionId, sourceQueueOfStatuses._2)

    // watch for closing the source queue (probably because of being inactive) and at that point remove it
    // and its consumer source from the maps
    sourceQueueOfStatuses._1.watchCompletion().foreach { _ =>
      sourceQueueOfStatusesPerSession.remove(sessionId)
        // .foreach(_ => println(s"Source queue with session id $sessionId removed from sourceQueueOfStatusesPerSession"))
      sourceOfStatusesPerSession.remove(sessionId)
        // .foreach(_ => println(s"Source with session id $sessionId removed from sourceOfStatusesPerSession"))
    }
    
    sourceOfAlivePerSession += sessionId
    sourceQueueOfStatuses._2
  }

  def registerNewStatus(status: ProcessEvent, sessionId: UUID): Future[Unit] = {
    val runningSourceQueueOfStatusesO: Option[SourceQueueWithComplete[ProcessEvent]] =
      sourceQueueOfStatusesPerSession.get(sessionId.toString).orElse {
        createSourceOfStatuses(sessionId.toString)
        sourceQueueOfStatusesPerSession.get(sessionId.toString)
      }

    runningSourceQueueOfStatusesO match {
      case Some(runningSourceQueueOfStatuses) =>
        // adding a status through the method `offer()` triggers passing the status to `sourceOfStatuses`, from which
        // the consumer is consuming the statuses
        runningSourceQueueOfStatuses.offer(status)
          .flatMap {
            case Enqueued =>
              Future.successful(())
            case Dropped =>
              sourceOfAlivePerSession -= sessionId.toString()
              Future.failed(new Exception(s"Status: $status couldn't be published"))
            case Failure(e) =>
              sourceOfAlivePerSession -= sessionId.toString()
              println("Failure(e):" + e.getMessage)
              Future.failed(new Exception(s"Status: $status couldn't be published because of $e"))
            case QueueClosed =>
              sourceOfAlivePerSession -= sessionId.toString()
              println("QueueClosed")
              Future.failed(new Exception(s"Status: $status couldn't be published because the publishing queue was closed"))
          }.recover({
          case t =>
            println(t.getMessage)
        })
      case None => Future.failed(new Exception("Source of statuses was already closed"))
    }

  }

  def sendAlive() = {
    // loop infinito de keep-alive mientras llega el fin
    if (notsending) {
      println("Iniciando Thread keep alive")
      val thread = new Thread {
            override def run {
              while (true) {
                sourceOfAlivePerSession.foreach { sessionId => 
                  if (sessionId != "undefined") {
                    ProcessingProgressTrackerEvent.registerNewStatus(new ProcessEvent("0", "0", "0", "aliveEvent"), UUID.fromString(sessionId))
                  }
                }
                Thread.sleep(30000)
              }
            }
      }
      notsending = false
      thread.start()
    }
  }
}
