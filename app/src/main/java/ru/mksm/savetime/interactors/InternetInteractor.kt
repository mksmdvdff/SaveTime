package ru.mksm.savetime.interactors

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.mksm.savetime.R
import ru.mksm.savetime.util.Locator
import ru.mksm.savetime.view.activity.MainActivity

/**
 * Created by mac on 06.05.17.
 */

class InternetInteractor(private var context : Context) {

    private var hasInternet = BehaviorSubject.createDefault(true)
    private var hasInternetBoolean = false

    fun observeInternet() : Observable<Boolean> {
        return hasInternet
    }

    fun isInternetConnected() = hasInternetBoolean

    fun setInternet(value : Boolean) {
        val mNotifyMgr = Locator.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        hasInternetBoolean = value
        hasInternet.onNext(value)
        if (!value) {
            var notif: Notification = Notification.Builder(Locator.context)
                    .setContentTitle("SaveTime")
                    .setContentText("Оффлайн режим")
                    .setSmallIcon(R.drawable.ic_chat_bubble_black_24dp)
                    .setContentIntent(PendingIntent.getActivity(context, 0, MainActivity.getIntent(context), 0))
                    .setOngoing(true)
                    .build()

            mNotifyMgr.notify(100, notif)
        } else {
            mNotifyMgr.cancel(100)
        }
    }

    fun cancelNotif() {
        (Locator.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(100)
    }
}
