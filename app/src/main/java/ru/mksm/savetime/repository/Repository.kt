package ru.mksm.savetime.repository

/**
 * Created by mac on 01.04.17.
 */
interface Repository<T : Entity> {
    fun get (id : String) : T?
    fun getAll() : Collection<T>
    fun addAll(entities : Collection<T>)
    fun remove (id : String) : T?
    fun clear()

}