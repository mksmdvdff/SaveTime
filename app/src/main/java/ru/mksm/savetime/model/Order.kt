package ru.mksm.savetime.model

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

enum class OrderType {
    NotPayed,
    Payed,
    Cooking,
    Cooked,
    Done
}