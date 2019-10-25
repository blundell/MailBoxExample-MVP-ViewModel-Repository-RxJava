package com.blundell.tut

data class MailCollection(val listOfMail: List<Mail>) {
    fun total(): Int {
        return listOfMail.size
    }
}
