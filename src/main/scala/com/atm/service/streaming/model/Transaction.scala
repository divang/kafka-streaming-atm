package com.atm.service.streaming.model

object TRANSACTION_TYPES extends Enumeration {
  type TRANSACTION_TYPE = Value
  val CREDIT, DEBIT = Value
}

/**
  * It represents the transaction message which comes as a value in topic topic.
  * @param accountNo
  * @param accountHolder
  * @param transactionType
  * @param amount
  * @param timeInMillis
  */
case class Transaction(
    accountNo: String,
    accountHolder: String,
    transactionType: TRANSACTION_TYPES.TRANSACTION_TYPE,
    amount: Double,
    timeInMillis: Long
)
