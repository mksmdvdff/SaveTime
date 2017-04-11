package ru.mksm.savetime.repository

import ru.mksm.savetime.model.Order

/**
 * Created by mac on 08.04.17.
 */
open class MapRepository<T : Entity>() : Repository<T> {

    open val map = HashMap<String, T>()

    override fun getAll(): Collection<T> = map.values

    override fun get(id: String) = map[id]

    override fun addOrUpdate(entity: T) = map.put(entity.getId(), entity)!!.getId()

    override fun remove(id: String): T? = map.remove(id)

    override fun clear() = map.clear()
}
