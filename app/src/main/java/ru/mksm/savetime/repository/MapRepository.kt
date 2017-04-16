package ru.mksm.savetime.repository

import io.reactivex.subjects.BehaviorSubject
import ru.mksm.savetime.model.Order

/**
 * Created by mac on 08.04.17.
 */
open class MapRepository<T : Entity>() : Repository<T> {
    var subject : BehaviorSubject<Collection<T>> = BehaviorSubject.create()
    val map = HashMap<String, T>()


    override fun getAll(): Collection<T> = map.values

    override fun get(id: String) = map[id]

    override fun addOrUpdate(entity: T) : String {
        map.put(entity.getId(), entity)
        subject.onNext(map.values)
        return entity.getId()
    }

    override fun remove(id: String): T? {
        val result = map.remove(id)
        subject.onNext(map.values)
        return result
    }

    override fun clear() {
        map.clear()
        subject.onNext(map.values)
    }
}
