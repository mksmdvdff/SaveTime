package ru.mksm.savetime.util

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mksm.savetime.interactors.*
import ru.mksm.savetime.net.SaveTimeApi
import ru.mksm.savetime.net.SaveTimeApiHelper
import ru.mksm.savetime.repository.MapDishesRepository
import ru.mksm.savetime.repository.MapOrderTypeRepository
import ru.mksm.savetime.repository.MapOrdersRepository
import ru.mksm.savetime.repository.MapRepository


/**
 * Created by mac on 01.04.17.
 */
object Locator {

    val context by lazy {
        Application.context?: throw IllegalStateException()
    }
    val ordersInteractor : OrdersInteractor by lazy {
        OrdersInteractor(MapOrdersRepository())
    }

    val dishesInteractor : DishesInteractor by  lazy {
        DishesInteractor(MapDishesRepository())
    }

    val orderTypeInteractor : OrderTypeInteractor by  lazy {
        OrderTypeInteractor(MapOrderTypeRepository())
    }

    val promocodeInteractor : PromocodeInteractor by lazy {
        PromocodeInteractor(MapRepository())
    }

    val stopInteractor : StopInteractor by lazy {
        StopInteractor(context)
    }

    val retrofit : Retrofit by lazy {

        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.e("MKSM", it) })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val gson = GsonBuilder()
                .setLenient()
                .create()
        Retrofit.Builder()
                .baseUrl("https://api.savetime4.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    val api : SaveTimeApi by lazy {
        retrofit.create(SaveTimeApi::class.java)
    }

    val apiHelper : SaveTimeApiHelper by lazy {
        SaveTimeApiHelper(context, api, accountInteractor)
    }

    val accountInteractor : AccountInteractor by lazy {
        AccountInteractor(context)
    }

    val internetInteractor : InternetInteractor by lazy {
        InternetInteractor(context)
    }

    fun getCorrectNumber (number: String) = if (number.startsWith("+")) {
        number.subSequence(1, number.length)
    } else {
        number.replaceFirst("8", "7")
    }
}