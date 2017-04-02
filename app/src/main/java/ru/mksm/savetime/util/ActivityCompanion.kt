package ru.mksm.savetime.util

import android.app.Activity
import android.content.Context
import android.content.Intent

open class ActivityCompanion(val activityClass: Class<out Any>) {

    fun create(context : Context) {
        context.startActivity(getIntent(context))
    }

    fun getIntent(context: Context) = Intent(context, activityClass);

}
