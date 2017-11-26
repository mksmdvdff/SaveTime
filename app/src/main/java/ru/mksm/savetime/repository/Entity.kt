package ru.mksm.savetime.repository

/**
 * Created by mac on 01.04.17.
 */
abstract class Entity : Any() {
    abstract fun getId() : String
}
