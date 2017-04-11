package ru.mksm.savetime.interactors

import io.reactivex.Observable
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.repository.Repository

/**
 * Created by mac on 08.04.17.
 */
class DishesInteractor (val repo : Repository<Dish>) {
    val currOrder = HashMap<Dish, Int>()
    fun getAllDishes() = Observable.fromCallable {
        repo.getAll()
    }
}


