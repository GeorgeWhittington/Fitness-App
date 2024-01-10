package com.foxden.fitnessapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.data.NutritionLog
import com.foxden.fitnessapp.data.NutritionType
import java.time.LocalDate
import java.time.LocalTime

val FOX_IMAGES = listOf<Int>(
    R.drawable.fox_angry,
    R.drawable.fox_sad,
    R.drawable.fox_neutral,
    R.drawable.fox_happy,
)

val RACOON_IMAGES = listOf<Int>(
    R.drawable.angryracoon,
    R.drawable.sadracoon,
    R.drawable.racoon,
    R.drawable.happyracoon,
)

val CAT_IMAGES = listOf<Int>(
    R.drawable.hendrix_window,
)

enum class EvaluationMessage(var completeText : String, var uncompleteText: String, var approval: Int) {
    NONE("...", "...", 0),
    GOAL("Congratulations on completing your goal!","", 10),
    ACTIVE("Nice activity today!","Try to get some activities in today", 10),
    NUTRITION_LOGGED_TODAY("Good job logging all of your meals!","No meals logged today", 10),
    NUTRITION_LOGGED_BREAKFAST("Yummy breakfast today!", "Nothing Logged For Breakfast", 3),
    NUTRITION_LOGGED_LUNCH("Lunch!!", "Nothing Logged For Lunch", 3),
    NUTRITION_LOGGED_DINNER("Dinner Dinner", "Nothing Logged For Dinner", 3),
    NUTRITION_CALORIE_GOAL("Calories under budget! Congratulations", "Too many calories today, try again tomorrow :3", 8),
}

data class Evaluation (
    var image: Int = R.drawable.fox_happy,
    var message: String = "...",
    var messagePositive: String = "...",
    var messageNegative: String = "...",
    var approval: Int = 0
)

@RequiresApi(Build.VERSION_CODES.O)
fun Evaluate(totalActivites: Int, caloriesEnabled: Boolean, calorieGoal: Int, nutritionLogList: List<NutritionLog>, character: String) : Evaluation {

    val approvalMin: Int = -10
    val approvalMax: Int = 10

    var ret = Evaluation();

    val evaluations = mutableListOf<Pair<EvaluationMessage, Boolean>>()

    val currentHour = LocalTime.now().hour

    // Activities
    if (totalActivites > 0) {
        evaluations.add(Pair(EvaluationMessage.ACTIVE, true))
    } else if (totalActivites == 0 && currentHour >= 20) {
        evaluations.add(Pair(EvaluationMessage.ACTIVE, false))
    }

    // Nutrition
    val nutritionToday = nutritionLogList.filter { it.date == LocalDate.now() }

    //  nothing logged for today
    if (caloriesEnabled && nutritionToday.isEmpty()) {
        evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_TODAY, false))
    } else if (caloriesEnabled) {
        var breakfastCount = nutritionToday.filter { it.type == NutritionType.BREAKFAST }.size
        var lunchCount = nutritionToday.filter { it.type == NutritionType.LUNCH }.size
        var dinnerCount = nutritionToday.filter { it.type == NutritionType.DINNER }.size

        if (breakfastCount > 0) { evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_BREAKFAST, true)) }
        if (lunchCount > 0) { evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_LUNCH, true)) }
        if (dinnerCount > 0) { evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_DINNER, true)) }


        if (breakfastCount == 0 && currentHour >= 10) { evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_BREAKFAST, false)) }
        if (lunchCount == 0 && currentHour >= 15) { evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_LUNCH, false)) }
        if (breakfastCount == 0 && currentHour >= 20) { evaluations.add(Pair(EvaluationMessage.NUTRITION_LOGGED_DINNER, false)) }

        if (breakfastCount > 0 && lunchCount > 0 && dinnerCount > 0 && currentHour > 20) {
            var totalCalories = nutritionToday.sumOf { it.calories }
            evaluations.add(Pair(EvaluationMessage.NUTRITION_CALORIE_GOAL, totalCalories <= calorieGoal))
        }
    }


    var approval: Int = 0

    evaluations.sortBy { it.second }
    evaluations.sortBy { it.first.approval }

    evaluations.forEach{
        if (it.second) { approval += it.first.approval }
        else {
            approval += -it.first.approval
        }
    }

    if (approval < -10) { approval = -10 }
    if (approval > 10) { approval = 10 }

    if (evaluations.size > 0) {
        if (evaluations[0].second) {
            ret.message = evaluations[0].first.completeText
        } else {
            ret.message = evaluations[0].first.uncompleteText
            }

        val positiveEvaluations = evaluations.filter { it.second == true }
        val negativeEvaluations = evaluations.filter { it.second == false }

        if (positiveEvaluations.size > 0) {
            ret.messagePositive = positiveEvaluations.sortedBy { it.first.approval }.last().first.completeText
        }

        if (negativeEvaluations.size > 0) {
            ret.messageNegative = negativeEvaluations.sortedBy { it.first.approval }.first().first.uncompleteText
        }

    } else {
        ret.message = "..."
        ret.messagePositive = ""
        ret.messageNegative = ""
    }

    var imageList = FOX_IMAGES
    if (character == "Racoon") { imageList = RACOON_IMAGES  }
    else if (character == "Cat") { imageList = CAT_IMAGES }

    var listSize = imageList.size
    val imageId: Int = ((listSize.toFloat() / (approvalMax.toFloat() - approvalMin.toFloat())) * (approval.toFloat() - approvalMin.toFloat())).toInt()


    ret.approval = approval
    ret.image = imageList[imageId]

    return ret
}