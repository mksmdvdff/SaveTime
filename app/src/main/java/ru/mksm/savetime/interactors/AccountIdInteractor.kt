package ru.mksm.savetime.interactors

import android.content.Context
import android.preference.PreferenceManager
import ru.mksm.savetime.util.PrefStringDelegate

/**
 * Created by mac on 18.04.17.
 */
class AccountInteractor(context: Context) {
    companion object {
        var ERROR_CODE: String = "-1"
    }

    var number by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "phone_number", "")
        private set

    var id by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "id", "")


    var token by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "token", "")

    var provider by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "provider", "")

    var name by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "user_name", "")

    fun setPhoneNumber(number: String) {
        this.number = number.apply { replace("+7", "8")}
    }

    fun logout() {
        number = ""
        id = ""
        name = ""
        token = ""
        provider = ""
    }
}
