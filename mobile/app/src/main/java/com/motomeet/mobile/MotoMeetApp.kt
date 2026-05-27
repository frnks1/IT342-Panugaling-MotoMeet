package com.motomeet.mobile

import android.app.Application
import com.motomeet.mobile.data.network.TokenManager

class MotoMeetApp : Application() {
    
    companion object {
        lateinit var instance: MotoMeetApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
