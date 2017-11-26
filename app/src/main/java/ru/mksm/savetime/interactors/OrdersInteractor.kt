package ru.mksm.savetime.interactors

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.provider.Settings
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.R
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.repository.MapOrdersRepository
import ru.mksm.savetime.util.Locator
import ru.mksm.savetime.util.UpdateService
import ru.mksm.savetime.view.activity.OrderActivity


/**
 * Created by mac on 01.04.17.
 */

class OrdersInteractor(val repo: MapOrdersRepository) {

    private val ORDERS = "orders_interactor"
    private val notifiedOrders = getOrders()
    private var serviceEnabled = false

    private fun addOrder(order: Order) {
        notifiedOrders.add(order.getId())
        PreferenceManager.getDefaultSharedPreferences(Locator.context).edit().putString(ORDERS, Gson().toJson(notifiedOrders.toTypedArray())).apply()
    }

    private fun getOrders(): HashSet<String> {
        val json = Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(Locator.context).getString(ORDERS, ""),
                Array<String>::class.java)
        if (json != null) {
            return HashSet(json.toSet())
        } else {
            return HashSet()
        }
    }

    fun notifyAboutNewOrder(order: Order) {
        var previousOrder = getOrder(order.getId())
        if (previousOrder?.stage == OrderStage.Payed) {
            return
        }
        if (order.stage == OrderStage.Payed && !notifiedOrders.contains(order.getId())) {

            var notif: Notification = Notification.Builder(Locator.context)
                    .setContentTitle("SaveTime")
                    .setContentText("Новый заказ")
                    .setSmallIcon(R.drawable.icon_notification)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(LongArray(2).also { it[0] = 1000; it[1] = 1000 })
                    .setContentIntent(PendingIntent.
                            getActivity(Locator.context, order.id, OrderActivity.getIntent(Locator.context, order), 0))
                    .setAutoCancel(true)
                    .build()
            val mNotifyMgr = Locator.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.cancel(1)
            mNotifyMgr.notify(1, notif)
            addOrder(order)
        }
    }

    fun observeOrders(vararg stages: OrderStage): Observable<Iterable<Order>> {
        return repo.getOrdersByType(*stages)
    }

    fun getOrder(id: String): Order? = repo.get(id)

    fun changeOrder(order: Order, toServer: Boolean) {
        repo.addOrUpdate(order, toServer)
    }

    fun requestOrders(): Observable<Iterable<Order>> {
        startScheduler()
        Locator.apiHelper.getOrders()
                .subscribeOn(Schedulers.computation())
                .retry(3)
                .subscribe({
                    repo.subject.onNext(it)
                    repo.addAll(it)
                }, {
                    it.printStackTrace()
                })

        return repo.subject
    }

    fun startScheduler() {
        serviceEnabled = true
        Locator.context.startService(Intent(Locator.context, UpdateService::class.java))
    }

    fun IsServiceStarted() = serviceEnabled


    fun stopScheduler() {
        serviceEnabled = false
        Locator.internetInteractor.cancelNotif()
        Locator.context.startService(UpdateService.getStopIntent(Locator.context))
    }

}