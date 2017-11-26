package ru.mksm.savetime.repository

import io.reactivex.Observable
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage

/**
 * Created by mac on 02.04.17.
 */
interface OrdersRepository : Repository<Order> {
    fun getOrdersByType(vararg stages: OrderStage) : Observable<Iterable<Order>>
}