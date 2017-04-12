package ru.mksm.savetime.util

import android.app.Application
import android.content.Context

/**
 * Created by mac on 01.04.17.
 */
 class Application : Application() {
    companion object {
        var context : Context? = null
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}