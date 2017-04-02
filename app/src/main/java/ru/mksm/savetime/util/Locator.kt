package ru.mksm.savetime.util

import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.repository.MapOrdersRepository

/**
 * Created by mac on 01.04.17.
 */
object Locator {
    val mOrdersInteractor : OrdersInteractor by lazy {
        OrdersInteractor(MapOrdersRepository())
    }
}