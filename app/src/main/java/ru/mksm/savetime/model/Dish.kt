package ru.mksm.savetime.model

import ru.mksm.savetime.repository.Entity

/**
 * Created by mac on 05.04.17.
 */
data class Dish(val _id : String,
                val name : String,
                val prise : Float) : Entity {
    override fun getId() = _id
}