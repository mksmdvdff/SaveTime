package ru.mksm.savetime.util

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


/**
 * Created by mac on 15.04.17.
 */
class CustomViewPager(context: Context, attrs : AttributeSet) : ViewPager(context, attrs) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }

}