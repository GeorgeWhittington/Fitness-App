package com.foxden.fitnessapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ZoomOutMap
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// TODO: Check over my styling here, I don't think the fullscreen image viewer will look good on dark mode
@Composable
fun AddActivityAttachmentsWidget(
    imageURIs: MutableList<Uri>, addImageUri: (uri: Uri) -> Unit,
    removeImageUri: (uriIndex: Int) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        for (uri in it) { addImageUri(uri) }
    }

    var imageDisplayed by remember { mutableIntStateOf(0) }
    var fullscreenImageExpanded by remember { mutableStateOf(false) }
    var fullscreenImageUri: Uri? by remember { mutableStateOf(null) }

    if (fullscreenImageExpanded) {
        fullscreenImageUri?.let {imageURI ->
            FullscreenImageDialog(
                onDismiss = { fullscreenImageExpanded = false },
                imageURI = imageURI, contentDescription = null
            )
        }
    }

    Row (
        modifier = Modifier
            .padding(TextFieldDefaults.contentPaddingWithLabel(start = 0.dp, end = 0.dp))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // when there are no images, this is the placeholder
        if (imageURIs.size == 0) {
            Box (
                modifier = Modifier
                    .height(200.dp)
                    .weight(1f)
                    .background(Color.Gray, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Image,
                    "No images attached",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        } else {
            Box (
                modifier = Modifier
                    .height(200.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface), RoundedCornerShape(20.dp))
                    .clickable {
                        if (imageDisplayed + 1 == imageURIs.size)
                            imageDisplayed = 0
                        else
                            imageDisplayed++
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageURIs[imageDisplayed]),
                    contentDescription = "Attached image ${imageDisplayed + 1}/${imageURIs.size}",
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth()
                )
                ImageSteppers(
                    numImages = imageURIs.size, selectedImage = imageDisplayed,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp)
                )
                IconButton(
                    onClick = {
                        fullscreenImageUri = imageURIs[imageDisplayed]
                        fullscreenImageExpanded = true
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .background(Color.White, CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Outlined.ZoomOutMap, "View Photo in full",
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            IconButton(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .size(56.dp),
                onClick = { galleryLauncher.launch("image/*") }
            ) {
                Icon(
                    Icons.Outlined.Add, "Add photo(s) to activity",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(24.dp)
                )
            }

            if (imageURIs.size != 0) {
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.error, CircleShape)
                        .size(56.dp),
                    onClick = {
                        removeImageUri(imageDisplayed)
                        imageDisplayed = 0.coerceAtLeast(imageDisplayed - 1)
                    }
                ) {
                    Icon(
                        Icons.Outlined.Delete, "Remove photo from activity",
                        tint = Color.White, modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FullscreenImageDialog(onDismiss: () -> Unit, imageURI: Uri, contentDescription: String?) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onDismiss() }) {
            Text("Close")
        } },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageURI),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}