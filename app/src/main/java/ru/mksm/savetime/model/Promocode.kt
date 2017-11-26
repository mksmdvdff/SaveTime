package ru.mksm.savetime.model

import com.google.gson.annotations.SerializedName
import ru.mksm.savetime.repository.Entity

/**
 * Created by mac on 26.11.17.
 */
data class Promocode(@SerializedName("id") val _id : Int,
                     @SerializedName("code") val name: String,
                     @SerializedName("description") val descr: String) : Entity() {
    override fun getId() = _id.toString()
}