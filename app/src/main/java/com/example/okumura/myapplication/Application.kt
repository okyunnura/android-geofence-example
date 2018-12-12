package com.example.okumura.myapplication

import android.app.Application

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        BackgroundService.start(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        BackgroundService.stop(this)
    }

}