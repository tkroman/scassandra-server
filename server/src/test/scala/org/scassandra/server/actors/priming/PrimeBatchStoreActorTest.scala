package org.scassandra.server.actors.priming

import akka.Done
import akka.actor.Props
import akka.testkit.ImplicitSender
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import org.scassandra.codec.Consistency
import org.scassandra.codec.Consistency._
import org.scassandra.codec.messages.BatchQueryKind._
import org.scassandra.codec.messages.BatchType
import org.scassandra.codec.messages.BatchType._
import org.scassandra.server.actors.Activity.{ BatchExecution, BatchQuery }
import org.scassandra.server.actors.TestKitWithShutdown
import org.scassandra.server.actors.priming.PrimeBatchStoreActor._
import org.scassandra.server.actors.priming.PrimeQueryStoreActor.Then
import org.scassandra.server.priming.json.Success

class PrimeBatchStoreActorTest extends WordSpec with TestKitWithShutdown with ImplicitSender with ScalaFutures with Matchers {
  val primeRequest = BatchPrimeSingle(BatchWhen(List(BatchQueryPrime("select * blah", Simple)), consistency = Some(List(ONE))), Then(result = Some(Success)))
  private val matchingQueries = Seq(BatchQuery("select * blah", Simple))
  val matchingExecution = BatchExecution(matchingQueries, ONE, Some(Consistency.SERIAL), LOGGED, None)

  "a prime batch store" must {
    val batchStore = system.actorOf(Props[PrimeBatchStoreActor])
    batchStore ! RecordBatchPrime(primeRequest)

    "match single batches" in {
      batchStore ! MatchBatch(matchingExecution)
      expectMsg(MatchResult(Some(primeRequest.prime)))
    }

    "not match if queries are different" in {
      val differentQueries = matchingQueries.map(bq => bq.copy(query = "Different"))
      batchStore ! MatchBatch(matchingExecution.copy(batchQueries = differentQueries))
      expectMsg(MatchResult(None))
    }

    "not match if consistency is different" in {
      batchStore ! MatchBatch(matchingExecution.copy(consistency = Consistency.ALL))
      expectMsg(MatchResult(None))
    }

    "not match if batch type is different" in {
      batchStore ! MatchBatch(matchingExecution.copy(batchType = BatchType.COUNTER))
      expectMsg(MatchResult(None))
    }

    "allow clearing" in {
      batchStore ! ClearPrimes
      expectMsg(Done)

      batchStore ! MatchBatch(matchingExecution)
      expectMsg(MatchResult(None))
    }
  }
}
