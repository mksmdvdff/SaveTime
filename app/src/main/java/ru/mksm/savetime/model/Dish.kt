package ru.mksm.savetime.model

import com.google.gson.annotations.SerializedName
import ru.mksm.savetime.repository.Entity

/**
 * Created by mac on 05.04.17.
 */
data class Dish(@SerializedName("id") val _id : Int,
                @SerializedName("name") val name: String,
                @SerializedName("price") val price : Float) : Entity() {
    override fun getId() = _id.toString()
}