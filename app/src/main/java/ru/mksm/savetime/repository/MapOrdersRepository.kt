package ru.mksm.savetime.repository

import android.util.Log
import android.view.OrientationEventListener
import io.reactivex.Observable
import io.reactivex.Single
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import java.util.*

/**
 * Created by mac on 02.04.17.
 */
class MapOrdersRepository : OrdersRepository, MapRepository<Order>() {
    init {
        addOrUpdate(Order(1, OrderType.Payed, Calendar.getInstance().also { it.add(Calendar.MINUTE, 1) }, 120f, "+79129131307", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2, "Сок" to 5, "Горячее" to 1)))
        addOrUpdate(Order(2, OrderType.Cooked, Calendar.getInstance().also { it.add(Calendar.MINUTE, 2) }, 1330f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 4)))
        addOrUpdate(Order(3, OrderType.Cooking, Calendar.getInstance().also { it.add(Calendar.MINUTE, 2) }, 11f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
        addOrUpdate(Order(4, OrderType.Cooking, Calendar.getInstance().also { it.add(Calendar.MINUTE, 12) }, 200f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2, "Дранники" to 12, "Лечо по деревенски" to 3, "Орехи" to 10, "Пролинне" to 3, "Семечки" to 3, "Жаркое" to 7)))
        addOrUpdate(Order(212, OrderType.Cooking, Calendar.getInstance().also { it.add(Calendar.MINUTE, 30) }, 211f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
        addOrUpdate(Order(207, OrderType.NotPayed, Calendar.getInstance().also { it.add(Calendar.MINUTE, 100) }, 221f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
        addOrUpdate(Order(293, OrderType.Payed, Calendar.getInstance().also { it.add(Calendar.MINUTE, 23) }, 1231f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
        addOrUpdate(Order(294, OrderType.Cooking, Calendar.getInstance(), 2211f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
        addOrUpdate(Order(295, OrderType.Payed, Calendar.getInstance(), 111121f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
        addOrUpdate(Order(297, OrderType.Done, Calendar.getInstance(), 15f, "+7 912 913 13 07", "Какой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарийКакой-то комментарий", mapOf("Суп" to 2)))
    }



    override fun getOrdersByType(vararg types: OrderType) : Observable<Collection<Order>> {
        try {
            return subject.map {
                it.filter {
                    it.type in types }
                        as Collection<Order>}
        } catch (e : Exception) {
            Log.e("mksm", "ex", e)
            return Observable.fromArray()
        }
    }


}
