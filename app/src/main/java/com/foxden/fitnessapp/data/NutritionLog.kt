package com.foxden.fitnessapp.data

class NutritionLog {
    var id: Int = 0
    var type: Int = 0
    var time: Int = 0
    var calories: Int = 0
}

enum class NutritionType {
    BREAKFAST, LUNCH, DINNER, SNACK
}