package com.foxden.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val DarkBlue  = Color(0xFF0B2D3D)
val MidBlue = Color(0xFF40759C)
val LightBlue = Color(0xFF86BBD8)
val Yellow = Color(0xFFF6AE2D)
val Orange = Color(0xFFF26419)
//val White = Color(0xFFFFFFFF)
//val Black = Color(0xFF000000)




fun Color.withAlpha(alpha: Float): Color {
    return copy(alpha = alpha)
}