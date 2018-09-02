package com.github.arekolek.phone


class PlayDTMF{

    public  fun startPlayDTMF(playChar :Char){
        OngoingCall.playDtmf(playChar)
    }
}