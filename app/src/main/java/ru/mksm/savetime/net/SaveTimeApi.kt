package ru.mksm.savetime.net

import retrofit2.Call
import retrofit2.http.*
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.model.Promocode
import ru.mksm.savetime.net.data.*

/**
 * Created by mac on 17.04.17.
 */

interface SaveTimeApi {

    companion object {
        public val PHONE = "phone"
        public val TWO_FACTOR = "twofactor"
    }
    @POST("Clients/requestCode")
    fun createAccount(@Body body : Map<String, @JvmSuppressWildcards Any>) : Call<PhoneResponse>

    @POST("Clients/loginWithCode")
    fun getAccessToken(@Body body : Map<String, @JvmSuppressWildcards Any>) : Call<IdResponse>

    @GET("Orders/userGoods")
    fun  getDishes(@Query("access_token")accessToken : String,
                  @Query("id")id : String) : Call<Array<CategoryResponse>>

    @GET("Orders/userOrders")
    fun getOrders(@Query("access_token")accessToken : String,
                  @Query("id")id : String) : Call<Array<OrderResponse>>

    @GET("OrderTypes")
    fun getOrderTypes(@Query("access_token")accessToken : String):
            Call<Array<OrderType>>

    @GET("Providers/{id}/promocodes")
    fun getPromocodes(@Path("id") id : String,
                      @Query("access_token")accessToken : String):
            Call<Array<Promocode>>

    @GET("Orders/changeStatus")
    fun updateOrder(@Query("access_token")accessToken : String,
                    @Query("credentials")id : String) : Call<Any>

//    @GET(".")
//    fun changeOrders(@Query("action")action : String,
//                    @Query("id_account")id_account : String,
//                    @Query("id_session")sessionId: String,
//                    @Query("id_device")id_device : String,
//                    @Query("value")id_order : Int) : Call<Responce<Dish>>

    @POST("Orders/createOrder")
    fun createOrder(@Query("access_token")accessToken : String,
                    @Body id : OrderCreate) : Call<NewOrderId>
}
