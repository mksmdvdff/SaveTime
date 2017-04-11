package ru.mksm.savetime.util

import ru.mksm.savetime.interactors.DishesInteractor
import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.repository.MapDishesRepository
import ru.mksm.savetime.repository.MapOrdersRepository

/**
 * Created by mac on 01.04.17.
 */
object Locator {
    val ordersInteractor : OrdersInteractor by lazy {
        OrdersInteractor(MapOrdersRepository())
    }

    val dishesInteractor : DishesInteractor by  lazy {
        DishesInteractor(MapDishesRepository())
    }
}