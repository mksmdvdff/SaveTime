package ru.mksm.savetime.net

import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import ru.mksm.savetime.interactors.AccountInteractor
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.model.Promocode
import ru.mksm.savetime.net.data.OrderCreate
import ru.mksm.savetime.net.data.OrderDishResponce
import ru.mksm.savetime.net.data.OrderUpdate
import ru.mksm.savetime.util.Locator
import ru.mksm.savetime.util.PrefStringDelegate

/**
 * Created by mac on 17.04.17.
 */

class SaveTimeApiHelper(val context: Context,
                        val saveTimeApi: SaveTimeApi,
                        val accountInteractor: AccountInteractor) {

    private fun phone() = Locator.getCorrectNumber(accountInteractor.number)

    fun sendPhone(number : String) = Single.fromCallable {
        Log.d("MKSM send phone", "phone =" + phone())
        val body = saveTimeApi.createAccount(mapOf(SaveTimeApi.PHONE to
                Locator.getCorrectNumber(number))).execute().body()
        return@fromCallable body
    }

    fun getSessionId(activationCode: String) = Observable.fromCallable {
        val body = saveTimeApi.getAccessToken(mapOf(SaveTimeApi.PHONE to phone(),
                SaveTimeApi.TWO_FACTOR to activationCode)).execute().body()
        return@fromCallable body
    }

    fun getDishes(): Single<List<Dish>> {
        return Single.fromCallable {
            return@fromCallable saveTimeApi.getDishes(
                       accountInteractor.token,
                        accountInteractor.id).execute().body().flatMap {
                listOf(*it.dishes ?: emptyArray<Dish>()) }
        }
    }

    fun getOrderTypes(): Single<List<OrderType>> {
        return Single.fromCallable {
            return@fromCallable saveTimeApi.getOrderTypes(accountInteractor.token).
                    execute().body().toList()
        }
    }

    fun getPromocodes(): Single<List<Promocode>> {
        return Single.fromCallable {
            return@fromCallable saveTimeApi.getPromocodes(accountInteractor.provider,
                    accountInteractor.token).
                    execute().body().toList()
        }
    }

    fun getOrders(): Single<List<Order>> {
        return Single.fromCallable {
            return@fromCallable saveTimeApi.getOrders(
                    accountInteractor.token,
                    accountInteractor.id).execute().body().mapNotNull { it.toOrder() }
        }
    }

    fun updateOrder(order: Order) {
        Thread(Runnable {
            try {
                val body = saveTimeApi.updateOrder(
                        accountInteractor.token, Gson().toJson(OrderUpdate(order.id,
                        order.stage.toServer))).execute()
                body.isSuccessful
            } catch (ex: Exception) {
                Log.e("MKSM", "updateOrder", ex)

            }
        }).start()
    }

//    fun stopOrders(value: Boolean) {
//        Thread(Runnable {
//            try {
//                val body = saveTimeApi.changeOrders("",
//                        "",
//                        accountInteractor.id,
//                        deviceId,
//                        if (value) 1 else 0).clone().execute()
//            } catch (ex: Exception) {
//                stopOrders(value)
//                Log.e("MKSM", "updateOrder", ex)
//
//            }
//        }).start()
//
//    }

    fun createOrder(order: Order, payCash : Boolean) {
        Thread(Runnable {
            try {
                val call = saveTimeApi.createOrder(
                        Locator.accountInteractor.token,
                        OrderCreate(order.comment ?: "",
                                goods = order.desc.map { OrderDishResponce(it.key._id, it.value) }.toTypedArray(),
                                phone = order.phone ?: "",
                                cash = payCash,
                                providerId = Locator.accountInteractor.provider.toInt()))
                val body = call.execute().body()
                val newId = body.id!!
                var currOrder = Locator.ordersInteractor.repo.remove(order.getId())
                if (currOrder != null) {
                    Locator.ordersInteractor.changeOrder(Order(newId,
                            currOrder.stage,
                            order.time,
                            order.date,
                            order.phone,
                            order.comment,
                            order.orderTypeId,
                            order.promocodes,
                            order.guestsCount,
                            order.desc), true)
                } else {
                    Locator.ordersInteractor.changeOrder(Order(newId,
                            order.stage,
                            order.time,
                            order.date,
                            order.phone,
                            order.comment,
                            order.orderTypeId,
                            order.promocodes,
                            order.guestsCount,
                            order.desc), true)
                }
            } catch (ex: Exception) {
                if (Locator.ordersInteractor.getOrder(order.getId()) == null) {
                    Locator.ordersInteractor.changeOrder(order, false)
                }
                Log.e("MKSM", "createOrder", ex)
                Thread.sleep(1000 * 60)
                createOrder(order, payCash)
            }
        }).start()
    }
}
