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
                  val phone : String,
                  val comment : String,
                  val desc : Map<String, Int>) : Entity, Comparable<Order> {

    override fun compareTo(other: Order): Int {
        if (type.priority == other.type.priority) {
            return other.time.compareTo(time)
        } else {
            return type.priority.compareTo(other.type.priority)
        }

    }

    override fun getId() = id.toString();
}

enum class OrderType(val priority : Int, val headerResId : Int,
                     val idColorResId : Int,
                     val otherColorResid : Int,
                     val nameStringRes : Int?) {
    NotPayed(0, R.color.white, R.color.order_black_text, R.color.order_gray_text, null),
    Payed(1, R.color.lightBlue, R.color.white, R.color.white, R.string.newOrder),
    Cooking(3, R.color.green, R.color.white, R.color.white, R.string.cooking),
    Cooked(2, R.color.white, R.color.order_black_text, R.color.order_gray_text, R.string.cooked_short),
    Done(0, R.color.white, R.color.order_black_text, R.color.order_gray_text, null),
    Old(0, R.color.white, R.color.order_black_text, R.color.order_gray_text, null)
}