package com.github.arekolek.phone

import android.telecom.Call
import android.telecom.VideoProfile
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.util.*

object OngoingCall {
    val state: BehaviorSubject<Int> = BehaviorSubject.create()

    private val callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, newState: Int) {
            Timber.d(call.toString())
            state.onNext(newState)
        }
    }

    var call: Call? = null
        set(value) {
            field?.unregisterCallback(callback)
            value?.let {
                it.registerCallback(callback)
                state.onNext(it.state)
            }
            field = value
        }

    fun answer() {
        call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun hangup() {
        call!!.disconnect()
    }
    fun playDtmf( num:Char){
        call!!.playDtmfTone(num);
        launch {
            delay(2000)
            stopDtmf();
            //    println("Hello from Kotlin Coroutines!")
        }
        //stopDtmf();

    }
    fun stopDtmf(){
        call!!.stopDtmfTone();
    }
}
