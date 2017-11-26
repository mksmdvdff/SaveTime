package ru.mksm.savetime.model

import ru.mksm.savetime.R
import ru.mksm.savetime.net.data.PromocodeId
import ru.mksm.savetime.repository.Entity

/**
 * Created by mac on 26.03.17.
 */
data class Order (val id : Int,
                  val stage: OrderStage,
                  val time : String,
                  val date : String,
                  val phone : String?,
                  val comment : String?,
                  val orderTypeId : Int,
                  val promocodes : Array<PromocodeId>,
                  val guestsCount: Int,
                  val desc : Map<Dish, Int>) : Entity() {

    companion object {
        private var tempId = -1

        public val DEFAULT_TYPE = 2;
        public val DEFAULT_GUEST_COUNT = 1;

        public fun tempId() = --tempId;
    }

    override fun getId() = id.toString();

    fun getSummaryPrice() : Float {
        var sum : Float = 0f;
        for (pair in desc) {
            sum += pair.key.price * pair.value
        }
        return sum;
    }

    fun isTemp() = id < 0
}

enum class OrderStage(val priority : Int, val headerResId : Int,
                      val idColorResId : Int,
                      val otherColorResid : Int,
                      val toServer : Int) {
    NotPayed(0, R.color.white, R.color.order_black_text, R.color.order_gray_text, 0),
    Payed(1, R.color.lightBlue, R.color.white, R.color.white, 1),
    Cooking(3, R.color.green, R.color.white, R.color.white, 2),
    Cooked(2, R.color.white, R.color.order_black_text, R.color.order_gray_text, 3),
    Done(0, R.color.white, R.color.order_black_text, R.color.order_gray_text, 4)
}