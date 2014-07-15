package coinffeine.model.exchange

import coinffeine.model.bitcoin.PublicKey
import coinffeine.model.currency.FiatCurrency

/** Relevant information for an ongoing exchange. This point of view is only held by the parts
  * as contains information not made public to everyone on the network.
  */
trait OngoingExchange[+C <: FiatCurrency] extends Exchange[C] {
  val role: Role

  /** Information about the parts */
  val participants: Both[Exchange.PeerInfo]

  def requiredSignatures: Both[PublicKey] = participants.map(_.bitcoinKey)

  val user = participants(role)
  val counterpart = participants(role.counterpart)

  require(user.bitcoinKey.hasPrivKey)
}
