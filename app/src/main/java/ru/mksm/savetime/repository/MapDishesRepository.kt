package ru.mksm.savetime.repository

import ru.mksm.savetime.model.Dish

/**
 * Created by mac on 08.04.17.
 */
class MapDishesRepository : MapRepository<Dish>() {
    override val map = hashMapOf("1" to Dish("1", "Лаваш по грузински", 120f),
            "2" to Dish("2", "Запеканка яичная", 250f),
            "3" to Dish("3", "Запеканка яичная, с длинным текстом, чтобы посмотреть", 250f),
            "4" to Dish("4", "Запеканка яичная", 250f),
            "5" to Dish("5", "Запеканка яичная", 250f),
            "6" to Dish("6", "Запеканка яичная", 250f),
            "7" to Dish("7", "Запеканка яичная", 250f),
            "8" to Dish("8", "Запеканка яичная", 250f))
}