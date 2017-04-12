package ru.mksm.savetime.interactors

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.mksm.savetime.util.CachedPrefDelegate

/**
 * Created by mac on 11.04.17.
 */

class StopInteractor (val context : Context) {
    var stopped by CachedPrefDelegate(PreferenceManager.getDefaultSharedPreferences(context), "is_stoped", false)
}
