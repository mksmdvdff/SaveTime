package ru.mksm.savetime.interactors

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.model.Promocode
import ru.mksm.savetime.repository.MapRepository
import ru.mksm.savetime.util.Locator

/**
 * Created by mac on 26.11.17.
 */
class PromocodeInteractor(val repo: MapRepository<Promocode>) {


    fun requestPromocodes(): Observable<Iterable<Promocode>> {
        Locator.apiHelper.getPromocodes()
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