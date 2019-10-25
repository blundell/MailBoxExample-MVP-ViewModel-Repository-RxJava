package com.blundell.tut

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(), MailMvp.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val model = ViewModelProvider(this, TutViewModelProviderFactory()).get(MailModel::class.java)
        val presenter = MailPresenter(this, model, this)
        button_send_mail.setOnClickListener {
            presenter.onSendMailClicked()
        }
        button_read_mail.setOnClickListener {
            presenter.onReadMailClicked()
        }
        button_read_all_mail.setOnClickListener {
            presenter.onReadAllMailClicked()
        }
        presenter.onCreateRan()
    }

    override fun getMailToInput(): String {
        return input_to.text.toString()
    }

    override fun showLoadingMail() {
        Log.d("TUT", "showLoadingMail (imaging any type of loading view/animation)")
    }

    override fun hideLoadingMail() {
        Log.d("TUT", "hideLoadingMail")
    }

    override fun showEmptyMailIcon() {
        icon_mailbox.showEmptyMailIcon()
    }

    override fun showGotMailIcon() {
        icon_mailbox.showGotMailIcon()
    }

    override fun showFlamingMailIcon() {
        icon_mailbox.showFlamingMailIcon()
    }

    override fun showMailCount(count: String) {
        icon_mailbox.showMailCount(count)
    }

    override fun hideMailCount() {
        icon_mailbox.hideMailCount()
    }
}

interface MailMvp {
    interface Model {
        fun sendMail(mail: Mail)
        fun getNextMailId(): Int
        fun getCurrentMail(): LiveData<MailCollection>
        fun markSingleMailRead()
        fun markAllMailRead()
    }

    interface View {
        fun getMailToInput(): String
        fun showLoadingMail()
        fun hideLoadingMail()
        fun showEmptyMailIcon()
        fun showGotMailIcon()
        fun showFlamingMailIcon()
        fun showMailCount(count: String)
        fun hideMailCount()
    }

    interface Presenter {
        fun onCreateRan()
        fun onSendMailClicked()
        fun onReadMailClicked()
        fun onReadAllMailClicked()
    }
}

class MailModel(
    val idGenerator: IdGenerator,
    val repository: MailBoxRepository,
    val subscribingScheduler: Scheduler,
    val observingScheduler: Scheduler
) : ViewModel(), MailMvp.Model {

    private var mailCollection: MutableLiveData<MailCollection>? = null

    private var currentMailDisposable: Disposable? = null

    override fun sendMail(mail: Mail) {
        repository.sendNewMail(mail)
    }

    override fun getNextMailId(): Int {
        return idGenerator.nextId()
    }

    override fun getCurrentMail(): LiveData<MailCollection> {
        if (mailCollection == null) {
            mailCollection = MutableLiveData()
            loadCurrentMail()
        }
        return mailCollection!!
    }

    private fun loadCurrentMail() {
        currentMailDisposable = repository.getCurrentMail()
            .subscribeOn(subscribingScheduler)
            .observeOn(observingScheduler)
            .subscribe(
                { result -> mailCollection?.value = result }
//                , { error -> errorEvent.postValue(ErrorEvent("Getting Mail failed.", error)) }
                // This example doesn't deal with errors, however you can handle them by posting an object
                // to another LiveData object that you subscribe to, then the presenter can update the view
                // showing the error
            )
    }

    override fun markSingleMailRead() {
        repository.decrementMailCount()
    }

    override fun markAllMailRead() {
        repository.clearMailCount()
    }

    override fun onCleared() {
        currentMailDisposable?.dispose()
    }
}

class MailPresenter(val view: MailMvp.View, val model: MailMvp.Model, val lifecycleOwner: LifecycleOwner) : MailMvp.Presenter {

    override fun onSendMailClicked() {
        val id = model.getNextMailId()
        val to = view.getMailToInput()
        val from = "D roid"
        val msg = "Dear $to, for Christmas I would like... ${Random.nextInt()}"
        val mail = Mail(id, to, from, msg)
        model.sendMail(mail)
    }

    override fun onReadMailClicked() {
        model.markSingleMailRead()
    }

    override fun onReadAllMailClicked() {
        model.markAllMailRead()
    }

    override fun onCreateRan() {
        view.showLoadingMail()
        model.getCurrentMail().observe(lifecycleOwner, onCurrentMailObserved())
    }

    private fun onCurrentMailObserved() = Observer<MailCollection> { mailCollection ->
        view.hideLoadingMail()
        val totalMail = mailCollection.total()
        when {
            totalMail < 1 -> {
                view.showEmptyMailIcon()
                view.hideMailCount()
            }
            totalMail < 100 -> {
                view.showGotMailIcon()
                view.showMailCount("$totalMail")
            }
            else -> {
                view.showFlamingMailIcon()
                view.showMailCount("99+")
            }
        }
    }
}

class TutViewModelProviderFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            IdGenerator::class.java,
            MailBoxRepository::class.java,
            Scheduler::class.java,
            Scheduler::class.java
        )
            .newInstance(IdGenerator(0), MailBoxRepository(), Schedulers.io(), AndroidSchedulers.mainThread())
    }
}
