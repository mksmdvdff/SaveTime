package ru.mksm.savetime.interactors

import android.content.Context
import android.preference.PreferenceManager
import ru.mksm.savetime.util.PrefStringDelegate

/**
 * Created by mac on 13.04.17.
 */
class RegistrationInteractor(val context : Context) {
    var number by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "phone_number", "")

    var name by PrefStringDelegate(PreferenceManager.getDefaultSharedPreferences(context),
            "user_name", "")
}
