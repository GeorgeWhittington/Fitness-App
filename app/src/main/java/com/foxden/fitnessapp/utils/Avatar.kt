package com.foxden.fitnessapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.data.NutritionLog
import com.foxden.fitnessapp.data.NutritionType
import java.time.LocalDate
import java.time.LocalTime

/* Drawables for each character, stored in negative - positive order */
val FOX_IMAGES = listOf<Int>(       // Fox character - Daniel
    R.drawable.fox_angry,
    R.drawable.fox_sad,
    R.drawable.fox_neutral,
    R.drawable.fox_happy,
)

val RACOON_IMAGES = listOf<Int>(    // Racoon character - Rio
    R.drawable.angryracoon,
    R.drawable.sadracoon,
    R.drawable.racoon,
    R.drawable.happyracoon,
)

val CAT_IMAGES = listOf<Int>(       // Cat character    - George
    R.drawable.hendrix_window,
)

/*  Evaluation

    Enumuation for each criteria the user is assessed when calculating on their progress. 
    Each evaulation has a approval score that is either added or subtracted when calculating the total
    approval as well as positive / negative messages shown to the user

    @param completeText Text shown to the user when they have achieved
    @param uncompleteText Text shown to the user when they have not completed yet
    @param approval The value for this evaluation
*/
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

/*
    Evaluation Result - Data structure sotring final evaluation
 */
data class EvaluationResult (
    var image: Int = R.drawable.fox_happy,
    var message: String = "...",
    var messagePositive: String = "...",
    var messageNegative: String = "...",
    var approval: Int = 0
)

/*
    Evaluate

    Accepts current state and works out how well the user is doing, used to show avatar and messages on the home page. 
*/
@RequiresApi(Build.VERSION_CODES.O)
fun Evaluate(totalActivites: Int, caloriesEnabled: Boolean, calorieGoal: Int, nutritionLogList: List<NutritionLog>, character: String) : EvaluationResult {

    /*  Approval is a score that ranges from -10 to 10
        10 is the best approval
        -10 is the worst */
    val approvalMin: Int = -10
    val approvalMax: Int = 10

    var ret = EvaluationResult();

    // List of each evauluation assesed, and if they completed it or not
    val evaluations = mutableListOf<Pair<Evaluation, Boolean>>()

    // Current hour of the day, used to limit evaluations that are not possible yet
    // eg. expecting lunch to be logged at 9am
    val currentHour = LocalTime.now().hour

    /* Activity evaluation */

    // Activity awarded if the user has logged something or not 
    if (totalActivites > 0) {
        evaluations.add(Pair(Evaluation.ACTIVE, true))
    } else if (totalActivites == 0 && currentHour >= 20) {
        evaluations.add(Pair(Evaluation.ACTIVE, false))
    }

    /* Nutrition evaluation */

    // List of nutrition logs passed in, filtered to only account for today
    val nutritionToday = nutritionLogList.filter { it.date == LocalDate.now() }

    if (caloriesEnabled && nutritionToday.isEmpty()) {  // if the user wants to track nutrition and they havent logged anything
        evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_TODAY, false))
    } else if (caloriesEnabled) {

        // count nutrition logs for each type
        var breakfastCount = nutritionToday.filter { it.type == NutritionType.BREAKFAST }.size
        var lunchCount = nutritionToday.filter { it.type == NutritionType.LUNCH }.size
        var dinnerCount = nutritionToday.filter { it.type == NutritionType.DINNER }.size

        // award good evaulation if the user has logged something 
        if (breakfastCount > 0) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_BREAKFAST, true)) }
        if (lunchCount > 0) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_LUNCH, true)) }
        if (dinnerCount > 0) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_DINNER, true)) }

        // award negatie evaluation if the user has not logged something by a certain time
        if (breakfastCount == 0 && currentHour >= 10) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_BREAKFAST, false)) }
        if (lunchCount == 0 && currentHour >= 15) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_LUNCH, false)) }
        if (breakfastCount == 0 && currentHour >= 20) { evaluations.add(Pair(Evaluation.NUTRITION_LOGGED_DINNER, false)) }

        // award positive/negative evauluation for the calories logged vs calorie goal
        if (breakfastCount > 0 && lunchCount > 0 && dinnerCount > 0 && currentHour > 20) {
            var totalCalories = nutritionToday.sumOf { it.calories }
            evaluations.add(Pair(Evaluation.NUTRITION_CALORIE_GOAL, totalCalories <= calorieGoal))
        }
    }

    // calculate final approval score
    var approval: Int = 0

    evaluations.sortBy { it.second }
    evaluations.sortBy { it.first.approval }

    // sum up approval for each evaulation in list, subtracting if the user has not completed
    evaluations.forEach{
        if (it.second) { approval += it.first.approval }
        else {
            approval += -it.first.approval
        }
    }

    // clamp approval to range -10, 10
    if (approval < -10) { approval = -10 }
    if (approval > 10) { approval = 10 }

    if (evaluations.size > 0) {
        if (evaluations[0].second) {
            ret.message = evaluations[0].first.completeText
        } else {
            ret.message = evaluations[0].first.uncompleteText
            }
        
        // lists of filtered evaluations, positive & negative
        val positiveEvaluations = evaluations.filter { it.second == true }
        val negativeEvaluations = evaluations.filter { it.second == false }

        // work out positive message for user
        if (positiveEvaluations.size > 0) {
            ret.messagePositive = positiveEvaluations.sortedBy { it.first.approval }.last().first.completeText
        }

        // work out negative message for user
        if (negativeEvaluations.size > 0) {
            ret.messageNegative = negativeEvaluations.sortedBy { it.first.approval }.first().first.uncompleteText
        }

    } else { // if we have no evaulations then we have nothing to say, should not really happen
        ret.message = "..."
        ret.messagePositive = ""
        ret.messageNegative = ""
    }

    // work out what image to show

    var imageList = FOX_IMAGES // default to fox images

    // update list depending on which character is selected
    if (character == "Racoon") { imageList = RACOON_IMAGES  }
    else if (character == "Cat") { imageList = CAT_IMAGES }

    // map the approval (-10 -> 10) to specific image id depending on how many images each character has
    var imageId: Int = ((imageList.size.toFloat() / (approvalMax.toFloat() - approvalMin.toFloat())) * (approval.toFloat() - approvalMin.toFloat())).toInt()
    
    // ensure image id is valid just in case
    if (imageId > imageList.size - 1) { imageId = imageList.size - 1 }
    if (imageId < 0) { imageId = 0 }


    // return fina; evaulation result
    ret.approval = approval
    ret.image = imageList[imageId]
    return ret
}