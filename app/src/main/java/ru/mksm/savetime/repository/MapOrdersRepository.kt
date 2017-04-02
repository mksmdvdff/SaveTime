package ru.mksm.savetime.repository

import android.view.OrientationEventListener
import io.reactivex.Observable
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import java.util.*

/**
 * Created by mac on 02.04.17.
 */
class MapOrdersRepository : OrdersRepository {

    val map : HashMap<String, Order> = hashMapOf(
            "1" to Order(1, OrderType.Payed, Calendar.getInstance(), 120f, mapOf("Суп" to 2, "Сок" to 5, "Горячее" to 1)),
            "2" to Order(2, OrderType.Cooked, Calendar.getInstance(), 1330f, mapOf("Суп" to 2, "Суп" to 4, "" to 1)),
            "3" to Order(3, OrderType.Cooking, Calendar.getInstance(), 11f, mapOf("Суп" to 2, "" to 1, "" to 3)),
            "4" to Order(4, OrderType.Cooking, Calendar.getInstance(), 200f, mapOf("Суп" to 2, "Дранники" to 12, "Лечо по деревенски" to 3, "Орехи" to 10, "Пролинне" to 3, "Семечки" to 3, "Жаркое" to 7)),
            "212" to Order(212, OrderType.Cooking, Calendar.getInstance(), 211f, mapOf("Суп" to 2)),
            "207" to Order(207, OrderType.NotPayed, Calendar.getInstance(), 221f, mapOf("Суп" to 2)),
            "293" to Order(293, OrderType.Cooking, Calendar.getInstance(), 1231f, mapOf("Суп" to 2)),
            "294" to Order(294, OrderType.Cooking, Calendar.getInstance(), 2211f, mapOf("Суп" to 2)),
            "295" to Order(295, OrderType.Cooking, Calendar.getInstance(), 111121f, mapOf("Суп" to 2)),
            "297" to Order(297, OrderType.Done, Calendar.getInstance(), 15f, mapOf("Суп" to 2)))

    override fun get(id: String) = map[id]

    override fun addOrUpdate(entity: Order) = map.put(entity.getId(), entity)!!.getId()

    override fun remove(id: String): Order? = map.remove(id)

    override fun clear() = map.clear()

    override fun getOrdersByType(vararg types: OrderType) =
        Observable.fromCallable {
            map.values.filter{
                it.type in types
            }
        }!!

}
