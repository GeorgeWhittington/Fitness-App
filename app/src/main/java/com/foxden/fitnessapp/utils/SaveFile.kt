package com.foxden.fitnessapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import io.jenetics.jpx.GPX
import java.io.File
import java.lang.Exception
import java.util.UUID
import kotlin.io.path.Path

fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    val type = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
    val filename = "${UUID.randomUUID()}.${type}"
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return filename
}

fun saveGPXToInternalStorage(context: Context, gpx: GPX): String {
    val dir = File(context.filesDir, "gpx")
    if (!dir.exists()) {
        dir.mkdir()
    }

    val filename = "${UUID.randomUUID()}.gpx"
    try {
        GPX.write(gpx, Path("${dir.path}/${filename}"))
    } catch (e: Exception) {
        Log.e("FIT", "Error writing gpx to file: $e")
    }

    return filename
}