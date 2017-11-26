package ru.mksm.savetime.util

import android.content.Context
import android.content.Intent

open class ActivityCompanion(val activityClass: Class<out Any>) {

    fun create(context : Context) {
        context.startActivity(getIntent(context))
    }

    fun createWithoutHistory(context: Context) {
        var intent = getIntent(context)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    fun getIntent(context: Context) = Intent(context, activityClass);

}
