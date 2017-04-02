package ru.mksm.savetime.model

import ru.mksm.savetime.R
import ru.mksm.savetime.repository.Entity
import java.util.*

/**
 * Created by mac on 26.03.17.
 */
data class Order (val id : Int,
                  val type : OrderType,
                  val time : Calendar,
                  val price : Float,
                  val desc : Map<String, Int>) : Entity {
    override fun getId() = id.toString();
}

enum class OrderType(val headerResId : Int, val idColorResId : Int, val otherColorResid : Int) {
    NotPayed(R.color.white, R.color.order_black_text, R.color.order_gray_text),
    Payed(R.color.lightBlue, R.color.white, R.color.white),
    Cooked(R.color.green, R.color.white, R.color.white),
    Cooking(R.color.white, R.color.order_black_text, R.color.order_gray_text),
    Done(R.color.white, R.color.order_black_text, R.color.order_gray_text)
}