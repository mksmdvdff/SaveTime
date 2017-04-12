package ru.mksm.savetime.util

import android.content.SharedPreferences
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by mac on 11.04.17.
 */
class CachedPrefDelegate (val prefs : SharedPreferences, val id : String, val defaultValue : Boolean) : ReadWriteProperty<Any, Boolean> {
    var current : Boolean? = null;
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return current ?: prefs.getBoolean(id, defaultValue);
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        current = value
        prefs.edit().putBoolean(id, value).apply()
    }
}
