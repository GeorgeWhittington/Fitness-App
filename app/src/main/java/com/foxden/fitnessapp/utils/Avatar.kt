package com.foxden.fitnessapp.utils

import com.foxden.fitnessapp.R

enum class EvaluationMessage(var displayText: String) {
    NONE("..."),
    NUTRITION_NOT_LOGGED_TODAY("No meals logged today"),
    NUTRITION_NOT_LOGGED_BREAKFAST("Nothing Logged For Breakfast"),
    NUTRITION_NOT_LOGGED_LUNCH("Nothing Logged For Lunch"),
    NUTRITION_NOT_LOGGED_DINNER("Nothing Logged For Dinner"),
}

data class Evaluation (
    val image: Int = R.drawable.fox_happy,
    val message: EvaluationMessage = EvaluationMessage.NONE
)

fun Evaluate() : Evaluation {

    // 10   - Good
    // 0    - Neutral
    // -10  - Bad
    var approval: Int = 0


    return Evaluation()
}