package com.foxden.fitnessapp.utils

fun formatDistance(distance: Float, distanceUnit: Any?): Float {
    return if (distanceUnit == "Km") {
        String.format("%.2f", distance * 1.609).toFloat()
    } else {
        String.format("%.2f", distance).toFloat()
    }
}

fun formatDuration(totalSeconds: Int): String {
    return String.format("%dh %dm", totalSeconds / 3600, (totalSeconds % 3600) / 60)
}