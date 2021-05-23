package com.atm.service.streaming.db
import java.util.concurrent.ConcurrentHashMap

/***
  * It is a mocked database. It also take care of current update. It holds account number and its
  * balance.
  */
class MockedDataBase {

  //Use concurrent hashmap
  private val accountBalance = new ConcurrentHashMap[String, Double]

  @throws[IllegalArgumentException]
  def getBalance(accNo: String): Double = {
    if (accountBalance.containsKey(accNo)) return accountBalance.get(accNo)
    throw new IllegalArgumentException("Account does not exist")
  }

  def credit(accNo: String, amount: Double): Unit = {
    // Hack, if account does not exist add it.
    val balance = accountBalance.getOrDefault(accNo, 0d)
    accountBalance.putIfAbsent(accNo, balance + amount)
  }

  @throws[IllegalArgumentException]
  def debit(accNo: String, amount: Double): Unit = {

    if (accountBalance.containsKey(accNo)) {
      val balance = accountBalance.get(accNo)
      if (balance <= 0)
        new IllegalArgumentException("Account does not have enough balance.")
      // Need to take care Atomic operation on Balance check and update balance
      accountBalance.put(accNo, balance - amount)
    } else
      throw new IllegalArgumentException(
        "Account does not exist. Acc No: " + accNo
      )
  }
}
