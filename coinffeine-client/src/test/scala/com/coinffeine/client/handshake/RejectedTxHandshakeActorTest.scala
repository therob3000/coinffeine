package com.coinffeine.client.handshake

import coinffeine.model.exchange.Both

import scala.concurrent.duration._

import com.coinffeine.client.handshake.HandshakeActor._
import com.coinffeine.common.blockchain.BlockchainActor.TransactionRejected
import com.coinffeine.common.protocol._
import com.coinffeine.common.protocol.messages.arbitration.CommitmentNotification

class RejectedTxHandshakeActorTest extends HandshakeActorTest("rejected-tx") {

  override def protocolConstants = ProtocolConstants(
    commitmentConfirmations = 1,
    resubmitRefundSignatureTimeout = 10 seconds,
    refundSignatureAbortTimeout = 100 millis
  )

  "Handshakes in which TX are rejected" should "have a failed handshake result" in {
    givenActorIsInitialized()
    givenCounterpartPeerHandshake()
    givenValidRefundSignatureResponse()
    gateway.send(actor, fromBroker(CommitmentNotification(
      exchange.id,
      Both(handshake.myDeposit.get.getHash, handshake.counterpartCommitmentTransaction.getHash)
    )))
    blockchain.send(actor, TransactionRejected(handshake.counterpartCommitmentTransaction.getHash))

    val result = listener.expectMsgClass(classOf[HandshakeFailure])
    result.e.toString should include (
      s"transaction ${handshake.counterpartCommitmentTransaction.getHash} (counterpart) was rejected")
  }

  it should "terminate" in {
    listener.expectTerminated(actor)
  }
}
