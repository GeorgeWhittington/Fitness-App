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

enum class Evaluation(var completeText : String, var uncompleteText: String, var approval: Int) {
    NONE("...", "...", 0),
    GOAL("Congratulations on completing your goal!","", 10),
    ACTIVE("Good job on your activity today!","Try to get active today", 10),
    NUTRITION_LOGGED_TODAY("All meals logged, well done :3","Try to log some meals", 10),
    NUTRITION_LOGGED_BREAKFAST("Well done logging your breakfast!", "I think you forgot to log breakfast", 3),
    NUTRITION_LOGGED_LUNCH("Well done logging your lunch!", "I think you forgot to log lunch!", 3),
    NUTRITION_LOGGED_DINNER("Well done logging your dinner!", "I think it might be dinner time", 3),
    NUTRITION_CALORIE_GOAL("Calories under budget! Congratulations", "You've overshot your calories for today, try again tomorrow :3", 8),
}

data class EvaluationResult (
    var image: Int = R.drawable.fox_happy,
    var message: String = "...",
    var messagePositive: String = "...",
    var messageNegative: String = "...",
    var approval: Int = 0
)

@RequiresApi(Build.VERSION_CODES.O)
fun Evaluate(totalActivites: Int, caloriesEnabled: Boolean, calorieGoal: Int, nutritionLogList: List<NutritionLog>, character: String) : EvaluationResult {

    val approvalMin: Int = -10
    val approvalMax: Int = 10

    var ret = EvaluationResult();

    val evaluations = mutableListOf<Pair<Evaluation, Boolean>>()

    val currentHour = LocalTime.now().hour

    // Activities
    if (totalActivites > 0) {
        evaluations.add(Pair(Evaluation.ACTIVE, true))
    } else if (totalActivites == 0 && currentHour >= 20) {
        evaluations.add(Pair(Evaluation.ACTIVE, false))
    }

    // Nutrition
    val nutritionToday = nutritionLogList.filter { it.date == LocalDate.now() }

    //  nothing logged for today
    if (caloriesEnabled && nutritionToday.isEmpty()) {
        evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_TODAY, false))
    } else if (caloriesEnabled) {
        var breakfastCount = nutritionToday.filter { it.type == NutritionType.BREAKFAST }.size
        var lunchCount = nutritionToday.filter { it.type == NutritionType.LUNCH }.size
        var dinnerCount = nutritionToday.filter { it.type == NutritionType.DINNER }.size

        if (breakfastCount > 0) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_BREAKFAST, true)) }
        if (lunchCount > 0) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_LUNCH, true)) }
        if (dinnerCount > 0) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_DINNER, true)) }


        if (breakfastCount == 0 && currentHour >= 10) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_BREAKFAST, false)) }
        if (lunchCount == 0 && currentHour >= 15) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_LUNCH, false)) }
        if (breakfastCount == 0 && currentHour >= 20) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_DINNER, false)) }

        if (breakfastCount > 0 && lunchCount > 0 && dinnerCount > 0 && currentHour > 20) {
            var totalCalories = nutritionToday.sumOf { it.calories }
            evaluations.add(Pair(Evaluation.NUTRITION_CALORIE_GOAL, totalCalories <= calorieGoal))
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
    Well done logging your lunch!
    var imageId: Int = ((imageList.size.toFloat() / (approvalMax.toFloat() - approvalMin.toFloat())) * (approval.toFloat() - approvalMin.toFloat())).toInt()

    if (imageId > imageList.size - 1) { imageId = imageList.size - 1 }
    if (imageId < 0) { imageId = 0 }


    ret.approval = approval
    ret.image = imageList[imageId]

    return ret
}