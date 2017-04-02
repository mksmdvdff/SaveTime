package ru.mksm.savetime.util

import io.reactivex.Observable

/**
 * Created by mac on 02.04.17.
 */
interface ObservableHolder<T> {

    fun getValue() : T
    fun observe() : Observable<T>
}