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

    val map : HashMap<String, Order> = hashMapOf("1" to Order(1, OrderType.Payed, Calendar.getInstance(), 120f, mapOf("Суп" to 2)))

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
