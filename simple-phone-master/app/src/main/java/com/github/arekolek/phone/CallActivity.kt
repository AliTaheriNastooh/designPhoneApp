package com.github.arekolek.phone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_call.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String
    var char1='0';
    var flagme=true;
    var visibilityFloatingPointButton=false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        number = intent.data.schemeSpecificPart

      //  val fab = findViewById(R.id.buttonDtmf) as Button
     //   fab.setOnClickListener {
      //      playDTMF()
       // }
    }
    fun dtmfButton(v:View){
        when (v.id) {
            R.id.button0 ->OngoingCall.playDtmf('0')
            R.id.button1 ->OngoingCall.playDtmf('1')
            R.id.button2 ->OngoingCall.playDtmf('2')
            R.id.button3 ->OngoingCall.playDtmf('3')
            R.id.button4 ->OngoingCall.playDtmf('4')
            R.id.button5 ->OngoingCall.playDtmf('5')
            R.id.button6 ->OngoingCall.playDtmf('6')
            R.id.button7 ->OngoingCall.playDtmf('7')
            R.id.button8 ->OngoingCall.playDtmf('8')
            R.id.button9 ->OngoingCall.playDtmf('9')
            R.id.buttonAsterisk ->OngoingCall.playDtmf('*')
            R.id.buttonHash ->OngoingCall.playDtmf('#')
        }
    }
    fun playDTMF() {
        if (flagme) {
            OngoingCall.playDtmf(char1)
            flagme = false
        } else {
            OngoingCall.stopDtmf()
            flagme = true
        }
        if (char1 == '9') {
            char1 = '0'
        }
        char1++;
        Log.i("adf  -----------", "char1")
    }
    class SingleSchedulePool {

        val executorService = Executors.newScheduledThreadPool(1)
        var future: ScheduledFuture<*>? = null

        fun delay(delay: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, body: (() -> Unit)) {
            future?.cancel(false)

            val runnable = Runnable { body() }

            future = executorService.schedule(runnable, delay, timeUnit)
        }

        fun shutdown() = executorService.shutdown()

    }
    override fun onStart() {
        super.onStart()
        phonNumberShowed.setText(number)
        tableLayout1.visibility= View.INVISIBLE;
        floatingActionButton.setOnClickListener{
            if (visibilityFloatingPointButton){
                tableLayout1.visibility= View.INVISIBLE;
                visibilityFloatingPointButton=false;
            }else{
                tableLayout1.visibility= View.VISIBLE;
                visibilityFloatingPointButton=true;
            }
        }
        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {

            OngoingCall.hangup()
        }

        OngoingCall.state
            .subscribe(::updateUi)
            .addTo(disposables)

        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe { finish() }
            .addTo(disposables)

        val pool = SingleSchedulePool()

        pool.delay(0, TimeUnit.SECONDS) { println("test 1") }
       // Thread.sleep(100)
        pool.delay(2, TimeUnit.SECONDS) { println("test 2") }
        pool.delay(2, TimeUnit.SECONDS) { println("test 3") }
        pool.delay(2, TimeUnit.SECONDS) { println("test 4") }

        pool.shutdown()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
       // callInfo.text = "${state.asString().toLowerCase().capitalize()}\n$number"

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }
}
