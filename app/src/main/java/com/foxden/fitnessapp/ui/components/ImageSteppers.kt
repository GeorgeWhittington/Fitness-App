package com.foxden.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foxden.fitnessapp.R

/**
 * Component that displays circular steppers for an image slideshow, so that the user
 * knows the total number of images, and where in the slideshow they are
 * @param numImages The number of images
 * @param selectedImage Which image is currently selected
 * @param modifier Modifier that controls where the stepper is aligned
 */
@Composable
fun ImageSteppers(numImages: Int, selectedImage: Int, modifier: Modifier) {
    if (numImages <= 1) {
        return
    }

    Row(modifier = modifier.background(Color.White, RoundedCornerShape(20.dp)).padding(2.dp)) {
        for (index in 0..(numImages - 1)) {
            if (index == selectedImage) {
                Icon(
                    painterResource(R.drawable.filled_circle), null,
                    modifier = Modifier.size(7.dp), tint = Color.Black
                )
            } else {
                Icon(
                    painterResource(R.drawable.stroked_circle), null,
                    modifier = Modifier.size(7.dp), tint = Color.Black
                )
            }
            // Don't add a spacer if this is the last stepper!
            if (index != numImages - 1) {
                Spacer(modifier = Modifier.size(2.dp))
            }
        }
    }
}