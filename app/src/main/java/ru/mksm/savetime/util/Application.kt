package ru.mksm.savetime.util

import android.app.Application
import android.content.Context
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers


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
        var wasShown = false
        Locator.internetInteractor
                .observeInternet()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!it) {
                        if (!wasShown) {
                            wasShown = true
                            Toast.makeText(this, "Сеть недоступна. Работаю в оффлайн режиме", Toast.LENGTH_LONG)
                                    .show()
                        }
                    } else {
                        wasShown = false
                    }
                }
    }
}