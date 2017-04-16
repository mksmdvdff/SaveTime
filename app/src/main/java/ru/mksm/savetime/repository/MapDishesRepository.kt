package ru.mksm.savetime.repository

import ru.mksm.savetime.model.Dish

/**
 * Created by mac on 08.04.17.
 */
class MapDishesRepository : MapRepository<Dish>() {
    init {
        addOrUpdate(Dish("1", "Лаваш по грузински", 120f))
        addOrUpdate(Dish("2", "Запеканка яичная", 250f))
        addOrUpdate(Dish("3", "Запеканка яичная, с длинным текстом, чтобы посмотреть", 250f))
        addOrUpdate(Dish("4", "Запеканка яичная", 250f))
        addOrUpdate(Dish("5", "Запеканка яичная", 250f))
        addOrUpdate(Dish("6", "Запеканка яичная", 250f))
        addOrUpdate(Dish("7", "Запеканка яичная", 250f))
        addOrUpdate(Dish("8", "Запеканка яичная", 250f))
    }

}