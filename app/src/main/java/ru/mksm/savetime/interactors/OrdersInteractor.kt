package ru.mksm.savetime.interactors

import io.reactivex.Observable
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.repository.OrdersRepository
import ru.mksm.savetime.repository.Repository

/**
 * Created by mac on 01.04.17.
 */

class OrdersInteractor(val repo : OrdersRepository) {

    fun observeOrders(vararg types : OrderType) : Observable<List<Order>> {
        return repo.getOrdersByType(*types)
    }

    fun getOrder(id : String) : Order? = repo.get(id)

}