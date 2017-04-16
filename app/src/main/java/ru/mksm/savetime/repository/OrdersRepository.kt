package ru.mksm.savetime.repository

import io.reactivex.Observable
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.view.activity.MainActivity

/**
 * Created by mac on 02.04.17.
 */
interface OrdersRepository : Repository<Order> {
    fun getOrdersByType(vararg types : OrderType) : Observable<Collection<Order>>
}