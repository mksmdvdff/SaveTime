package ru.mksm.savetime.model

import com.google.gson.annotations.SerializedName
import ru.mksm.savetime.repository.Entity

/**
 * Created by mac on 26.11.17.
 */
data class OrderType(@SerializedName("id") val _id : Int,
                @SerializedName("name") val name: String) : Entity() {
    override fun getId() = _id.toString()
}