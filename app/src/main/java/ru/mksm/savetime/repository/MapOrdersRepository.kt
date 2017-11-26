package ru.mksm.savetime.repository

import io.reactivex.Observable
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.util.Locator


/**
 * Created by mac on 02.04.17.
 */
class MapOrdersRepository : OrdersRepository, MapRepository<Order>() {


    override fun getOrdersByType(vararg stages: OrderStage): Observable<Iterable<Order>> {
        return subject.map { it.filter { it.stage in stages } }
    }

    override fun addAll(entities: Collection<Order>) {
        for (entity in entities) {
            Locator.ordersInteractor.notifyAboutNewOrder(entity)
            var currValue = map[entity.getId()]
            if (currValue != null && currValue.stage.ordinal > entity.stage.ordinal) {
                updateOnServer(currValue)
            } else {
                val comment = map[entity.getId()]?.comment ?: entity.comment
                val state = if (map[entity.getId()]?.stage == OrderStage.Cooking) OrderStage.Cooking else entity.stage
                var newOrder = Order(entity.id,
                        state,
                        entity.time,
                        entity.date,
                        entity.phone,
                        comment,
                        entity.orderTypeId,
                        entity.promocodes,
                        entity.guestsCount,
                        entity.desc)
                map[entity.getId()] = newOrder
            }
        }
        subject.onNext(map.values)
    }

    fun addOrUpdate(entity: Order, toServer: Boolean): String {
        map.put(entity.getId(), entity)
        subject.onNext(map.values)
        if (toServer) {
            Locator.apiHelper.updateOrder(entity)
        }
        return entity.getId()
    }

    private fun updateOnServer(order: Order) {
        Locator.apiHelper.updateOrder(order);
    }
}
