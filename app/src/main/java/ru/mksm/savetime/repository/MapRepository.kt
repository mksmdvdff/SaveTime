package ru.mksm.savetime.repository

import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * Created by mac on 08.04.17.
 */
open class MapRepository<T : Entity>() : Repository<T> {


    var subject : BehaviorSubject<Iterable<T>> = BehaviorSubject.create()
    val map = TreeMap<String, T>()


    override fun getAll(): Collection<T> = map.values

    override fun get(id: String) = map[id]

    override fun remove(id: String): T? {
        val result = map.remove(id)
        subject.onNext(map.values)
        return result
    }

    override fun clear() {
        map.clear()
        subject.onNext(map.values)
    }

    override fun addAll(entities: Collection<T>) {
        for (entity in entities) {
            map[entity.getId()] = entity
        }
        subject.onNext(map.values)
    }
}
