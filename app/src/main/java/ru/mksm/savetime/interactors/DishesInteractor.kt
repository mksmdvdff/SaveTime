package ru.mksm.savetime.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.repository.MapRepository
import ru.mksm.savetime.util.Locator

/**
 * Created by mac on 08.04.17.
 */

class DishesInteractor(val repo: MapRepository<Dish>) {
    val currOrder = HashMap<Dish, Int>()
    fun getAllDishes(): Observable<Iterable<Dish>> {
        return repo.subject
    }

//    private fun saveDishes(dishes: Collection<Dish>) {
//        if (dishes.isNotEmpty()) {
//            PreferenceManager.getDefaultSharedPreferences(Locator.context).edit().putString(DISHES, Gson().toJson(dishes.toTypedArray())).apply()
//        }
//    }
//
//    private fun getDishes(): Collection<Dish> {
//        return Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(Locator.context).getString(DISHES, ""),
//                Array<Dish>::class.java)?.toList() ?: emptySet()
//    }


    fun requestDishes(): Observable<Iterable<Dish>> {
        Locator.apiHelper.getDishes()
                .subscribeOn(Schedulers.computation())
                .retry(3)
                .subscribe({
                    repo.subject.onNext(it)
                    repo.addAll(it)
                }, {
                    it.printStackTrace()
                })

        return repo.subject
    }
}


