package ru.mksm.savetime.util

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.R
import ru.mksm.savetime.repository.MapOrdersRepository
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


/**
 * Created by mac on 03.05.17.
 */
class UpdateService : Service() {

    var executor: ScheduledExecutorService? = null
    var future: ScheduledFuture<*>? = null
    val repo: MapOrdersRepository = Locator.ordersInteractor.repo
    var enabled by
        PrefBooleanDelegate(PreferenceManager.getDefaultSharedPreferences(Locator.context), SERVICE_ENABLED,
                false)

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        var STOP = "updateServiceStop"
        var SERVICE_ENABLED = "service_enabled"

        fun getStopIntent(context : Context) =
            Intent(STOP, null, context, UpdateService::class.java)
    }

    override fun onCreate() {
        super.onCreate()

        val builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_chat_bubble_black_24dp)
                .setContentTitle("SaveTime")
                .setContentText("Режим кассы")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            builder.addAction(Notification.Action.Builder(R.drawable.ic_close, "Завершить",
                    PendingIntent.getService(this, 0, getStopIntent(applicationContext), 0)).build())
        }
        val notification = builder.build()
        startForeground(777, notification)
        executor = Executors.newScheduledThreadPool(2)
        future = executor!!.scheduleAtFixedRate(getRunnable(), 15, 15, TimeUnit.SECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP) {
            stopSelf()
            enabled = false
            executor?.shutdown()
            future?.cancel(false)
            return START_NOT_STICKY
        } else {
            enabled = true
            return START_STICKY
        }

    }

    private fun getRunnable(): Runnable {
        return Runnable {
            Locator.apiHelper.getOrders()
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        Locator.internetInteractor.setInternet(true)
                        repo.subject.onNext(it)
                        repo.addAll(it)
                    }, {
                        Locator.internetInteractor.setInternet(false)
                        Log.e("MKSM", "orders", it)
                    })
        }

    }



}
