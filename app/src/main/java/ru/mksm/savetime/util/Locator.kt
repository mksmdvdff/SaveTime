package ru.mksm.savetime.util

import android.content.Context
import ru.mksm.savetime.interactors.DishesInteractor
import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.interactors.StopInteractor
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

    val stopInteractor : StopInteractor by lazy {
        StopInteractor(Application.context?: throw IllegalStateException())
    }
}