package com.blundell.tut

import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

class MailBoxRepository {

    private val mailbox = mutableListOf<Mail>()
    private val subject: ReplaySubject<MailCollection> = ReplaySubject.create<MailCollection>()

    init {
        for (i in 1..97) {
            mailbox += Mail(1, "Santa", "D roid", "I'm so excited for Christmas #$i")
        }
    }

    fun getCurrentMail(): Observable<MailCollection> {
        return Observable.merge(Observable.just(MailCollection(mailbox)), subject)
    }

    fun sendNewMail(mail: Mail) {
        mailbox += mail
        subject.onNext(MailCollection(mailbox))
    }

    fun decrementMailCount() {
        if (mailbox.isEmpty()) {
            return
        }
        mailbox.removeAt(0)
        subject.onNext(MailCollection(mailbox))
    }

    fun clearMailCount() {
        mailbox.clear()
        subject.onNext(MailCollection(mailbox))
    }
}
