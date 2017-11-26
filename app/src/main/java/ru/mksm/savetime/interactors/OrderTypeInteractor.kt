package ru.mksm.savetime.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.model.Dish
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.repository.MapRepository
import ru.mksm.savetime.util.Locator

/**
 * Created by mac on 26.11.17.
 */

class OrderTypeInteractor(val repo: MapRepository<OrderType>) {


    fun requestOrderTypes(): Observable<Iterable<OrderType>> {
        Locator.apiHelper.getOrderTypes()
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


