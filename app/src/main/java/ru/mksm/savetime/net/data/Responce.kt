package ru.mksm.savetime.net.data

import com.google.gson.annotations.SerializedName
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.util.Locator

/**
 * Created by mac on 18.04.17.
 */


data class Error(@SerializedName("statusCode") val errorCode: Int,
                 @SerializedName("name") val name: String,
                 @SerializedName("message") val message: String)

data class PhoneResponse(@SerializedName("result") val result: String?,
                         @SerializedName("error") val error: Error?)

data class IdResponse(@SerializedName("clientId") val clientId: String?,
                      @SerializedName("token") val token: String?,
                      @SerializedName("providerId") val provider: String?,
                      @SerializedName("error") val error: Error?)

data class CategoryResponse(@SerializedName("goods") val dishes: Array<Dish>?)


data class OrderResponse(@SerializedName("id") val id: Int,
                         @SerializedName("phone") val phone: String,
                         @SerializedName("time") val orderTime: String,
                         @SerializedName("date") var orderDate: String,
                         @SerializedName("status") val orderStage: Int,
                         @SerializedName("comment") val comment: String,
                         @SerializedName("promocodes") val promocodes: Array<PromocodeId>,
                         @SerializedName("type") val type: Int,
                         @SerializedName("persons_quantity") val persons: Int,
                         @SerializedName("goods") val dishes: Array<OrderDishResponce>) {
    fun toOrder(): Order? {
        val stage: OrderStage? = OrderStage.values()[orderStage]
        if (stage != null) {
            val map = LinkedHashMap<Dish, Int>()
            dishes.forEach {
                val pair = it.toPair()
                pair.first?.let { it1 -> map.put(it1, pair.second) }
            }
            return Order(id, stage,
                    orderTime,
                    orderDate,
                    phone,
                    comment, type, promocodes, persons,  map)
        } else {
            return null
        }
    }
}

data class OrderTypeResponse(@SerializedName("id") val id : Int,
                             @SerializedName("name") val name : String)

data class OrderDishResponce(@SerializedName("id") val id: Int,
                             @SerializedName("quantity") val quantity: Int) {

    fun toPair() = Pair(Locator.dishesInteractor.repo.get(id.toString()), quantity)
}

public data class PromocodeId(@SerializedName("id") val id : Int)

data class NewOrderId(@SerializedName("id") val id: Int?,
                      @SerializedName("error") val error: Error?)

data class OrderUpdate(@SerializedName("id") val id: Int,
                       @SerializedName("status") val status: Int)

data class OrderCreate(@SerializedName("comment") val comment: String,
                       @SerializedName("type") val status: Int = Order.DEFAULT_TYPE,
                        @SerializedName("persons_quantity") val persons: Int = 1,
                        @SerializedName("goods") val goods : Array<OrderDishResponce>,
                        @SerializedName("cash") val cash : Boolean,
                        @SerializedName("phone") val phone: String,
                        @SerializedName("providerId") val providerId : Int)

