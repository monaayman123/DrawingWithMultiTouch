package com.example.drawingwithmultitouch.ui.drawingViewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawingsUiState (
    val selectedColor: Color = Color.Black,
    val activePaths: Map<Int, PathData> = emptyMap(),
    val finishedPaths: List<PathData> = emptyList()

)
data class PathData(
    val id: Int,
    val color: Color,
    val path: List<Offset>,
    val thickness: Float = 10f
)
val allColors = listOf(
    Color.Black,
    Color.Blue,
    Color.Red,
    Color.Green,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan,
    Color.White,
    Color.LightGray,
    Color.DarkGray
)
